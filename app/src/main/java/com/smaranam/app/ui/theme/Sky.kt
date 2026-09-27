package com.smaranam.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import java.time.LocalTime

enum class Sky {
    USHA,
    MADHYAHNA,
    SANDHYA,
    RATRI,
}

fun skyFor(local: LocalTime): Sky = when {
    local >= LocalTime.of(4, 30) && local < LocalTime.of(9, 0) -> Sky.USHA
    local >= LocalTime.of(9, 0) && local < LocalTime.of(16, 30) -> Sky.MADHYAHNA
    local >= LocalTime.of(16, 30) && local < LocalTime.of(19, 30) -> Sky.SANDHYA
    else -> Sky.RATRI
}

@Immutable
data class SkyPalette(
    val gradientStops: List<Color>,
    val hillLayers: List<Color>,
    val showSun: Boolean,
    val showMoon: Boolean,
    val showStars: Boolean,
    val showBirds: Boolean,
    val sunColor: Color?,
    val greetingInk: Color,
)

fun paletteFor(sky: Sky): SkyPalette = when (sky) {
    Sky.USHA -> SkyPalette(
        gradientStops = listOf(
            Color(0xFFF2C2B6),
            Color(0xFFF7D6C3),
            Color(0xFFFAE7D6),
            Ivory,
        ),
        hillLayers = listOf(
            Color(0xFFEDC1B5),
            Color(0xFFD39A96),
            Color(0xFFA5646F),
            Color(0xFF7A1F2B),
        ),
        showSun = true,
        showMoon = false,
        showStars = false,
        showBirds = true,
        sunColor = SunDawn,
        greetingInk = Color(0xFF7A1F2B),
    )
    Sky.MADHYAHNA -> SkyPalette(
        gradientStops = listOf(
            Color(0xFFCFE3E6),
            Color(0xFFE9EEE4),
            Color(0xFFF4EFE2),
            Ivory,
        ),
        hillLayers = listOf(
            Color(0xFFB7C7C0),
            Color(0xFF7F9A8E),
            Color(0xFF4E6B4A),
            Color(0xFF3E5A34),
        ),
        showSun = true,
        showMoon = false,
        showStars = false,
        showBirds = true,
        sunColor = SunDawn,
        greetingInk = Color(0xFF3E5A34),
    )
    Sky.SANDHYA -> SkyPalette(
        gradientStops = listOf(
            Color(0xFF5A2340),
            Color(0xFFA3485A),
            Color(0xFFE39A73),
            Color(0xFFEDB488),
        ),
        hillLayers = listOf(
            Color(0xFF9A4455),
            Color(0xFF6E2C43),
            Color(0xFF461C31),
            Color(0xFF2A1220),
        ),
        showSun = true,
        showMoon = false,
        showStars = false,
        showBirds = false,
        sunColor = SunDusk,
        greetingInk = Color(0xFF2A1220),
    )
    Sky.RATRI -> SkyPalette(
        gradientStops = listOf(
            Color(0xFF141726),
            Color(0xFF1E2238),
            Color(0xFF2B2338),
        ),
        hillLayers = listOf(
            Color(0xFF262A3F),
            Color(0xFF1B1D2C),
            Color(0xFF12131D),
            Color(0xFF12131D),
        ),
        showSun = false,
        showMoon = true,
        showStars = true,
        showBirds = false,
        sunColor = null,
        greetingInk = Color(0xFFEFE6DA),
    )
}
