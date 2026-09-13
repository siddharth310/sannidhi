package com.naamalu.app.data.repository

import com.naamalu.app.data.local.dao.RecitationDao
import com.naamalu.app.data.mapper.toDomain
import com.naamalu.app.data.mapper.toEntity
import com.naamalu.app.domain.model.DayRecitationSummary
import com.naamalu.app.domain.model.NaamavaliRecitationStats
import com.naamalu.app.domain.model.PracticeStreak
import com.naamalu.app.domain.model.Recitation
import com.naamalu.app.domain.model.RecitationMode
import com.naamalu.app.domain.repository.RecitationRepository
import com.naamalu.app.domain.model.SaadhanaTotals
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecitationRepositoryImpl(
    private val dao: RecitationDao,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) : RecitationRepository {

    override suspend fun logRecitation(recitation: Recitation) {
        dao.insert(recitation.toEntity())
    }

    override fun observeRecitations(): Flow<List<Recitation>> {
        return dao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeRecitationsForDay(date: LocalDate): Flow<List<Recitation>> {
        val (start, end) = dayBounds(date)
        return dao.observeForDay(start, end).map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeHistory(limit: Int): Flow<List<Recitation>> {
        return dao.observeHistory(limit).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getNaamavaliStats(naamavaliId: String): NaamavaliRecitationStats {
        val list = dao.getForNaamavali(naamavaliId).map { it.toDomain() }
        return NaamavaliRecitationStats(
            naamavaliId = naamavaliId,
            readCount = list.count { it.mode == RecitationMode.READ },
            listenCount = list.count { it.mode == RecitationMode.LISTEN },
            japaBeadCount = list.filter { it.mode == RecitationMode.JAPA }.sumOf { it.count },
            lastPracticedAt = list.maxByOrNull { it.completedAt }?.completedAt,
        )
    }

    override suspend fun getDaySummary(date: LocalDate): DayRecitationSummary {
        val (start, end) = dayBounds(date)
        val list = dao.getForDay(start, end).map { it.toDomain() }
        return DayRecitationSummary(
            date = date,
            totalRecitations = list.size,
            readCount = list.count { it.mode == RecitationMode.READ },
            listenCount = list.count { it.mode == RecitationMode.LISTEN },
            japaBeadCount = list.filter { it.mode == RecitationMode.JAPA }.sumOf { it.count },
        )
    }

    override suspend fun getStreak(asOf: LocalDate): PracticeStreak {
        val practicedDates = dao.getAllOnce()
            .map { it.toDomain().completedAt.atZone(zoneId).toLocalDate() }
            .toSet()

        if (practicedDates.isEmpty()) {
            return PracticeStreak(current = 0, longest = 0)
        }

        var current = 0
        var cursor = asOf
        while (practicedDates.contains(cursor)) {
            current++
            cursor = cursor.minusDays(1)
        }

        val sorted = practicedDates.sorted()
        var longest = 0
        var run = 0
        var previous: LocalDate? = null
        for (date in sorted) {
            run = if (previous != null && date == previous.plusDays(1)) run + 1 else 1
            longest = maxOf(longest, run)
            previous = date
        }

        return PracticeStreak(current = current, longest = longest)
    }

    override suspend fun getLastRecitation(): Recitation? {
        return dao.getLast()?.toDomain()
    }

    override suspend fun getTodayCount(asOf: LocalDate): Int {
        val (start, end) = dayBounds(asOf)
        return dao.getForDay(start, end).size
    }

    override suspend fun getSaadhanaTotals(): SaadhanaTotals {
        val list = dao.getAllOnce().map { it.toDomain() }
        val japaBeads = list.filter { it.mode == RecitationMode.JAPA }.sumOf { it.count }
        return SaadhanaTotals(
            totalRecitations = list.size,
            readCount = list.count { it.mode == RecitationMode.READ },
            listenCount = list.count { it.mode == RecitationMode.LISTEN },
            japaBeadCount = japaBeads,
            japaMalas = japaBeads / 108,
        )
    }

    override suspend fun getPracticedDates(): Set<LocalDate> {
        return dao.getAllOnce()
            .map { it.toDomain().completedAt.atZone(zoneId).toLocalDate() }
            .toSet()
    }

    private fun dayBounds(date: LocalDate): Pair<Long, Long> {
        val start = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return start to end
    }
}
