package com.smaranam.app.practice

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {
    @Query(
        """
        SELECT sankalpam.* FROM sankalpam
        LEFT JOIN (
            SELECT sankalpamId AS sid, MAX(createdAt) AS lastAt
            FROM practice_entry GROUP BY sankalpamId
        ) recent ON recent.sid = sankalpam.id
        WHERE sankalpam.status = 'active' AND sankalpam.kind = 'japam'
        ORDER BY COALESCE(recent.lastAt, 0) DESC, sankalpam.startDate DESC
        """,
    )
    fun activeJapams(): Flow<List<Sankalpam>>

    @Query("SELECT * FROM sankalpam WHERE status = 'active' AND kind = 'japam'")
    suspend fun activeJapamsNow(): List<Sankalpam>

    @Query("SELECT * FROM sankalpam WHERE status = 'completed' ORDER BY completedAt DESC, id DESC")
    fun completed(): Flow<List<Sankalpam>>

    @Query("SELECT * FROM sankalpam WHERE id = :id")
    fun observe(id: String): Flow<Sankalpam?>

    @Query("SELECT * FROM sankalpam WHERE id = :id")
    suspend fun sankalpam(id: String): Sankalpam?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSankalpam(row: Sankalpam)

    @Query("UPDATE sankalpam SET goal = :goal, days = :days WHERE id = :id")
    suspend fun updatePlan(id: String, goal: Int, days: Int)

    @Query("UPDATE sankalpam SET status = 'completed', completedAt = :at WHERE id = :id")
    suspend fun markCompleted(id: String, at: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: PracticeEntry)

    @Query("SELECT * FROM practice_entry WHERE id = :id")
    suspend fun entry(id: String): PracticeEntry?

    @Query("UPDATE practice_entry SET count = :count, createdAt = :at WHERE id = :id")
    suspend fun updateEntryCount(id: String, count: Int, at: Long)

    @Query("SELECT sankalpamId, SUM(count) AS total FROM practice_entry GROUP BY sankalpamId")
    fun totals(): Flow<List<CountTotal>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM practice_entry WHERE sankalpamId = :id")
    fun total(id: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(count), 0) FROM practice_entry WHERE sankalpamId = :id")
    suspend fun totalNow(id: String): Int

    @Query("SELECT COALESCE(SUM(count), 0) FROM practice_entry WHERE sankalpamId = :id AND localDate = :date")
    fun dayTotal(id: String, date: String): Flow<Int>

    @Query(
        """
        SELECT * FROM practice_entry
        WHERE sankalpamId = :id AND localDate LIKE :prefix || '%'
        ORDER BY localDate DESC, createdAt DESC
        """,
    )
    fun monthEntries(id: String, prefix: String): Flow<List<PracticeEntry>>
}
