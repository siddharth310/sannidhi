package com.naamalu.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NaamaluColors(
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textEmphasis: Color,
    val primary: Color,
    val hairline: Color,
    val muted: Color,
    val error: Color,
    val onPrimary: Color,
)

fun dinaColors(): NaamaluColors {
    return NaamaluColors(
        background = Ivory,
        surface = SurfaceDina,
        textPrimary = Peacock,
        textEmphasis = Maroon,
        primary = Maroon,
        hairline = Gold.copy(alpha = 0.4f),
        muted = MutedDina,
        error = ErrorDina,
        onPrimary = Ivory,
    )
}

fun ratriColors(): NaamaluColors {
    return NaamaluColors(
        background = BackgroundRatri,
        surface = Peacock,
        textPrimary = Ivory,
        textEmphasis = TextEmphasisRatri,
        primary = Maroon,
        hairline = Gold.copy(alpha = 0.4f),
        muted = MutedRatri,
        error = ErrorRatri,
        onPrimary = Ivory,
    )
}

val LocalNaamaluColors = staticCompositionLocalOf { dinaColors() }
