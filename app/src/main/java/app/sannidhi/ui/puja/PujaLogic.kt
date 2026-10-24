package app.sannidhi.ui.puja

import app.sannidhi.content.CalendarDay
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Deity
import app.sannidhi.content.Lang
import app.sannidhi.content.Labels
import app.sannidhi.content.Master
import app.sannidhi.content.PujaSummary
import app.sannidhi.content.Sacred
import app.sannidhi.content.TextView
import app.sannidhi.settings.AppSettings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Absolute mantra script chosen by the puja chips. Telugu is stored apart from "my script". */
enum class MantraScript { Telugu, Devanagari, Iast }

fun mantraScript(settings: AppSettings): MantraScript = when (settings.textView) {
    AppSettings.TELUGU_SCRIPT -> MantraScript.Telugu
    TextView.Devanagari.name -> MantraScript.Devanagari
    TextView.Iast.name -> MantraScript.Iast
    else -> when (settings.lang) {
        "te" -> MantraScript.Telugu
        "hi" -> MantraScript.Devanagari
        else -> MantraScript.Iast
    }
}

fun mantraStorage(script: MantraScript): String = when (script) {
    MantraScript.Telugu -> AppSettings.TELUGU_SCRIPT
    MantraScript.Devanagari -> TextView.Devanagari.name
    MantraScript.Iast -> TextView.Iast.name
}

fun mantraView(script: MantraScript): TextView = when (script) {
    MantraScript.Telugu -> TextView.Script
    MantraScript.Devanagari -> TextView.Devanagari
    MantraScript.Iast -> TextView.Iast
}

/** Telugu chip always reads the Telugu line, even when the app language is Hindi or English. */
fun displayMantra(sacred: Sacred, script: MantraScript, lang: Lang, values: Map<String, String>): String {
    val template = when (script) {
        MantraScript.Telugu -> sacred.te
        MantraScript.Devanagari -> sacred.sa
        MantraScript.Iast -> sacred.iast
    }
    return if ('{' in template) fillPlaceholders(template, values) else template
}

/**
 * Replaces `{key}` with its value. A missing value removes the placeholder and the next word
 * (the suffix, such as गोत्रः or नामधेयः) so the sentence stays whole.
 */
fun fillPlaceholders(template: String, values: Map<String, String>): String {
    val pattern = Regex("\\{([a-z_]+)\\}")
    val out = StringBuilder()
    var index = 0
    while (true) {
        val match = pattern.find(template, index) ?: break
        out.append(template, index, match.range.first)
        val value = values[match.groupValues[1]].orEmpty().trim()
        if (value.isNotEmpty()) {
            out.append(value)
            index = match.range.last + 1
        } else {
            val rest = template.substring(match.range.last + 1)
            val trimmed = rest.trimStart()
            val spaces = rest.length - trimmed.length
            val word = trimmed.takeWhile { !it.isWhitespace() }
            index = match.range.last + 1 + spaces + word.length
        }
    }
    out.append(template.substring(index))
    return out.toString().replace(Regex("[ \\t]{2,}"), " ").trim()
}

fun placeholderValues(
    master: Master,
    day: CalendarDay?,
    samvatsaraId: String?,
    settings: AppSettings,
    script: MantraScript,
): Map<String, String> {
    val view = mantraView(script)
    val lang = if (script == MantraScript.Telugu) Lang.te else settings.language()
    val values = mutableMapOf<String, String>()
    if (day != null) {
        master.samvatsaras.firstOrNull { it.id == samvatsaraId }?.let {
            values["samvatsara"] = it.name.pick(view, lang)
        }
        values["ayana"] = ayana(day.masa).pick(view, lang)
        master.masams.firstOrNull { it.n == day.masa }?.let { masa ->
            values["ritu"] = ritu(masa.ritu).pick(view, lang)
            values["masa"] = masa.name.pick(view, lang)
        }
        values["paksha"] = paksha(day).pick(view, lang)
        val tithiN = if (day.tithi == 30) 30 else day.tithiInPaksha
        master.tithis.firstOrNull { it.n == tithiN }?.let { values["tithi"] = it.name.pick(view, lang) }
        values["vasara"] = vasara(LocalDate.parse(day.date).dayOfWeek).pick(view, lang)
        master.nakshatras.firstOrNull { it.n == day.nakshatra }?.let {
            values["nakshatra"] = it.name.pick(view, lang)
        }
    }
    master.states.firstOrNull { it.code == settings.stateCode }?.deshaClause?.pick(view, lang)
        ?.takeIf { it.isNotBlank() }
        ?.let { values["desha_clause"] = it }
    settings.gotra.trim().takeIf { it.isNotEmpty() }?.let { values["gotra"] = it }
    settings.name.trim().takeIf { it.isNotEmpty() }?.let { values["name"] = it }
    return values
}

fun deityScriptName(deity: Deity, lang: Lang): String =
    deity.nameCommon?.pick(lang)?.takeIf { it.isNotBlank() }
        ?: deity.name?.pick(if (lang == Lang.te) TextView.Script else if (lang == Lang.hi) TextView.Devanagari else TextView.Iast, lang)
        ?: deity.id

fun deityEnglishName(deity: Deity): String =
    deity.nameCommon?.en?.takeIf { it.isNotBlank() } ?: deityScriptName(deity, Lang.en)

fun searchParts(deity: Deity): List<String> = buildList {
    deity.name?.let { add(it.sa); add(it.iast); add(it.te) }
    deity.nameCommon?.let { add(it.te.orEmpty()); add(it.hi.orEmpty()); add(it.en.orEmpty()) }
    deity.alsoKnownAs.forEach { add(it.sa); add(it.iast); add(it.te) }
}

fun searchParts(puja: PujaSummary): List<String> = buildList {
    puja.title?.let { add(it.te.orEmpty()); add(it.hi.orEmpty()); add(it.en.orEmpty()) }
    add(puja.kind)
}

fun matchesQuery(query: String, parts: List<String>): Boolean {
    val q = query.trim()
    if (q.isEmpty()) return true
    return parts.any { it.contains(q, ignoreCase = true) }
}

suspend fun daysOnOrAfter(repository: ContentRepository, cityId: String, from: LocalDate): List<CalendarDay> {
    if (cityId.isBlank()) return emptyList()
    val first = repository.calendarYear(cityId, from) ?: return emptyList()
    val out = first.days.filter { it.date >= from.toString() }.toMutableList()
    val next = runCatching { LocalDate.parse(first.end).plusDays(1) }.getOrNull()
    if (next != null) repository.calendarYear(cityId, next)?.let { out += it.days }
    return out
}

fun nextObservance(days: List<CalendarDay>, observanceId: String?): CalendarDay? {
    if (observanceId.isNullOrBlank()) return null
    return days.firstOrNull { observanceId in it.observances }
}

fun formatDayLine(master: Master, day: CalendarDay, settings: AppSettings): String {
    val lang = settings.language()
    val view = settings.infoView()
    val locale = when (settings.lang) {
        "te" -> Locale.forLanguageTag("te")
        "hi" -> Locale.forLanguageTag("hi")
        else -> Locale.ENGLISH
    }
    val date = LocalDate.parse(day.date).format(DateTimeFormatter.ofPattern("EEE d MMM", locale))
    val tithi = Labels.tithi(master, day, view, lang)
    return "$date · $tithi"
}

private fun paksha(day: CalendarDay) = Sacred(
    if (day.isShukla) "शुक्ल" else "कृष्ण",
    if (day.isShukla) "śukla" else "kṛṣṇa",
    if (day.isShukla) "శుక్ల" else "కృష్ణ",
)

/** Uttarāyaṇa for māsa 10–12 and 1–3 (Makara through Mithuna); otherwise dakṣiṇāyana. */
private fun ayana(masa: Int): Sacred = if (masa in 10..12 || masa in 1..3) {
    Sacred("उत्तरायण", "uttarāyaṇa", "ఉత్తరాయణ")
} else {
    Sacred("दक्षिणायन", "dakṣiṇāyana", "దక్షిణాయణ")
}

private fun ritu(id: String): Sacred = when (id) {
    "vasanta" -> Sacred("वसन्त", "vasanta", "వసంత")
    "grishma" -> Sacred("ग्रीष्म", "grīṣma", "గ్రీష్మ")
    "varsha" -> Sacred("वर्षा", "varṣā", "వర్ష")
    "sharad" -> Sacred("शरद्", "śarad", "శరద్")
    "hemanta" -> Sacred("हेमन्त", "hemanta", "హేమంత")
    "shishira" -> Sacred("शिशिर", "śiśira", "శిశిర")
    else -> Sacred(id, id, id)
}

private fun vasara(day: DayOfWeek): Sacred = when (day) {
    DayOfWeek.SUNDAY -> Sacred("भानु", "bhānu", "భాను")
    DayOfWeek.MONDAY -> Sacred("इन्दु", "indu", "ఇన్దు")
    DayOfWeek.TUESDAY -> Sacred("भौम", "bhauma", "భౌమ")
    DayOfWeek.WEDNESDAY -> Sacred("सौम्य", "saumya", "సౌమ్య")
    DayOfWeek.THURSDAY -> Sacred("गुरु", "guru", "గురు")
    DayOfWeek.FRIDAY -> Sacred("भृगु", "bhṛgu", "భృగు")
    DayOfWeek.SATURDAY -> Sacred("स्थिर", "sthira", "స్థిర")
}
