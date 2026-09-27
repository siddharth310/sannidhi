package com.smaranam.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NaamaluColors(
    val ivory: Color,
    val paper: Color,
    val ink: Color,
    val inkSoft: Color,
    val inkMuted: Color,
    val line: Color,
    val gold: Color,
    val primary: Color,
    val onPrimary: Color,
    val surface: Color,
    val error: Color,
    val isRatriTheme: Boolean,
    val modules: NaamaluModulePalette,
)

@Immutable
data class NaamaluModulePalette(
    val kumkum: ModuleColorSet,
    val haldi: ModuleColorSet,
    val tulasi: ModuleColorSet,
    val neela: ModuleColorSet,
    val kamala: ModuleColorSet,
    val chandan: ModuleColorSet,
) {
    operator fun get(module: NaamaluModule): ModuleColorSet = NaamaluModules.forModule(module)
}

fun lightNaamaluColors(): NaamaluColors = NaamaluColors(
    ivory = Ivory,
    paper = Paper,
    ink = Ink,
    inkSoft = InkSoft,
    inkMuted = InkMuted,
    line = LineHairline,
    gold = GoldAccent,
    primary = NaamaluModules.Kumkum.deep,
    onPrimary = Ivory,
    surface = Paper,
    error = ErrorLight,
    isRatriTheme = false,
    modules = NaamaluModulePalette(
        kumkum = NaamaluModules.Kumkum,
        haldi = NaamaluModules.Haldi,
        tulasi = NaamaluModules.Tulasi,
        neela = NaamaluModules.Neela,
        kamala = NaamaluModules.Kamala,
        chandan = NaamaluModules.Chandan,
    ),
)

fun darkNaamaluColors(): NaamaluColors = NaamaluColors(
    ivory = RatriBg,
    paper = RatriSurface,
    ink = RatriInk,
    inkSoft = RatriInk,
    inkMuted = RatriInkMuted,
    line = RatriLine,
    gold = GoldAccent,
    primary = RatriPrimary,
    onPrimary = RatriBg,
    surface = RatriSurface,
    error = ErrorDark,
    isRatriTheme = true,
    modules = NaamaluModulePalette(
        kumkum = NaamaluModules.Kumkum,
        haldi = NaamaluModules.Haldi,
        tulasi = NaamaluModules.Tulasi,
        neela = NaamaluModules.Neela,
        kamala = NaamaluModules.Kamala,
        chandan = NaamaluModules.Chandan,
    ),
)

val LocalNaamaluColors = staticCompositionLocalOf { lightNaamaluColors() }
