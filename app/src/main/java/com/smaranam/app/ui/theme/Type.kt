@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.smaranam.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.smaranam.app.R
import com.smaranam.app.content.Lang
import com.smaranam.app.content.TextView

private fun variable(res: Int, weight: Int) = Font(
    res,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val CormorantFamily = FontFamily(variable(R.font.cormorant_garamond, 600))

val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
)

val NotoSerifTeluguFamily = FontFamily(
    variable(R.font.noto_serif_telugu, 400),
    variable(R.font.noto_serif_telugu, 600),
)

val NotoSerifDevanagariFamily = FontFamily(
    variable(R.font.noto_serif_devanagari, 400),
    variable(R.font.noto_serif_devanagari, 600),
)

fun SacredStyle(view: TextView, lang: Lang, sizeSp: Int = 24): TextStyle {
    val family = when (view) {
        TextView.Iast -> CormorantFamily
        TextView.Devanagari -> NotoSerifDevanagariFamily
        TextView.Script -> if (lang == Lang.te) NotoSerifTeluguFamily else NotoSerifDevanagariFamily
    }
    val size = sizeSp.sp
    return TextStyle(fontFamily = family, fontSize = size, lineHeight = size * 1.5f)
}

val SannidhiTypography = Typography(
    displayLarge = TextStyle(fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = CormorantFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    bodyLarge = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 26.sp),
    labelLarge = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 20.sp),
    bodyMedium = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
)
