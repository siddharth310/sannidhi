package app.sannidhi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Maroon,
    onPrimary = Ivory,
    primaryContainer = TintPuja,
    onPrimaryContainer = Ink,
    secondary = Peacock,
    onSecondary = Ivory,
    background = Ivory,
    onBackground = Ink,
    surface = Ivory,
    onSurface = Ink,
    surfaceVariant = TintCalendar,
    onSurfaceVariant = InkSoft,
    outline = Gold,
)

private val DarkColors = darkColorScheme(
    primary = Maroon,
    onPrimary = NightText,
    primaryContainer = NightSurface,
    onPrimaryContainer = NightText,
    secondary = Peacock,
    onSecondary = NightText,
    background = NightBackground,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = NightSurface,
    onSurfaceVariant = NightText,
    outline = Gold,
    error = NightText,
    onError = NightBackground,
)

@Composable
fun SannidhiTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SannidhiTypography,
        shapes = SannidhiShapes,
        content = content,
    )
}
