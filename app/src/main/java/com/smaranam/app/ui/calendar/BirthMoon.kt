package com.smaranam.app.ui.calendar

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.floor
import kotlin.math.sin

/** Nakṣatra (1–27), pada (1–4) and rāśi (1–12) from the Moon. Birth details are not stored here. */
data class StarResult(
    val nakshatra: Int,
    val pada: Int,
    val rashi: Int,
)

data class StarReading(
    val primary: StarResult,
    val also: StarResult?,
)

private val ZONE = ZoneId.of("Asia/Kolkata")

/**
 * Moon's sidereal place at a local Indian civil time.
 * The series is computed on the phone. Nothing about the birth is kept by this function.
 */
fun readStar(local: LocalDateTime): StarReading {
    val primary = place(local)
    val before = place(local.minusMinutes(20))
    val after = place(local.plusMinutes(20))
    val other = listOf(before, after).firstOrNull {
        it.nakshatra != primary.nakshatra || it.pada != primary.pada
    }
    return StarReading(primary, other)
}

private fun place(local: LocalDateTime): StarResult {
    val utc = ZonedDateTime.of(local, ZONE).withZoneSameInstant(ZoneOffset.UTC)
    val jd = julian(utc.year, utc.monthValue, utc.dayOfMonth, utc.hour, utc.minute, utc.second)
    val sidereal = siderealMoon(jd)
    val nakshatra = floor(sidereal / (360.0 / 27.0)).toInt().mod(27) + 1
    val inStar = sidereal % (360.0 / 27.0)
    val pada = floor(inStar / (360.0 / 108.0)).toInt().mod(4) + 1
    val rashi = floor(sidereal / 30.0).toInt().mod(12) + 1
    return StarResult(nakshatra, pada, rashi)
}

private fun julian(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Double {
    var y = year
    var m = month
    val d = day + (hour + minute / 60.0 + second / 3600.0) / 24.0
    if (m <= 2) {
        y -= 1
        m += 12
    }
    val a = y / 100
    val b = 2 - a + a / 4
    return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5
}

private fun siderealMoon(jd: Double): Double {
    val t = (jd - 2451545.0) / 36525.0
    val l = 218.3164477 + 481267.88123421 * t - 0.0015786 * t * t + t * t * t / 538841 - t * t * t * t / 65194000
    val d = wrap(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t + t * t * t / 545868 - t * t * t * t / 113065000)
    val m = wrap(357.5291092 + 35999.0502909 * t - 0.0001536 * t * t + t * t * t / 24490000)
    val mp = wrap(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t + t * t * t / 69699 - t * t * t * t / 14712000)
    val f = wrap(93.2720950 + 483202.0175233 * t - 0.0036539 * t * t - t * t * t / 3526000 + t * t * t * t / 863310000)
    val dr = Math.toRadians(d)
    val mr = Math.toRadians(m)
    val mpr = Math.toRadians(mp)
    val fr = Math.toRadians(f)
    var sum = 0.0
    for (term in TERMS) {
        sum += term[4] * sin(term[0] * dr + term[1] * mr + term[2] * mpr + term[3] * fr)
    }
    val tropical = wrap(l + sum / 1_000_000.0)
    val years = (jd - 2451545.0) / 365.25
    val ayanamsa = 23.853166 + years * (50.29 / 3600.0)
    return wrap(tropical - ayanamsa)
}

private fun wrap(degrees: Double): Double {
    val x = degrees % 360.0
    return if (x < 0) x + 360.0 else x
}

private val TERMS = arrayOf(
    intArrayOf(0, 0, 1, 0, 6288774),
    intArrayOf(2, 0, -1, 0, 1274027),
    intArrayOf(2, 0, 0, 0, 658314),
    intArrayOf(0, 0, 2, 0, 213618),
    intArrayOf(0, 1, 0, 0, -185116),
    intArrayOf(0, 0, 0, 2, -114332),
    intArrayOf(2, 0, -2, 0, 58793),
    intArrayOf(2, -1, -1, 0, 57066),
    intArrayOf(2, 0, 1, 0, 53322),
    intArrayOf(2, -1, 0, 0, 45758),
    intArrayOf(0, 1, -1, 0, -40923),
    intArrayOf(1, 0, 0, 0, -34720),
    intArrayOf(0, 1, 1, 0, -30383),
    intArrayOf(2, 0, 0, -2, 15327),
    intArrayOf(0, 0, 1, 2, -12528),
    intArrayOf(0, 0, 1, -2, 10980),
    intArrayOf(4, 0, -1, 0, 10675),
    intArrayOf(0, 0, 3, 0, 10034),
    intArrayOf(4, 0, -2, 0, 8548),
    intArrayOf(2, 1, -1, 0, -7888),
    intArrayOf(2, 1, 0, 0, -6766),
    intArrayOf(1, 0, -1, 0, -5163),
    intArrayOf(1, 1, 0, 0, 4987),
    intArrayOf(2, -1, 1, 0, 4036),
    intArrayOf(2, 0, 2, 0, 3994),
    intArrayOf(4, 0, 0, 0, 3861),
    intArrayOf(2, 0, -3, 0, 3665),
    intArrayOf(0, 1, -2, 0, -2689),
    intArrayOf(2, -1, -2, 0, -2602),
    intArrayOf(2, -2, -1, 0, 2390),
    intArrayOf(2, 0, 1, -2, 2236),
    intArrayOf(2, -2, 0, 0, -2120),
    intArrayOf(0, 1, 2, 0, -2069),
    intArrayOf(0, 2, 0, 0, 2048),
    intArrayOf(2, -2, -2, 0, -1773),
    intArrayOf(2, 0, -1, 2, -1595),
    intArrayOf(2, 0, 0, 2, 1215),
    intArrayOf(4, -1, -1, 0, -1110),
    intArrayOf(0, 0, 2, 2, -892),
    intArrayOf(3, 0, -1, 0, -810),
    intArrayOf(2, 1, 1, 0, 759),
    intArrayOf(4, -1, -2, 0, -713),
    intArrayOf(0, 2, -1, 0, -700),
    intArrayOf(2, 2, -1, 0, 691),
    intArrayOf(2, 1, -2, 0, 596),
    intArrayOf(2, -1, 0, -2, 549),
    intArrayOf(4, 0, 1, 0, 537),
    intArrayOf(0, 0, 4, 0, 520),
    intArrayOf(4, -1, 0, 0, -487),
    intArrayOf(1, 0, -2, 0, -399),
    intArrayOf(2, 1, 0, -2, 351),
    intArrayOf(0, 0, 2, -2, -340),
    intArrayOf(1, 1, 1, 0, 330),
    intArrayOf(3, 0, -2, 0, -323),
    intArrayOf(2, 2, 0, 0, 299),
    intArrayOf(1, 1, -1, 0, 294),
)
