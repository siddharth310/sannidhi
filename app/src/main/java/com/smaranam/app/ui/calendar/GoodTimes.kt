package com.smaranam.app.ui.calendar

import com.smaranam.app.content.CalendarDay
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** A good time the devotee may see. Nothing else from the day is named. */
enum class GoodKind { Early, Begin, Today, Lamp }

data class GoodWindow(
    val kind: GoodKind,
    val start: LocalDateTime,
    val end: LocalDateTime,
)

/**
 * Good times for [date] from the city's sunrise and sunset.
 * Windows that overlap a time kept aside are shortened or dropped. Those times are never returned.
 */
fun goodTimes(days: List<CalendarDay>, date: LocalDate): List<GoodWindow> {
    val byDate = days.associateBy { it.date }
    val today = byDate[date.toString()] ?: return emptyList()
    val sunrise = atClock(date, today.sunrise) ?: return emptyList()
    val sunset = atClock(date, today.sunset) ?: return emptyList()
    if (!sunset.isAfter(sunrise)) return emptyList()
    val previous = byDate[date.minusDays(1).toString()]
    val next = byDate[date.plusDays(1).toString()]
    val nextSunrise = next?.let { atClock(date.plusDays(1), it.sunrise) }
        ?: sunrise.plusHours(24)
    val prevSunset = previous?.let { atClock(date.minusDays(1), it.sunset) }
    val dayLength = Duration.between(sunrise, sunset)
    val nightLength = prevSunset?.let { Duration.between(it, sunrise) }?.takeIf { !it.isNegative && !it.isZero }
    val spans = nakshatraSpans(days)

    val blocks = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()
    eighth(sunrise, dayLength, rahuEighth(date.dayOfWeek))?.let { blocks += it }
    eighth(sunrise, dayLength, yamaEighth(date.dayOfWeek))?.let { blocks += it }
    muhurta(sunrise, dayLength, durMuhurta(date.dayOfWeek))?.let { blocks += it }
    spans.forEach { span ->
        ghatiWindow(span, VISH_FROM[span.n - 1], VISH_TO[span.n - 1])?.let { blocks += it }
    }

    val raw = mutableListOf<GoodWindow>()
    if (nightLength != null) {
        raw += GoodWindow(
            GoodKind.Early,
            sunrise.minus(portion(nightLength, 2, 15)),
            sunrise.minus(portion(nightLength, 1, 15)),
        )
    }
    if (date.dayOfWeek != DayOfWeek.WEDNESDAY) {
        raw += GoodWindow(
            GoodKind.Begin,
            sunrise.plus(portion(dayLength, 7, 15)),
            sunrise.plus(portion(dayLength, 8, 15)),
        )
    }
    val dayStart = sunrise
    val dayEnd = nextSunrise
    spans.forEach { span ->
        val window = ghatiWindow(span, AMRITA_FROM[span.n - 1], AMRITA_TO[span.n - 1]) ?: return@forEach
        val clipped = clip(window.first, window.second, dayStart, dayEnd) ?: return@forEach
        raw += GoodWindow(GoodKind.Today, clipped.first, clipped.second)
    }
    val nightAfter = Duration.between(sunset, nextSunrise).takeIf { !it.isNegative && !it.isZero }
    if (nightAfter != null) {
        raw += GoodWindow(
            GoodKind.Lamp,
            sunset.minus(portion(dayLength, 2, 30)),
            sunset.plus(portion(nightAfter, 2, 30)),
        )
    }

    return raw.flatMap { window ->
        subtract(window.start, window.end, blocks).map { (start, end) ->
            GoodWindow(window.kind, start, end)
        }
    }.filter { Duration.between(it.start, it.end).toMinutes() >= 1 }
        .sortedBy { it.start }
}

/** The window underway now, otherwise the next one that has not ended. */
fun shownGood(windows: List<GoodWindow>, now: LocalDateTime): GoodWindow? {
    windows.firstOrNull { !now.isBefore(it.start) && now.isBefore(it.end) }?.let { return it }
    return windows.firstOrNull { now.isBefore(it.end) }
}

fun underway(window: GoodWindow, now: LocalDateTime): Boolean =
    !now.isBefore(window.start) && now.isBefore(window.end)

/** Days whose nakṣatra is a good tārā for [birthNakshatra] (1–27). Other days are not marked. */
fun goodForNakshatra(dayNakshatra: Int, birthNakshatra: Int): Boolean {
    if (birthNakshatra !in 1..27 || dayNakshatra !in 1..27) return false
    val count = (dayNakshatra - birthNakshatra + 27) % 27
    val tara = (count % 9) + 1
    return tara == 2 || tara == 4 || tara == 6 || tara == 8 || tara == 9
}

private data class NakSpan(val n: Int, val start: LocalDateTime, val end: LocalDateTime)

private fun nakshatraSpans(days: List<CalendarDay>): List<NakSpan> {
    val events = days.mapNotNull { day ->
        val raw = day.nakshatraEnds ?: return@mapNotNull null
        val end = runCatching { LocalDateTime.parse(raw) }.getOrNull() ?: return@mapNotNull null
        day.nakshatra to end
    }.distinctBy { it.second }.sortedBy { it.second }
    if (events.isEmpty()) return emptyList()
    val spans = mutableListOf<NakSpan>()
    var start = events.first().second.minusHours(26)
    for ((n, end) in events) {
        if (n in 1..27 && end.isAfter(start)) spans += NakSpan(n, start, end)
        start = end
    }
    return spans
}

private fun atClock(date: LocalDate, hhmm: String): LocalDateTime? {
    val time = runCatching { LocalTime.parse(hhmm.take(5)) }.getOrNull() ?: return null
    return date.atTime(time)
}

private fun portion(total: Duration, numerator: Long, denominator: Long): Duration =
    Duration.ofMillis(total.toMillis() * numerator / denominator)

private fun eighth(sunrise: LocalDateTime, day: Duration, part: Int): Pair<LocalDateTime, LocalDateTime>? {
    if (part !in 1..8) return null
    return sunrise.plus(portion(day, (part - 1).toLong(), 8)) to sunrise.plus(portion(day, part.toLong(), 8))
}

private fun muhurta(sunrise: LocalDateTime, day: Duration, number: Int): Pair<LocalDateTime, LocalDateTime>? {
    if (number !in 1..15) return null
    return sunrise.plus(portion(day, (number - 1).toLong(), 15)) to sunrise.plus(portion(day, number.toLong(), 15))
}

private fun ghatiWindow(span: NakSpan, from: Int, to: Int): Pair<LocalDateTime, LocalDateTime>? {
    val ms = Duration.between(span.start, span.end).toMillis()
    if (ms <= 0 || to <= from) return null
    val start = span.start.plus(Duration.ofMillis(ms * from / 60))
    val end = span.start.plus(Duration.ofMillis(ms * to / 60))
    return start to end
}

private fun clip(
    start: LocalDateTime,
    end: LocalDateTime,
    from: LocalDateTime,
    to: LocalDateTime,
): Pair<LocalDateTime, LocalDateTime>? {
    val a = if (start.isBefore(from)) from else start
    val b = if (end.isAfter(to)) to else end
    return if (b.isAfter(a)) a to b else null
}

private fun subtract(
    start: LocalDateTime,
    end: LocalDateTime,
    blocks: List<Pair<LocalDateTime, LocalDateTime>>,
): List<Pair<LocalDateTime, LocalDateTime>> {
    var parts = listOf(start to end)
    for (block in blocks) {
        parts = parts.flatMap { subtractOne(it, block) }
    }
    return parts.filter { Duration.between(it.first, it.second).toMinutes() >= 1 }
}

private fun subtractOne(
    window: Pair<LocalDateTime, LocalDateTime>,
    block: Pair<LocalDateTime, LocalDateTime>,
): List<Pair<LocalDateTime, LocalDateTime>> {
    val (start, end) = window
    val (blockStart, blockEnd) = block
    if (!blockEnd.isAfter(start) || !blockStart.isBefore(end)) return listOf(window)
    if (!blockStart.isAfter(start) && !blockEnd.isBefore(end)) return emptyList()
    if (blockStart.isAfter(start) && blockEnd.isBefore(end)) {
        return listOf(start to blockStart, blockEnd to end)
    }
    if (!blockStart.isAfter(start)) return listOf(blockEnd to end)
    return listOf(start to blockStart)
}

private fun rahuEighth(day: DayOfWeek): Int = when (day) {
    DayOfWeek.SUNDAY -> 8
    DayOfWeek.MONDAY -> 2
    DayOfWeek.TUESDAY -> 7
    DayOfWeek.WEDNESDAY -> 5
    DayOfWeek.THURSDAY -> 6
    DayOfWeek.FRIDAY -> 4
    DayOfWeek.SATURDAY -> 3
}

private fun yamaEighth(day: DayOfWeek): Int = when (day) {
    DayOfWeek.SUNDAY -> 5
    DayOfWeek.MONDAY -> 4
    DayOfWeek.TUESDAY -> 3
    DayOfWeek.WEDNESDAY -> 2
    DayOfWeek.THURSDAY -> 1
    DayOfWeek.FRIDAY -> 7
    DayOfWeek.SATURDAY -> 6
}

private fun durMuhurta(day: DayOfWeek): Int = when (day) {
    DayOfWeek.SUNDAY -> 14
    DayOfWeek.MONDAY -> 9
    DayOfWeek.TUESDAY -> 4
    DayOfWeek.WEDNESDAY -> 8
    DayOfWeek.THURSDAY -> 6
    DayOfWeek.FRIDAY -> 4
    DayOfWeek.SATURDAY -> 1
}

/** Start ghati of each window, nakṣatra 1..27. Length is the matching end table. */
private val VISH_FROM = intArrayOf(50, 24, 30, 40, 14, 11, 30, 20, 32, 30, 20, 18, 22, 20, 14, 14, 10, 14, 20, 24, 20, 10, 10, 18, 16, 24, 30)
private val VISH_TO = intArrayOf(54, 28, 34, 44, 18, 15, 34, 24, 36, 34, 24, 22, 26, 24, 18, 18, 14, 18, 24, 28, 24, 14, 14, 22, 20, 28, 34)
private val AMRITA_FROM = intArrayOf(42, 48, 54, 52, 38, 35, 54, 44, 56, 54, 44, 43, 45, 44, 38, 38, 34, 38, 44, 48, 44, 34, 34, 43, 40, 48, 54)
private val AMRITA_TO = intArrayOf(46, 52, 58, 56, 42, 39, 58, 48, 60, 58, 48, 46, 49, 48, 42, 42, 38, 42, 48, 52, 48, 38, 38, 46, 44, 52, 58)
