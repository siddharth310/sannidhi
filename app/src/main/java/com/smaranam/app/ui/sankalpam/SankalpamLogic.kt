package com.smaranam.app.ui.sankalpam

import com.smaranam.app.content.Audio
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun indian(n: Int): String {
    val raw = n.toString()
    if (raw.length <= 3) return raw
    val tail = raw.takeLast(3)
    var head = raw.dropLast(3)
    val parts = ArrayDeque<String>()
    while (head.length > 2) {
        parts.addFirst(head.takeLast(2))
        head = head.dropLast(2)
    }
    if (head.isNotEmpty()) parts.addFirst(head)
    return parts.joinToString(",") + "," + tail
}

fun civil(date: LocalDate, lang: String, pattern: String = "EEE d MMM"): String {
    val locale = when (lang) {
        "te" -> Locale.forLanguageTag("te")
        "hi" -> Locale.forLanguageTag("hi")
        else -> Locale.ENGLISH
    }
    return date.format(DateTimeFormatter.ofPattern(pattern, locale))
}

fun lastDay(start: LocalDate, days: Int): LocalDate = start.plusDays((days - 1).coerceAtLeast(0).toLong())

fun dayNumber(start: LocalDate, today: LocalDate): Int =
    (ChronoUnit.DAYS.between(start, today).toInt() + 1).coerceAtLeast(1)

/** Shown count and round. 108 finishes on round 2's label, as the mala turns. */
fun ringFace(total: Int): Pair<Int, Int> {
    if (total <= 0) return 0 to 1
    val shown = if (total % 108 == 0) 108 else total % 108
    val round = total / 108 + 1
    return shown to round
}

fun nameStarts(audio: Audio?, count: Int): List<Long> {
    if (audio == null || count <= 0) return emptyList()
    if (audio.namesMs.isNotEmpty()) return audio.namesMs
    val intro = audio.introMs ?: return emptyList()
    val slot = audio.slotMs ?: return emptyList()
    return List(count) { index -> intro + index * slot }
}

fun litIndex(starts: List<Long>, position: Long): Int {
    var lit = -1
    starts.forEachIndexed { index, start -> if (start <= position) lit = index }
    return lit
}
