package app.sannidhi.ui.calendar

import app.sannidhi.content.CalendarDay
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Lang
import app.sannidhi.content.Labels
import app.sannidhi.content.Master
import app.sannidhi.content.Sacred
import app.sannidhi.settings.AppSettings
import app.sannidhi.ui.puja.MantraScript
import app.sannidhi.ui.puja.mantraView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

fun appLocale(lang: Lang): Locale = when (lang) {
    Lang.te -> Locale.forLanguageTag("te-IN")
    Lang.hi -> Locale.forLanguageTag("hi-IN")
    Lang.en -> Locale.forLanguageTag("en-IN")
}

/** Monday or Sunday, whichever this language's locale uses. */
fun weekStart(lang: Lang): DayOfWeek = WeekFields.of(appLocale(lang)).firstDayOfWeek

/** Telugu chip always reads the Telugu line, even when the app language is Hindi or English. */
private fun nameLang(script: MantraScript, app: Lang): Lang =
    if (script == MantraScript.Telugu) Lang.te else app

fun sacredName(sacred: Sacred, script: MantraScript, app: Lang): String =
    sacred.pick(mantraView(script), nameLang(script, app))

fun tithiName(master: Master, day: CalendarDay, script: MantraScript, app: Lang): String =
    Labels.tithi(master, day, mantraView(script), nameLang(script, app))

fun masaName(master: Master, day: CalendarDay, script: MantraScript, app: Lang): String =
    Labels.masa(master, day, mantraView(script), nameLang(script, app))

fun nakshatraName(master: Master, day: CalendarDay, script: MantraScript, app: Lang): String =
    Labels.nakshatra(master, day, mantraView(script), nameLang(script, app))

fun rashiName(master: Master, n: Int, script: MantraScript, app: Lang): String =
    Labels.rashi(master, n, mantraView(script), nameLang(script, app))

/** "9:32 pm" from a calendar end field such as 2026-10-11T21:32. Sunrise stays HH:mm. */
fun clock12(raw: String?, lang: Lang): String? {
    if (raw.isNullOrBlank()) return null
    val timePart = raw.substringAfter('T', raw).take(5)
    val time = runCatching { LocalTime.parse(timePart) }.getOrNull() ?: return null
    val hour12 = if (time.hour % 12 == 0) 12 else time.hour % 12
    val hm = "$hour12:${time.minute.toString().padStart(2, '0')}"
    val afternoon = time.hour >= 12
    val half = when (lang) {
        Lang.en -> if (afternoon) "pm" else "am"
        Lang.hi -> if (afternoon) "अपराह्न" else "पूर्वाह्न"
        Lang.te -> if (afternoon) "సాయంత్రం" else "ఉదయం"
    }
    return "$hm $half"
}

fun civilDate(date: LocalDate, lang: Lang): String =
    date.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", appLocale(lang)))

fun civilMonth(month: YearMonth, lang: Lang): String =
    month.atDay(1).format(DateTimeFormatter.ofPattern("MMMM yyyy", appLocale(lang)))

fun weekdayShort(day: DayOfWeek, lang: Lang): String {
    // 4 Jan 2026 is a Sunday. Sunday's DayOfWeek value is 7, so value % 7 lands on that Sunday.
    val sunday = LocalDate.of(2026, 1, 4)
    val target = sunday.plusDays((day.value % 7).toLong())
    return target.format(DateTimeFormatter.ofPattern("EEE", appLocale(lang)))
}

/** Leading blank cells so day 1 sits under the locale's first weekday. */
fun leadingBlanks(month: YearMonth, start: DayOfWeek): Int {
    val first = month.atDay(1).dayOfWeek
    return (first.value - start.value + 7) % 7
}

data class CarriedRange(val start: LocalDate, val end: LocalDate)

fun monthsCovering(range: CarriedRange): List<YearMonth> {
    val out = mutableListOf<YearMonth>()
    var month = YearMonth.from(range.start)
    val last = YearMonth.from(range.end)
    while (!month.isAfter(last)) {
        out += month
        month = month.plusMonths(1)
    }
    return out
}

suspend fun carriedRange(repository: ContentRepository): CarriedRange? {
    val spans = repository.manifest().samvatsaras
    if (spans.isEmpty()) return null
    val start = spans.minOf { LocalDate.parse(it.start) }
    val end = spans.maxOf { LocalDate.parse(it.end) }
    return CarriedRange(start, end)
}

/** This day plus the days beside it, so a good time can use the night before and the next sunrise. */
suspend fun daysTouching(repository: ContentRepository, cityId: String, date: LocalDate): List<CalendarDay> {
    if (cityId.isBlank()) return emptyList()
    return listOf(date.minusDays(2), date, date.plusDays(1))
        .mapNotNull { repository.calendarYear(cityId, it) }
        .distinctBy { it.start }
        .flatMap { it.days }
}

/**
 * "Early morning sādhana · 4:24–5:12 am".
 * When [openAsTill] is set, a window that is open now reads "till 5:12 am".
 */
fun goodPhrase(
    window: GoodWindow,
    now: LocalDateTime,
    lang: Lang,
    label: String,
    tillPattern: String,
    rangePattern: String,
    openAsTill: Boolean = false,
): String {
    val start = clock12(window.start.toString(), lang).orEmpty()
    val end = clock12(window.end.toString(), lang).orEmpty()
    val locale = appLocale(lang)
    val whenText = if (openAsTill && underway(window, now)) {
        java.lang.String.format(locale, tillPattern, end)
    } else {
        java.lang.String.format(locale, rangePattern, start, end)
    }
    return "$label · $whenText"
}

/** Days of this civil month, read from the calendar files. Empty when the city has no file. */
suspend fun daysInMonth(repository: ContentRepository, cityId: String, month: YearMonth): List<CalendarDay> {
    if (cityId.isBlank()) return emptyList()
    val start = month.atDay(1)
    val end = month.atEndOfMonth()
    val years = listOfNotNull(
        repository.calendarYear(cityId, start),
        repository.calendarYear(cityId, end),
    ).distinctBy { it.start }
    val prefix = month.toString()
    return years.flatMap { it.days }.filter { it.date.startsWith(prefix) }.distinctBy { it.date }
}

/** First carried day on or after [from] that lists [observanceId]. */
suspend fun nextObservanceDay(
    repository: ContentRepository,
    cityId: String,
    from: LocalDate,
    observanceId: String,
): CalendarDay? {
    val range = carriedRange(repository) ?: return null
    var date = if (from.isBefore(range.start)) range.start else from
    while (!date.isAfter(range.end)) {
        val year = repository.calendarYear(cityId, date) ?: break
        val hit = year.days.firstOrNull { it.date >= date.toString() && observanceId in it.observances }
        if (hit != null) return hit
        val nextStart = runCatching { LocalDate.parse(year.end).plusDays(1) }.getOrNull() ?: break
        date = nextStart
    }
    return null
}

fun samvatsaraName(master: Master, n: Int): Sacred? =
    master.samvatsaras.firstOrNull { it.n == n }?.name

/** Distinct māsa labels in civil-date order, including adhika when the file says so. */
fun masasInOrder(days: List<CalendarDay>): List<CalendarDay> {
    val seen = linkedSetOf<Pair<Int, Boolean>>()
    return days.sortedBy { it.date }.mapNotNull { day ->
        if (seen.add(day.masa to day.adhika)) day else null
    }
}
