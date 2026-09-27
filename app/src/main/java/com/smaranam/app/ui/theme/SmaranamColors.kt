package com.smaranam.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Legacy accessor — values mirror [NaamaluColors] v3 §2. */
@Immutable
data class SmaranamColors(
    val background: Color,
    val backgroundDeep: Color,
    val surface: Color,
    val textPrimary: Color,
    val textEmphasis: Color,
    val primary: Color,
    val line: Color,
    val hairline: Color,
    val gold: Color,
    val muted: Color,
    val alt: Color,
    val sectionLabel: Color,
    val onPrimary: Color,
    val error: Color,
    val isDeepam: Boolean,
)

fun dinaColors(): SmaranamColors = smaranamFromNaamalu(lightNaamaluColors())

fun ratriColors(): SmaranamColors = smaranamFromNaamalu(darkNaamaluColors())

private fun smaranamFromNaamalu(n: NaamaluColors): SmaranamColors = SmaranamColors(
    background = n.ivory,
    backgroundDeep = n.ivory,
    surface = n.paper,
    textPrimary = n.ink,
    textEmphasis = n.primary,
    primary = n.primary,
    line = n.line,
    hairline = n.gold,
    gold = n.gold,
    muted = n.inkMuted,
    alt = n.modules.neela.deep,
    sectionLabel = n.modules.kumkum.mid,
    onPrimary = n.onPrimary,
    error = n.error,
    isDeepam = n.isRatriTheme,
)

val LocalSmaranamColors = staticCompositionLocalOf { dinaColors() }
