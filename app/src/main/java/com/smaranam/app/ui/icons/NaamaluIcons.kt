package com.smaranam.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.smaranam.app.ui.theme.NaamaluModules
import com.smaranam.app.ui.theme.NavInactive
import kotlin.math.cos
import kotlin.math.sin

/** §5 — module icons (48×48 viewBox). */
object NaamaluIcons {
    val Kalasam: ImageVector = icon(
        "Kalasam",
        "M24 17C18 15 13 11 9 9C15 8 21 11 24 17Z" to Color(0xFF6E9A5B),
        "M24 17C30 15 35 11 39 9C33 8 27 11 24 17Z" to Color(0xFF6E9A5B),
        "M24 17C21 12 21 7 24 3C27 7 27 12 24 17Z" to Color(0xFF3E5A34),
        "M19,16 h10 v5 h-10 z" to Color(0xFF7A1F2B),
        "M13 28C13 40 35 40 35 28C35 23 30 20 24 20C18 20 13 23 13 28Z" to Color(0xFFB23A48),
    )

    val Talapatra: ImageVector = icon(
        "Talapatra",
        "M7,13 h34 a3.5,3.5 0 0 1 3.5,3.5 v0 a3.5,3.5 0 0 1 -3.5,3.5 h-34 a3.5,3.5 0 0 1 -3.5,-3.5 v0 a3.5,3.5 0 0 1 3.5,-3.5 z" to Color(0xFF2F7188),
        "M7,21 h34 a3.5,3.5 0 0 1 3.5,3.5 v0 a3.5,3.5 0 0 1 -3.5,3.5 h-34 a3.5,3.5 0 0 1 -3.5,-3.5 v0 a3.5,3.5 0 0 1 3.5,-3.5 z" to Color(0xFF9CC5D1),
        "M7,29 h34 a3.5,3.5 0 0 1 3.5,3.5 v0 a3.5,3.5 0 0 1 -3.5,3.5 h-34 a3.5,3.5 0 0 1 -3.5,-3.5 v0 a3.5,3.5 0 0 1 3.5,-3.5 z" to Color(0xFF2F7188),
    )

    val Mala: ImageVector = run {
        val beadPaths = (0 until 13).flatMap { i ->
            val angle = Math.toRadians(i * (360.0 / 13) - 90)
            val cx = 24f + 13f * cos(angle).toFloat()
            val cy = 20f + 13f * sin(angle).toFloat()
            listOf("M${cx - 2.6f},$cy a2.6,2.6 0 1 0 5.2,0 a2.6,2.6 0 1 0 -5.2,0" to Color(0xFF6E9A5B))
        }.toTypedArray()
        icon(
            "Mala",
            *beadPaths,
            "M22,38 L19.5,46 H28.5 L26,38 Z" to Color(0xFFE0A93B),
            "M24,35 m-3.8,0 a3.8,3.8 0 1 0 7.6,0 a3.8,3.8 0 1 0 -7.6,0" to Color(0xFF3E5A34),
        )
    }

    val AratiAku: ImageVector = icon(
        "AratiAku",
        "M3 32C10 19 38 19 45 32C38 41 10 41 3 32Z" to Color(0xFF6E9A5B),
        "M12,27 a6,6 0 1 0 12,0 a6,6 0 1 0 -12,0" to Color(0xFFE0A93B),
        "M24,26 a6,6 0 1 0 12,0 a6,6 0 1 0 -12,0" to Color(0xFFE0A93B),
        "M18,32 a6,6 0 1 0 12,0 a6,6 0 1 0 -12,0" to Color(0xFFC98A25),
    )

    val Tithi: ImageVector = icon(
        "Tithi",
        "M7,10 h34 a7,7 0 0 1 7,7 v25 a7,7 0 0 1 -7,7 h-34 a7,7 0 0 1 -7,-7 v-25 a7,7 0 0 1 7,-7 z" to Color.White,
        "M7,17 a7,7 0 0 1 7,-7 h20 a7,7 0 0 1 7,7 v3 H7 z" to Color(0xFFC9A67A),
        "M15,6 h3.4 v9 h-3.4 z" to Color(0xFF6B4A2E),
        "M29.6,6 h3.4 v9 h-3.4 z" to Color(0xFF6B4A2E),
        "M24,31 m-7.5,0 a7.5,7.5 0 1 0 15,0 a7.5,7.5 0 1 0 -15,0" to Color(0xFFE0A93B),
    )

    val Diyas: ImageVector = icon(
        "Diyas",
        "M13 29C10 25 13 21 13 18C13 21 16 25 13 29Z" to Color(0xFFE0A93B),
        "M24 26C21 22 24 17 24 14C24 17 27 22 24 26Z" to Color(0xFFE0A93B),
        "M35 29C32 25 35 21 35 18C35 21 38 25 35 29Z" to Color(0xFFE0A93B),
        "M5,31 h16 v7 a0,0 0 0 1 0,0 h-16 z" to Color(0xFF8C3B55),
        "M27,31 h16 v7 h-16 z" to Color(0xFF8C3B55),
        "M16,28 h16 v8 h-16 z" to Color(0xFFE08BA0),
    )

    val Lotus: ImageVector = icon(
        "Lotus",
        "M24 38C18 32 18 20 24 12C30 20 30 32 24 38Z" to Color(0xFFE08BA0),
        "M24 38C16 36 10 28 10 20C18 22 22 30 24 38Z" to Color(0xFFE08BA0).copy(alpha = 0.6f),
        "M24 38C32 36 38 28 38 20C30 22 26 30 24 38Z" to Color(0xFFE08BA0).copy(alpha = 0.6f),
    )

    val Mandapam: ImageVector = icon(
        "Mandapam",
        "M10,40 V22 h28 v18 z" to Color(0xFFE4EEDC),
        "M6,23 L24,8 L42,23 Z" to Color(0xFF6E9A5B),
        "M24,32 m-4,0 a4,4 0 1 0 8,0 a4,4 0 1 0 -8,0" to Color(0xFFB23A48),
    )

    val Basket: ImageVector = icon(
        "Basket",
        "M8,20 h32 l-3,20 a3,3 0 0 1 -3,2 H14 a3,3 0 0 1 -3,-2 z" to Color(0xFFC9A67A),
        "M14,17 a4,4 0 1 0 8,0 a4,4 0 1 0 -8,0" to Color(0xFFE08BA0),
        "M21.5,15 a4.5,4.5 0 1 0 9,0 a4.5,4.5 0 1 0 -9,0" to Color(0xFFE0A93B),
    )

    val Rings: ImageVector = icon(
        "Rings",
        "M24,24 m-18,0 a18,18 0 1 0 36,0 a18,18 0 1 0 -36,0" to Color(0xFF2F7188),
        "M24,24 m-2.4,0 a2.4,2.4 0 1 0 4.8,0 a2.4,2.4 0 1 0 -4.8,0" to Color(0xFFE0A93B),
    )

    val Scroll: ImageVector = icon(
        "Scroll",
        "M12,10 h24 v28 h-24 z" to Color(0xFFFFF7F9),
        "M8,6 h32 a3.5,3.5 0 0 1 3.5,3.5 v0 a3.5,3.5 0 0 1 -3.5,3.5 h-32 a3.5,3.5 0 0 1 -3.5,-3.5 v0 a3.5,3.5 0 0 1 3.5,-3.5 z" to Color(0xFF8C3B55),
        "M8,35 h32 a3.5,3.5 0 0 1 3.5,3.5 v0 a3.5,3.5 0 0 1 -3.5,3.5 h-32 a3.5,3.5 0 0 1 -3.5,-3.5 v0 a3.5,3.5 0 0 1 3.5,-3.5 z" to Color(0xFF8C3B55),
    )

    val HomeNav: ImageVector = icon24(
        "HomeNav",
        "M3 11l9-7 9 7v9a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1z" to NavInactive,
    )

    val KumkumDeep: Color = NaamaluModules.Kumkum.deep

    fun navHome(active: Boolean): ImageVector = HomeNav
    fun navPuja(active: Boolean): ImageVector = Kalasam
    fun navNaamalu(active: Boolean): ImageVector = Talapatra
    fun navJapam(active: Boolean): ImageVector = Mala
    fun navSaadhana(active: Boolean): ImageVector = Lotus
}

private fun icon(
    name: String,
    vararg layers: Pair<String, Color>,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 48.dp,
    defaultHeight = 48.dp,
    viewportWidth = 48f,
    viewportHeight = 48f,
).apply {
    layers.forEach { (pathData, color) ->
        addSvgPath(pathData, color)
    }
}.build()

private fun icon24(
    name: String,
    vararg layers: Pair<String, Color>,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    layers.forEach { (pathData, color) ->
        addSvgPath(pathData, color)
    }
}.build()
