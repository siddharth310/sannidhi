package com.smaranam.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.smaranam.app.domain.model.Script

// Placeholder families until OFL fonts are bundled in res/font.
val CormorantFamily = FontFamily.Serif
val InterFamily = FontFamily.SansSerif
val NotoTeluguFamily = FontFamily.Serif
val NotoTamilFamily = FontFamily.Serif
val NotoDevanagariFamily = FontFamily.Serif

fun notoSerifFor(script: Script): FontFamily {
    return when (script) {
        Script.TELUGU -> NotoTeluguFamily
        Script.TAMIL -> NotoTamilFamily
        Script.DEVANAGARI -> NotoDevanagariFamily
        Script.IAST -> CormorantFamily
    }
}

@Immutable
data class SmaranamTypography(
    val nameHero: TextStyle,
    val nameList: TextStyle,
    val displayL: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
)

fun smaranamTypography(): SmaranamTypography {
    return SmaranamTypography(
        nameHero = TextStyle(
            fontFamily = NotoDevanagariFamily,
            fontSize = 34.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 42.sp,
        ),
        nameList = TextStyle(
            fontFamily = NotoDevanagariFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
        ),
        displayL = TextStyle(
            fontFamily = CormorantFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 34.sp,
        ),
        headline = TextStyle(
            fontFamily = CormorantFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
        ),
        title = TextStyle(
            fontFamily = InterFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
        ),
        body = TextStyle(
            fontFamily = InterFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
        ),
        label = TextStyle(
            fontFamily = InterFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
        ),
        caption = TextStyle(
            fontFamily = InterFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp,
        ),
    )
}

fun materialTypographyFrom(smaranam: SmaranamTypography): Typography {
    return Typography(
        displayLarge = smaranam.displayL,
        headlineMedium = smaranam.headline,
        titleMedium = smaranam.title,
        bodyLarge = smaranam.body,
        labelLarge = smaranam.label,
        bodySmall = smaranam.caption,
    )
}

val LocalSmaranamTypography = staticCompositionLocalOf { smaranamTypography() }
