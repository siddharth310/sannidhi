package com.smaranam.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class NaamaluTypography(
    val greeting: TextStyle,
    val heroTitle: TextStyle,
    val cardTitle: TextStyle,
    val tileLabel: TextStyle,
    val tileSub: TextStyle,
    val mantraLarge: TextStyle,
    val mantraBody: TextStyle,
    val iast: TextStyle,
    val body: TextStyle,
    val meta: TextStyle,
    val eyebrow: TextStyle,
    val count: TextStyle,
    val nav: TextStyle,
)

fun naamaluTypography(): NaamaluTypography {
    val greeting = TextStyle(
        fontFamily = NotoSerifTeluguFamily,
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 42.sp,
    )
    val heroTitle = TextStyle(
        fontFamily = NotoSerifTeluguFamily,
        fontSize = 35.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 46.sp,
    )
    val cardTitle = TextStyle(
        fontFamily = FrauncesFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 30.sp,
    )
    val tileLabel = TextStyle(
        fontFamily = NotoSerifTeluguFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
    )
    val tileSub = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
    )
    val mantraLarge = TextStyle(
        fontFamily = NotoSerifDevanagariFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 46.sp,
    )
    val mantraBody = TextStyle(
        fontFamily = NotoSerifDevanagariFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 33.sp,
    )
    val iast = TextStyle(
        fontFamily = FrauncesItalicFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        lineHeight = 24.sp,
    )
    val body = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 25.sp,
    )
    val meta = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
    )
    val eyebrow = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
    )
    val count = TextStyle(
        fontFamily = FrauncesFamily,
        fontSize = 84.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 84.sp,
    )
    val nav = TextStyle(
        fontFamily = InstrumentSansFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 14.sp,
    )
    return NaamaluTypography(
        greeting = greeting,
        heroTitle = heroTitle,
        cardTitle = cardTitle,
        tileLabel = tileLabel,
        tileSub = tileSub,
        mantraLarge = mantraLarge,
        mantraBody = mantraBody,
        iast = iast,
        body = body,
        meta = meta,
        eyebrow = eyebrow,
        count = count,
        nav = nav,
    )
}

/** Legacy Smaranam screen aliases mapped to v3 scale */
fun smaranamTypographyFromNaamalu(n: NaamaluTypography): SmaranamTypography = SmaranamTypography(
    mantraLarge = n.mantraLarge,
    mantraBody = n.mantraBody,
    greeting = n.greeting,
    iast = n.iast,
    sectionTitle = n.cardTitle,
    body = n.body,
    labelCaps = n.eyebrow,
    caption = n.meta,
    nav = n.nav,
    nameHero = n.mantraLarge,
    nameList = n.mantraBody,
    displayL = n.greeting,
    headline = n.cardTitle,
    title = n.cardTitle.copy(fontSize = 20.sp),
    label = n.nav,
)

val LocalNaamaluTypography = staticCompositionLocalOf { naamaluTypography() }

fun materialTypographyFromNaamalu(n: NaamaluTypography): Typography = Typography(
    displayLarge = n.greeting,
    headlineMedium = n.cardTitle,
    titleMedium = n.heroTitle,
    bodyLarge = n.body,
    labelLarge = n.nav,
    bodySmall = n.meta,
)
