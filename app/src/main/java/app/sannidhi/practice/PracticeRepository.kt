package app.sannidhi.practice

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

class PracticeRepository(private val dao: PracticeDao) {
    fun activeJapams(): Flow<List<Sankalpam>> = dao.activeJapams()
    fun completed(): Flow<List<Sankalpam>> = dao.completed()
    fun observe(id: String): Flow<Sankalpam?> = dao.observe(id)
    fun totals(): Flow<List<CountTotal>> = dao.totals()
    fun total(id: String): Flow<Int> = dao.total(id)
    fun dayTotal(id: String, date: String): Flow<Int> = dao.dayTotal(id, date)
    fun monthEntries(id: String, monthPrefix: String): Flow<List<PracticeEntry>> = dao.monthEntries(id, monthPrefix)

    /** The first open after the last day moves the japam to Completed, dated on that last day. */
    suspend fun closeExpired(today: LocalDate) {
        dao.activeJapamsNow().forEach { row ->
            val start = runCatching { LocalDate.parse(row.startDate) }.getOrNull() ?: return@forEach
            val end = start.plusDays(row.days.toLong() - 1)
            if (end.isBefore(today)) dao.markCompleted(row.id, end.toString())
        }
    }

    suspend fun beginJapam(
        textId: String,
        unit: String,
        goal: Int,
        days: Int,
        start: LocalDate,
        reason: String?,
    ): String {
        val id = UUID.randomUUID().toString()
        dao.insertSankalpam(
            Sankalpam(
                id = id,
                kind = "japam",
                textId = textId,
                unit = unit,
                goal = goal,
                days = days,
                startDate = start.toString(),
                reason = reason?.trim()?.takeIf { it.isNotEmpty() },
                status = "active",
            ),
        )
        return id
    }

    suspend fun updatePlan(id: String, goal: Int, days: Int) {
        if (goal > 0 && days > 0) dao.updatePlan(id, goal, days)
    }

    suspend fun markComplete(id: String, today: LocalDate) {
        dao.markCompleted(id, today.toString())
    }

    /** The goal line is shown once for this japam, then never again. */
    suspend fun noteGoal(id: String) {
        dao.noteGoal(id)
    }

    suspend fun sankalpam(id: String): Sankalpam? = dao.sankalpam(id)

    /** A finished recitation, a Done tap, a ring tap, or a hand add. Nothing is written before the start date. */
    suspend fun add(sankalpamId: String, count: Int, source: String, date: LocalDate) {
        if (count <= 0) return
        val row = countable(sankalpamId, date) ?: return
        dao.insertEntry(
            PracticeEntry(
                id = UUID.randomUUID().toString(),
                sankalpamId = sankalpamId,
                count = count,
                unit = row.unit,
                source = source,
                localDate = date.toString(),
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    /** One sitting of a looping mantra. The count is rewritten after every finished repetition. */
    suspend fun upsertSession(entryId: String, sankalpamId: String, count: Int, date: LocalDate) {
        if (count <= 0) return
        val row = countable(sankalpamId, date) ?: return
        val existing = dao.entry(entryId)
        if (existing == null) {
            dao.insertEntry(
                PracticeEntry(
                    id = entryId,
                    sankalpamId = sankalpamId,
                    count = count,
                    unit = row.unit,
                    source = "play",
                    localDate = date.toString(),
                    createdAt = System.currentTimeMillis(),
                ),
            )
        } else {
            dao.updateEntryCount(entryId, count, System.currentTimeMillis())
        }
    }

    private suspend fun countable(id: String, date: LocalDate): Sankalpam? {
        val row = dao.sankalpam(id) ?: return null
        if (row.status != "active") return null
        val start = runCatching { LocalDate.parse(row.startDate) }.getOrNull() ?: return null
        if (date.isBefore(start)) return null
        return row
    }
}
