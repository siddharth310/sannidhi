package com.smaranam.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Immutable
data class SmaranamTypography(
    val mantraLarge: TextStyle,
    val mantraBody: TextStyle,
    val greeting: TextStyle,
    val iast: TextStyle,
    val sectionTitle: TextStyle,
    val body: TextStyle,
    val labelCaps: TextStyle,
    val caption: TextStyle,
    val nav: TextStyle,
    val nameHero: TextStyle,
    val nameList: TextStyle,
    val displayL: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val label: TextStyle,
)

fun smaranamTypography(): SmaranamTypography = smaranamTypographyFromNaamalu(naamaluTypography())

fun materialTypographyFrom(smaranam: SmaranamTypography): Typography =
    materialTypographyFromNaamalu(naamaluTypography())

val LocalSmaranamTypography = staticCompositionLocalOf { smaranamTypography() }
