package com.smaranam.app.content

/** Builds the Home info line and Calendar labels from master data. */
object Labels {
    fun tithi(master: Master, day: CalendarDay, view: TextView, lang: Lang): String {
        val paksha = if (day.isShukla) "शुक्ल" else "कृष्ण"
        val n = if (day.tithi == 30) 30 else day.tithiInPaksha
        val name = master.tithis.first { it.n == n }.name
        val pakshaText = Sacred(paksha, if (day.isShukla) "śukla" else "kṛṣṇa", if (day.isShukla) "శుక్ల" else "కృష్ణ")
        return "${pakshaText.pick(view, lang)} ${name.pick(view, lang)}"
    }

    fun masa(master: Master, day: CalendarDay, view: TextView, lang: Lang): String {
        val m = master.masams.first { it.n == day.masa }.name.pick(view, lang)
        if (!day.adhika) return m
        val adhika = Sacred("अधिक", "adhika", "అధిక").pick(view, lang)
        return "$adhika $m"
    }

    fun nakshatra(master: Master, day: CalendarDay, view: TextView, lang: Lang) =
        master.nakshatras.first { it.n == day.nakshatra }.name.pick(view, lang)

    fun rashi(master: Master, n: Int, view: TextView, lang: Lang) =
        master.rashis.first { it.n == n }.name.pick(view, lang)
}
