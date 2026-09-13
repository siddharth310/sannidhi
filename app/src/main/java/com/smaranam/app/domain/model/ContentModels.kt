package com.smaranam.app.domain.model

data class Deity(
    val id: String,
    val name: String,
    val nameTe: String? = null,
    val nameTa: String? = null,
    val nameSa: String? = null,
)

data class Naamavali(
    val id: String,
    val deityId: String,
    val title: String,
    val type: NaamavaliType,
    val audioUrl: String? = null,
    val videoUrl: String? = null,
    val citation: String? = null,
)

data class Naama(
    val id: String,
    val naamavaliId: String,
    val index: Int,
    val textIast: String,
    val textTe: String? = null,
    val textTa: String? = null,
    val textSa: String? = null,
    val meaning: String? = null,
) {
    fun archanaForm(script: Script): String {
        val name = when (script) {
            Script.TELUGU -> textTe ?: textIast
            Script.TAMIL -> textTa ?: textIast
            Script.DEVANAGARI -> textSa ?: textIast
            Script.IAST -> textIast
        }
        return "oṁ $name namaḥ"
    }
}
