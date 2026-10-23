package com.smaranam.app.content

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.LocalDate

/**
 * The only way screens read content. Reads app/src/main/assets/content (built by tools/build_content.py).
 * Small files are cached after first read; per-item files (pujas, texts, stories, calendar years) load on demand.
 * Debug builds show drafts with a small "draft" chip; release builds never contain drafts.
 */
class ContentRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val mutex = Mutex()
    private val cache = mutableMapOf<String, Any>()

    private suspend fun <T : Any> load(path: String, strategy: DeserializationStrategy<T>): T =
        mutex.withLock {
            @Suppress("UNCHECKED_CAST")
            cache[path] as T? ?: withContext(Dispatchers.IO) {
                val text = context.assets.open("content/$path").bufferedReader(Charsets.UTF_8).use { it.readText() }
                json.decodeFromString(strategy, text)
            }.also { cache[path] = it }
        }

    private suspend fun <T : Any> loadOrNull(path: String, strategy: DeserializationStrategy<T>): T? =
        runCatching { load(path, strategy) }.getOrNull()

    // ---------- whole-file lists ----------
    suspend fun manifest() = load("manifest.json", Manifest.serializer())
    suspend fun master() = load("master.json", Master.serializer())
    suspend fun deities() = load("deities.json", ListSerializer(Deity.serializer()))
    suspend fun mantras() = load("mantras.json", ListSerializer(Mantra.serializer()))
    suspend fun samagri() = load("samagri.json", ListSerializer(SamagriItem.serializer()))
    suspend fun naivedyam() = load("naivedyam.json", ListSerializer(Naivedyam.serializer()))
    suspend fun observances() = load("observances.json", ListSerializer(Observance.serializer()))
    suspend fun rashi() = load("rashi.json", RashiContent.serializer())
    suspend fun pujaIndex() = load("pujas.json", ListSerializer(PujaSummary.serializer()))
    suspend fun textIndex() = load("texts.json", ListSerializer(NaamaluTextSummary.serializer()))
    suspend fun storyIndex() = load("stories.json", ListSerializer(StorySummary.serializer()))
    suspend fun images() = load("images.json", MapSerializer(String.serializer(), ImageEntry.serializer()))

    // ---------- single items ----------
    suspend fun deity(id: String) = deities().firstOrNull { it.id == id }
    suspend fun mantra(id: String) = mantras().firstOrNull { it.id == id }
    suspend fun samagriItem(id: String) = samagri().firstOrNull { it.id == id }
    suspend fun naivedyamItem(id: String) = naivedyam().firstOrNull { it.id == id }
    suspend fun observance(id: String) = observances().firstOrNull { it.id == id }
    suspend fun puja(id: String) = loadOrNull("pujas/$id.json", Puja.serializer())
    suspend fun text(id: String) = loadOrNull("texts/$id.json", NaamaluText.serializer())
    suspend fun story(id: String) = loadOrNull("stories/$id.json", Story.serializer())

    /** Asset path for an image id, or null → caller shows the design-system fallback. */
    suspend fun imagePath(id: String?): String? = id?.let { images()[it]?.file }?.let { "content/$it" }

    // ---------- per-deity lists ----------
    suspend fun pujasFor(deity: String, state: String) =
        pujaIndex().filter { it.deity == deity && ("all" in it.regions || state in it.regions) }
    suspend fun textsFor(deity: String) = textIndex().filter { it.deity == deity }

    /** Texts for this god, then japa mantras that are not already pointed at by a text. */
    suspend fun textsAndMantrasFor(deityId: String): List<Chantable> {
        val texts = textIndex().filter { it.deity == deityId }
        val linked = texts.mapNotNull { it.mantra }.toSet()
        val fromTexts = texts.map { summary ->
            val linkedFile = summary.mantra?.let { mantra(it)?.audio?.file }
            val file = listOf(summary.audio?.file, linkedFile).firstOrNull { hasAssetAudio(it) }
            Chantable(
                id = summary.id,
                deity = summary.deity,
                mantra = summary.unit == "repetition" || summary.type == "mantra",
                type = summary.type,
                unit = summary.unit,
                title = summary.title,
                hasRecording = file != null,
                status = summary.status,
                mantraId = summary.mantra,
            )
        }
        val fromMantras = mantras()
            .filter { it.deity == deityId && it.kind == "mantra" && "japa" in it.use && it.id !in linked }
            .map { mantra ->
                Chantable(
                    id = mantra.id,
                    deity = mantra.deity,
                    mantra = true,
                    type = "mantra",
                    unit = "repetition",
                    sacred = mantra.text,
                    hasRecording = hasAssetAudio(mantra.audio?.file),
                    status = mantra.status,
                    mantraId = mantra.id,
                )
            }
        return fromTexts + fromMantras
    }

    fun hasAssetAudio(file: String?): Boolean {
        if (file.isNullOrBlank()) return false
        return runCatching {
            context.assets.open("content/audio/$file").close()
            true
        }.getOrDefault(false)
    }
    suspend fun storiesFor(deity: String) = storyIndex().filter { it.deity == deity }

    // ---------- calendar ----------
    /** The calendar year file that contains [date] for [cityId], or null if outside the carried years. */
    suspend fun calendarYear(cityId: String, date: LocalDate): CalendarYear? {
        val range = manifest().samvatsaras.firstOrNull {
            !date.isBefore(LocalDate.parse(it.start)) && !date.isAfter(LocalDate.parse(it.end))
        } ?: return null
        return loadOrNull("calendar/$cityId/${range.year}-${range.id}.json", CalendarYear.serializer())
    }

    suspend fun day(cityId: String, date: LocalDate): CalendarDay? =
        calendarYear(cityId, date)?.days?.firstOrNull { it.date == date.toString() }

    /**
     * Home "Coming up": today's observance if it has a puja, else the next day (within [horizonDays])
     * whose observance has a puja. Yearly festivals win over monthly ones on the same day.
     */
    suspend fun comingUp(cityId: String, today: LocalDate, horizonDays: Long = 60): ComingUp? {
        val obs = observances().associateBy { it.id }
        var d = today
        while (!d.isAfter(today.plusDays(horizonDays))) {
            val day = day(cityId, d)
            val pick = day?.observances
                ?.mapNotNull { obs[it] }
                ?.filter { it.links["pujas"].orEmpty().isNotEmpty() }
                ?.sortedBy { if (it.kind == "monthly") 1 else 0 }
                ?.firstOrNull()
            if (day != null && pick != null) return ComingUp(pick, day, daysAway = (d.toEpochDay() - today.toEpochDay()).toInt())
            d = d.plusDays(1)
        }
        return null
    }

    // ---------- rāśi ----------
    /** Today's note for [myRashi] (1–12). Today only — never call with other dates. */
    suspend fun rashiNote(myRashi: Int, today: CalendarDay, lang: Lang): RashiNote? {
        val content = rashi()
        val house = ((today.moonRashi - myRashi + 12) % 12) + 1
        val note = content.gochara.firstOrNull { it.house == house } ?: return null
        val variants = note.variants.pick(lang)
        if (variants.isEmpty()) return null
        val dayOfYear = LocalDate.parse(today.date).dayOfYear
        return RashiNote(house, note.tone, variants[dayOfYear % variants.size], note.suggestText)
    }
}

data class ComingUp(val observance: Observance, val day: CalendarDay, val daysAway: Int)
data class RashiNote(val house: Int, val tone: String, val text: String, val suggestText: String?)
