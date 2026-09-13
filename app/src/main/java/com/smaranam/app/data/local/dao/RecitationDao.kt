package com.smaranam.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smaranam.app.data.local.entity.RecitationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecitationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(recitation: RecitationEntity)

    @Query("SELECT * FROM recitations ORDER BY completedAtEpochMillis DESC")
    fun observeAll(): Flow<List<RecitationEntity>>

    @Query(
        """
        SELECT * FROM recitations
        WHERE completedAtEpochMillis >= :dayStartMillis
          AND completedAtEpochMillis < :dayEndMillis
        ORDER BY completedAtEpochMillis DESC
        """,
    )
    fun observeForDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<RecitationEntity>>

    @Query("SELECT * FROM recitations ORDER BY completedAtEpochMillis DESC LIMIT :limit")
    fun observeHistory(limit: Int): Flow<List<RecitationEntity>>

    @Query("SELECT * FROM recitations ORDER BY completedAtEpochMillis DESC LIMIT 1")
    suspend fun getLast(): RecitationEntity?

    @Query("SELECT * FROM recitations WHERE naamavaliId = :naamavaliId")
    suspend fun getForNaamavali(naamavaliId: String): List<RecitationEntity>

    @Query(
        """
        SELECT * FROM recitations
        WHERE completedAtEpochMillis >= :dayStartMillis
          AND completedAtEpochMillis < :dayEndMillis
        ORDER BY completedAtEpochMillis DESC
        """,
    )
    suspend fun getForDay(dayStartMillis: Long, dayEndMillis: Long): List<RecitationEntity>

    @Query("SELECT * FROM recitations ORDER BY completedAtEpochMillis DESC")
    suspend fun getAllOnce(): List<RecitationEntity>
}
