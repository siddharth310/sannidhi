package com.smaranam.app.data.content

import com.smaranam.app.domain.model.Deity
import com.smaranam.app.domain.model.Naama
import com.smaranam.app.domain.model.Naamavali
import com.smaranam.app.domain.model.NaamavaliType
import com.smaranam.app.domain.model.SeedDeities

object SeedContent {
    val ganeshaAshtottara = Naamavali(
        id = "ganesha_ashtottara",
        deityId = SeedDeities.ganesha.id,
        title = "Ganesha Ashtottara Shatanamavali",
        type = NaamavaliType.ASHTOTTARA,
        citation = "Traditional Vināyaka Ashtottara",
    )

    val lakshmiAshtottara = Naamavali(
        id = "lakshmi_ashtottara",
        deityId = SeedDeities.lakshmi.id,
        title = "Lakshmi Ashtottara Shatanamavali",
        type = NaamavaliType.ASHTOTTARA,
        citation = "Traditional Lakshmi Ashtottara",
    )

    val venkateswaraAshtottara = Naamavali(
        id = "venkateswara_ashtottara",
        deityId = SeedDeities.venkateswara.id,
        title = "Venkateswara Ashtottara Shatanamavali",
        type = NaamavaliType.ASHTOTTARA,
        citation = "Traditional Venkateswara Ashtottara",
    )

    private val ganeshaNaamas = listOf(
        naama(1, "sumukhaḥ", "సుముఖః", "सुमुखः", "Auspicious-faced Lord"),
        naama(2, "duḥmodaraḥ", "దుఃమోదరః", "दुर्मुखः", "He with a formidable belly"),
        naama(3, "jñāninīśvaraḥ", "జ్ఞానినీశ్వరః", "ज्ञानिनीश्वरः", "Lord of the wise"),
        naama(4, "bālaprabhuḥ", "బాలప్రభుః", "बालप्रभुः", "Child Lord"),
        naama(5, "pūrṇaḥ", "పూర్ణః", "पूर्णः", "The complete one"),
        naama(6, "varadaḥ", "వరదః", "वरदः", "Bestower of boons"),
        naama(7, "ekadantaḥ", "ఏకదంతః", "एकदंतः", "Single-tusked Lord"),
        naama(8, "herambaḥ", "హేరంబః", "हेरम्बः", "Protector of the weak"),
        naama(9, "lambodaraḥ", "లంబోదరః", "लंबोदरः", "Large-bellied Lord"),
        naama(10, "suragrajaḥ", "సురగ్రజః", "सुरग्रजः", "Elder of the gods"),
        naama(11, "vighnarājaḥ", "విఘ్నరాజః", "विघ्नराजः", "King of obstacles removed"),
        naama(12, "vighnahartā", "విఘ్నహర్తా", "विघ्नहर्ता", "Remover of obstacles"),
    )

    private val lakshmiNaamas = listOf(
        naamaFor("lakshmi", 1, "śrīḥ", "శ్రీః", "श्रीः", "Auspiciousness"),
        naamaFor("lakshmi", 2, "padmā", "పద్మా", "पद्मा", "Lotus-dweller"),
        naamaFor("lakshmi", 3, "kamalā", "కమలా", "कमला", "Lotus goddess"),
    )

    private val venkateswaraNaamas = listOf(
        naamaFor("venkateswara", 1, "śrīnivāsaḥ", "శ్రీనివాసః", "श्रीनिवासः", "Abode of Sri"),
        naamaFor("venkateswara", 2, "govindaḥ", "గోవిందః", "गोविंदः", "Protector of cows"),
        naamaFor("venkateswara", 3, "veṅkaṭeśaḥ", "వెంకటేశః", "वेंकटेशः", "Lord of Venkata hill"),
    )

    val allNaamavalis = listOf(ganeshaAshtottara, lakshmiAshtottara, venkateswaraAshtottara)

    val allNaamas: List<Naama> = ganeshaNaamas + lakshmiNaamas + venkateswaraNaamas

    private fun naama(
        index: Int,
        iast: String,
        te: String,
        sa: String,
        meaning: String,
    ): Naama {
        return Naama(
            id = "ganesha_ashtottara_$index",
            naamavaliId = ganeshaAshtottara.id,
            index = index,
            textIast = iast,
            textTe = te,
            textSa = sa,
            meaning = meaning,
        )
    }

    private fun naamaFor(
        prefix: String,
        index: Int,
        iast: String,
        te: String,
        sa: String,
        meaning: String,
    ): Naama {
        val naamavaliId = "${prefix}_ashtottara"
        return Naama(
            id = "${naamavaliId}_$index",
            naamavaliId = naamavaliId,
            index = index,
            textIast = iast,
            textTe = te,
            textSa = sa,
            meaning = meaning,
        )
    }
}
