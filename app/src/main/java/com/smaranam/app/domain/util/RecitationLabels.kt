package com.smaranam.app.domain.util

import com.smaranam.app.data.content.SeedContent
import com.smaranam.app.domain.model.Naamavali
import com.smaranam.app.domain.model.NaamavaliRecitationStats
import com.smaranam.app.domain.model.Recitation
import com.smaranam.app.domain.model.RecitationMode
import com.smaranam.app.domain.model.Script
import com.smaranam.app.domain.model.SeedDeities
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object RecitationLabels {
    fun title(recitation: Recitation): String {
        if (recitation.naamaId != null) {
            val naama = SeedContent.allNaamas.find { it.id == recitation.naamaId }
            return naama?.archanaForm(Script.IAST) ?: "Japa session"
        }
        return SeedContent.allNaamavalis.find { it.id == recitation.naamavaliId }?.title ?: "Practice"
    }

    fun deityName(deityId: String): String {
        return SeedDeities.findById(deityId)?.name ?: deityId
    }

    fun modeLabel(mode: RecitationMode, count: Int): String {
        return when (mode) {
            RecitationMode.READ -> "Read"
            RecitationMode.LISTEN -> "Listened"
            RecitationMode.JAPA -> "Japa · $count beads"
        }
    }

    fun timeLabel(completedAt: Instant, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return completedAt.atZone(zoneId).format(formatter)
    }

    fun dateLabel(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
    }

    fun badgeForNaamavali(stats: NaamavaliRecitationStats): String {
        val parts = mutableListOf<String>()
        if (stats.readCount > 0) parts += "read ${stats.readCount}×"
        if (stats.listenCount > 0) parts += "listened ${stats.listenCount}×"
        if (stats.japaBeadCount > 0) parts += "${stats.japaBeadCount} japa beads"
        return parts.joinToString(" · ").ifEmpty { "Not yet practiced" }
    }

    fun typeLabel(naamavali: Naamavali): String {
        return when (naamavali.type) {
            com.smaranam.app.domain.model.NaamavaliType.ASHTOTTARA -> "108 names"
            com.smaranam.app.domain.model.NaamavaliType.SAHASRANAMA -> "1000 names"
        }
    }
}
