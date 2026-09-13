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

@Composable
fun SmaranamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) ratriColors() else dinaColors()
    val typography = smaranamTypography()
    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            error = colors.error,
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            background = colors.background,
            surface = colors.surface,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            error = colors.error,
        )
    }

    CompositionLocalProvider(
        LocalSmaranamColors provides colors,
        LocalSmaranamTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = materialTypographyFrom(typography),
            content = content,
        )
    }
}
