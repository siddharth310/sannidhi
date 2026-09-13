package com.naamalu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

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
fun NaamaluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) ratriColors() else dinaColors()
    val typography = naamaluTypography()
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
        LocalNaamaluColors provides colors,
        LocalNaamaluTypography provides typography,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = materialTypographyFrom(typography),
            content = content,
        )
    }
}
