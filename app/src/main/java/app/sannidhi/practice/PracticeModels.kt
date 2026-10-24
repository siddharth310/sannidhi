package app.sannidhi.practice

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A resolution to chant, alone for now. Groups use the same row later. */
@Entity(tableName = "sankalpam")
data class Sankalpam(
    @PrimaryKey val id: String,
    val kind: String,
    val textId: String,
    val unit: String,
    val goal: Int,
    val days: Int,
    val startDate: String,
    val reason: String? = null,
    val status: String,
    val completedAt: String? = null,
    val groupId: String? = null,
    /** Set the first time the goal line is shown, so it never appears again. */
    val goalNoted: Boolean = false,
)

/** One saved count. A cut-off recitation is never written. */
@Entity(
    tableName = "practice_entry",
    indices = [Index("sankalpamId"), Index("localDate")],
)
data class PracticeEntry(
    @PrimaryKey val id: String,
    val sankalpamId: String,
    val count: Int,
    val unit: String,
    val source: String,
    val localDate: String,
    val createdAt: Long,
    val sentToGroup: Boolean = false,
)

data class CountTotal(
    val sankalpamId: String,
    val total: Int,
)
