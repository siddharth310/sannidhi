package com.smaranam.app.domain.repository

import com.smaranam.app.domain.model.DayRecitationSummary
import com.smaranam.app.domain.model.NaamavaliRecitationStats
import com.smaranam.app.domain.model.PracticeStreak
import com.smaranam.app.domain.model.SaadhanaTotals
import com.smaranam.app.domain.model.Recitation
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

interface RecitationRepository {
    suspend fun logRecitation(recitation: Recitation)

    fun observeRecitations(): Flow<List<Recitation>>

    fun observeRecitationsForDay(date: LocalDate): Flow<List<Recitation>>

    fun observeHistory(limit: Int = 50): Flow<List<Recitation>>

    suspend fun getNaamavaliStats(naamavaliId: String): NaamavaliRecitationStats

    suspend fun getDaySummary(date: LocalDate): DayRecitationSummary

    suspend fun getStreak(asOf: LocalDate = LocalDate.now()): PracticeStreak

    suspend fun getLastRecitation(): Recitation?

    suspend fun getTodayCount(asOf: LocalDate = LocalDate.now()): Int

    suspend fun getSaadhanaTotals(): SaadhanaTotals

    suspend fun getPracticedDates(): Set<LocalDate>
}
