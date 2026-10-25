package app.sannidhi.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.sannidhi.content.Lang
import app.sannidhi.content.TextView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.settingsDataStore by preferencesDataStore("sannidhi_settings")

data class AppSettings(
    val lang: String = "en",
    val textView: String = TextView.Iast.name,
    val stateCode: String = "",
    val cityId: String = "",
    val name: String = "",
    val rashi: Int? = null,
    val nakshatra: String = "",
    val pada: Int? = null,
    val findNakshatraDismissed: Boolean = false,
    val gotra: String = "",
    val onboarded: Boolean = false,
    val debugToday: String? = null,
    val readToMe: Boolean = true,
    val storyMusic: Boolean = true,
    val debugStorySearch: Boolean = false,
    val handsFree: Boolean = true,
) {
    fun language(): Lang = when (lang) {
        "te" -> Lang.te
        "hi" -> Lang.hi
        else -> Lang.en
    }

    /** Home info line: Telugu script in te, Devanagari in hi, IAST in en. */
    fun infoView(): TextView = when (lang) {
        "te" -> TextView.Script
        "hi" -> TextView.Devanagari
        else -> TextView.Iast
    }

    companion object {
        const val TELUGU_SCRIPT = "Telugu"

        /** Sacred-text script that matches an app language. */
        fun textViewFor(lang: String): String = when (lang) {
            "te" -> TextView.Script.name
            "hi" -> TextView.Devanagari.name
            else -> TextView.Iast.name
        }
    }

    fun textViewEnum(): TextView = when (textView) {
        TextView.Devanagari.name -> TextView.Devanagari
        TextView.Script.name, TELUGU_SCRIPT -> TextView.Script
        else -> TextView.Iast
    }

    fun today(): LocalDate = debugToday?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now()
}

data class GuidedPlace(val index: Int, val at: Long)

data class KathaPlace(val part: Int, val beat: Int, val at: Long)

class AppSettingsStore(private val context: Context) {
    private val dataStore = context.settingsDataStore

    val settings: Flow<AppSettings> = dataStore.data.map { it.toSettings() }

    suspend fun setLang(lang: String) {
        dataStore.edit {
            it[Keys.lang] = lang
            it[Keys.textView] = AppSettings.textViewFor(lang)
        }
    }

    suspend fun setTextView(view: String) {
        dataStore.edit { it[Keys.textView] = view }
    }

    suspend fun setPlace(stateCode: String, cityId: String) {
        dataStore.edit {
            it[Keys.state] = stateCode
            it[Keys.city] = cityId
        }
    }

    suspend fun setName(name: String) {
        dataStore.edit { it[Keys.name] = name }
    }

    suspend fun setRashi(rashi: Int?) {
        dataStore.edit {
            if (rashi == null) it.remove(Keys.rashi) else it[Keys.rashi] = rashi
        }
    }

    suspend fun setNakshatra(value: String) {
        dataStore.edit {
            it[Keys.nakshatra] = value
            it.remove(Keys.pada)
        }
    }

    /** Nakṣatra, pada and rāśi only. Birth date, time and place are not written. */
    suspend fun setFoundStar(nakshatraId: String, pada: Int, rashi: Int) {
        dataStore.edit {
            it[Keys.nakshatra] = nakshatraId
            it[Keys.pada] = pada
            it[Keys.rashi] = rashi
        }
    }

    suspend fun setFindNakshatraDismissed(value: Boolean) {
        dataStore.edit { it[Keys.findNakshatraDismissed] = value }
    }

    suspend fun setGotra(value: String) {
        dataStore.edit { it[Keys.gotra] = value }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        dataStore.edit { it[Keys.onboarded] = onboarded }
    }

    suspend fun setDebugToday(isoDate: String?) {
        dataStore.edit {
            if (isoDate.isNullOrBlank()) it.remove(Keys.debugToday) else it[Keys.debugToday] = isoDate
        }
    }

    /** Little ones or older children, remembered per story. */
    fun storyAudience(storyId: String): Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.storyAudience].orEmpty()
            .firstOrNull { it.startsWith("$storyId=") }
            ?.substringAfter("=")
            ?.takeIf { it == AUDIENCE_LITTLE || it == AUDIENCE_OLDER }
    }

    suspend fun setStoryAudience(storyId: String, audience: String) {
        dataStore.edit { prefs ->
            val kept = prefs[Keys.storyAudience].orEmpty()
                .filterNot { it.startsWith("$storyId=") }
                .toMutableSet()
            kept.add("$storyId=$audience")
            prefs[Keys.storyAudience] = kept
        }
    }

    fun sankalpamTab(): Flow<String> = dataStore.data.map { it[Keys.sankalpamTab] ?: "japam" }

    suspend fun setSankalpamTab(tab: String) {
        dataStore.edit { it[Keys.sankalpamTab] = tab }
    }

    suspend fun setReadToMe(on: Boolean) {
        dataStore.edit { it[Keys.readToMe] = on }
    }

    suspend fun setStoryMusic(on: Boolean) {
        dataStore.edit { it[Keys.storyMusic] = on }
    }

    suspend fun setDebugStorySearch(on: Boolean) {
        dataStore.edit { it[Keys.debugStorySearch] = on }
    }

    fun kathaPlace(): Flow<KathaPlace?> = dataStore.data.map { prefs ->
        val raw = prefs[Keys.kathaPlace] ?: return@map null
        val parts = raw.split("|")
        if (parts.size != 3) return@map null
        val part = parts[0].toIntOrNull()
        val beat = parts[1].toIntOrNull()
        val at = parts[2].toLongOrNull()
        if (part == null || beat == null || at == null) null else KathaPlace(part, beat, at)
    }

    suspend fun setKathaPlace(part: Int, beat: Int) {
        dataStore.edit { it[Keys.kathaPlace] = "$part|$beat|${System.currentTimeMillis()}" }
    }

    suspend fun clearKathaPlace() {
        dataStore.edit { it.remove(Keys.kathaPlace) }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    /** Samagri ticks for this puja on this day. Older days are dropped. */
    fun samagriTicks(pujaId: String, date: String): Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.samagriTicks].orEmpty().mapNotNull { raw ->
            val parts = raw.split("|", limit = 3)
            if (parts.size == 3 && parts[0] == pujaId && parts[1] == date) parts[2] else null
        }.toSet()
    }

    suspend fun clearSamagriTicks(pujaId: String, date: String) {
        dataStore.edit { prefs ->
            prefs[Keys.samagriTicks] = prefs[Keys.samagriTicks].orEmpty().filter { raw ->
                val parts = raw.split("|", limit = 3)
                !(parts.size == 3 && parts[0] == pujaId && parts[1] == date)
            }.toSet()
        }
    }

    suspend fun setSamagriTick(pujaId: String, date: String, itemId: String, ticked: Boolean) {
        dataStore.edit { prefs ->
            val kept = prefs[Keys.samagriTicks].orEmpty().filter { raw ->
                val parts = raw.split("|", limit = 3)
                parts.size == 3 && parts[1] == date
            }.toMutableSet()
            val key = "$pujaId|$date|$itemId"
            if (ticked) kept.add(key) else kept.remove(key)
            prefs[Keys.samagriTicks] = kept
        }
    }

    fun guidedPlace(pujaId: String): Flow<GuidedPlace?> = dataStore.data.map { prefs ->
        prefs[Keys.guidedPlaces].orEmpty().firstNotNullOfOrNull { raw ->
            val parts = raw.split("|")
            if (parts.size == 3 && parts[0] == pujaId) {
                val index = parts[1].toIntOrNull()
                val at = parts[2].toLongOrNull()
                if (index != null && at != null) GuidedPlace(index, at) else null
            } else {
                null
            }
        }
    }

    suspend fun setHandsFree(on: Boolean) {
        dataStore.edit { it[Keys.handsFree] = on }
    }

    suspend fun setGuidedPlace(pujaId: String, index: Int) {
        dataStore.edit { prefs ->
            val kept = prefs[Keys.guidedPlaces].orEmpty().filter { !it.startsWith("$pujaId|") }.toMutableSet()
            kept.add("$pujaId|$index|${System.currentTimeMillis()}")
            prefs[Keys.guidedPlaces] = kept
        }
    }

    private fun Preferences.toSettings(): AppSettings {
        val rashi = this[Keys.rashi]
        return AppSettings(
            lang = this[Keys.lang] ?: "en",
            textView = this[Keys.textView] ?: TextView.Iast.name,
            stateCode = this[Keys.state] ?: "",
            cityId = this[Keys.city] ?: "",
            name = this[Keys.name] ?: "",
            rashi = rashi?.takeIf { it in 1..12 },
            nakshatra = this[Keys.nakshatra] ?: "",
            pada = this[Keys.pada]?.takeIf { it in 1..4 },
            findNakshatraDismissed = this[Keys.findNakshatraDismissed] ?: false,
            gotra = this[Keys.gotra] ?: "",
            onboarded = this[Keys.onboarded] ?: false,
            debugToday = this[Keys.debugToday],
            readToMe = this[Keys.readToMe] ?: true,
            storyMusic = this[Keys.storyMusic] ?: true,
            debugStorySearch = this[Keys.debugStorySearch] ?: false,
            handsFree = this[Keys.handsFree] ?: true,
        )
    }

    private object Keys {
        val lang = stringPreferencesKey("lang")
        val textView = stringPreferencesKey("text_view")
        val state = stringPreferencesKey("state_code")
        val city = stringPreferencesKey("city_id")
        val name = stringPreferencesKey("name")
        val rashi = intPreferencesKey("rashi")
        val nakshatra = stringPreferencesKey("nakshatra")
        val pada = intPreferencesKey("pada")
        val findNakshatraDismissed = booleanPreferencesKey("find_nakshatra_dismissed")
        val gotra = stringPreferencesKey("gotra")
        val onboarded = booleanPreferencesKey("onboarded")
        val debugToday = stringPreferencesKey("debug_today")
        val samagriTicks = stringSetPreferencesKey("samagri_ticks")
        val storyAudience = stringSetPreferencesKey("story_audience")
        val sankalpamTab = stringPreferencesKey("sankalpam_tab")
        val readToMe = booleanPreferencesKey("read_to_me")
        val storyMusic = booleanPreferencesKey("story_music")
        val debugStorySearch = booleanPreferencesKey("debug_story_search")
        val kathaPlace = stringPreferencesKey("katha_place")
        val guidedPlaces = stringSetPreferencesKey("guided_places")
        val handsFree = booleanPreferencesKey("hands_free")
    }

    companion object {
        const val AUDIENCE_LITTLE = "little"
        const val AUDIENCE_OLDER = "older"
    }
}
