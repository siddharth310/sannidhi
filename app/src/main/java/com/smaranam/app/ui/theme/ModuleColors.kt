package com.smaranam.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

enum class NaamaluModule {
    KUMKUM,
    HALDI,
    TULASI,
    NEELA,
    KAMALA,
    CHANDAN,
}

@Immutable
data class ModuleColorSet(
    val tint: Color,
    val mid: Color,
    val deep: Color,
)

object NaamaluModules {
    val Kumkum = ModuleColorSet(
        tint = Color(0xFFF7E3E1),
        mid = Color(0xFFB23A48),
        deep = Color(0xFF7A1F2B),
    )
    val Haldi = ModuleColorSet(
        tint = Color(0xFFFBEFD0),
        mid = Color(0xFFE0A93B),
        deep = Color(0xFF8A5A12),
    )
    val Tulasi = ModuleColorSet(
        tint = Color(0xFFE4EEDC),
        mid = Color(0xFF6E9A5B),
        deep = Color(0xFF3E5A34),
    )
    val Neela = ModuleColorSet(
        tint = Color(0xFFDCEBEF),
        mid = Color(0xFF2F7188),
        deep = Color(0xFF1B3A4B),
    )
    val Kamala = ModuleColorSet(
        tint = Color(0xFFFBE4EA),
        mid = Color(0xFFE08BA0),
        deep = Color(0xFF8C3B55),
    )
    val Chandan = ModuleColorSet(
        tint = Color(0xFFF4EAD9),
        mid = Color(0xFFC9A67A),
        deep = Color(0xFF6B4A2E),
    )

    fun forModule(module: NaamaluModule): ModuleColorSet = when (module) {
        NaamaluModule.KUMKUM -> Kumkum
        NaamaluModule.HALDI -> Haldi
        NaamaluModule.TULASI -> Tulasi
        NaamaluModule.NEELA -> Neela
        NaamaluModule.KAMALA -> Kamala
        NaamaluModule.CHANDAN -> Chandan
    }

    /** §2.4 — module tint on dark surface */
    fun tintOnDarkSurface(module: NaamaluModule, surface: Color = RatriSurface): Color {
        val deep = forModule(module).deep
        return lerp(surface, deep, 0.18f)
    }
}
