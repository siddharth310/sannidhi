package com.smaranam.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object SmaranamTheme {
    val colors: SmaranamColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSmaranamColors.current

    val typography: SmaranamTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalSmaranamTypography.current
}

object NaamaluTheme {
    val colors: NaamaluColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNaamaluColors.current

    val typography: NaamaluTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalNaamaluTypography.current
}

@Composable
fun SmaranamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val naamalu = if (darkTheme) darkNaamaluColors() else lightNaamaluColors()
    val legacy = smaranamFromNaamaluPublic(naamalu)
    val typography = smaranamTypographyFromNaamalu(naamaluTypography())
    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = legacy.primary,
            onPrimary = legacy.onPrimary,
            background = legacy.background,
            surface = legacy.surface,
            onBackground = legacy.textPrimary,
            onSurface = legacy.textPrimary,
            error = legacy.error,
        )
    } else {
        lightColorScheme(
            primary = legacy.primary,
            onPrimary = legacy.onPrimary,
            background = legacy.background,
            surface = legacy.surface,
            onBackground = legacy.textPrimary,
            onSurface = legacy.textPrimary,
            error = legacy.error,
        )
    }

    CompositionLocalProvider(
        LocalNaamaluColors provides naamalu,
        LocalNaamaluTypography provides naamaluTypography(),
        LocalSmaranamColors provides legacy,
        LocalSmaranamTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = materialTypographyFromNaamalu(naamaluTypography()),
            content = content,
        )
    }
}

@Composable
fun NaamaluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = SmaranamTheme(darkTheme = darkTheme, content = content)

private fun smaranamFromNaamaluPublic(n: NaamaluColors): SmaranamColors = SmaranamColors(
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
