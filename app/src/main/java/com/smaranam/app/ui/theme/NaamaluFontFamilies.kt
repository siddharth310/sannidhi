package com.smaranam.app.ui.theme

import androidx.compose.ui.text.font.FontFamily
import com.smaranam.app.domain.model.Script

/**
 * §3 — Fraunces, Instrument Sans, Noto Serif/Sans (Telugu, Devanagari, Tamil, Kannada).
 * Run scripts/download_naamalu_fonts.sh to populate res/font TTF files, then wire Font resources here.
 * Until then, semantic families map to platform serif/sans (no Inter/Cormorant).
 */
val FrauncesFamily: FontFamily = FontFamily.Serif
val FrauncesItalicFamily: FontFamily = FontFamily.Serif
val InstrumentSansFamily: FontFamily = FontFamily.SansSerif

val NotoSerifTeluguFamily: FontFamily = FontFamily.Serif
val NotoSerifDevanagariFamily: FontFamily = FontFamily.Serif
val NotoSerifTamilFamily: FontFamily = FontFamily.Serif
val NotoSerifKannadaFamily: FontFamily = FontFamily.Serif

val NotoSansTeluguFamily: FontFamily = FontFamily.SansSerif
val NotoSansDevanagariFamily: FontFamily = FontFamily.SansSerif

fun notoSerifFor(script: Script): FontFamily = when (script) {
    Script.TELUGU -> NotoSerifTeluguFamily
    Script.TAMIL -> NotoSerifTamilFamily
    Script.DEVANAGARI -> NotoSerifDevanagariFamily
    Script.IAST -> FrauncesItalicFamily
}

fun notoSansFor(script: Script): FontFamily = when (script) {
    Script.TELUGU -> NotoSansTeluguFamily
    Script.TAMIL -> NotoSerifTamilFamily
    Script.DEVANAGARI -> NotoSansDevanagariFamily
    Script.IAST -> InstrumentSansFamily
}
