package com.smaranam.app.domain.model

import java.time.Instant
import java.util.UUID

data class Recitation(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val deityId: String,
    val naamavaliId: String,
    val mode: RecitationMode,
    val count: Int,
    val naamaId: String? = null,
    val startedAt: Instant,
    val completedAt: Instant,
    val source: RecitationSource,
)

data class NaamavaliRecitationStats(
    val naamavaliId: String,
    val readCount: Int,
    val listenCount: Int,
    val japaBeadCount: Int,
    val lastPracticedAt: Instant?,
)

data class DayRecitationSummary(
    val date: java.time.LocalDate,
    val totalRecitations: Int,
    val readCount: Int,
    val listenCount: Int,
    val japaBeadCount: Int,
)

data class PracticeStreak(
    val current: Int,
    val longest: Int,
)

data class SaadhanaTotals(
    val totalRecitations: Int,
    val readCount: Int,
    val listenCount: Int,
    val japaBeadCount: Int,
    val japaMalas: Int,
)
