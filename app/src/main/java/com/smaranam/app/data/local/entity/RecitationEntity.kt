package com.smaranam.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recitations",
    indices = [
        Index(value = ["completedAtEpochMillis"]),
        Index(value = ["naamavaliId"]),
        Index(value = ["userId"]),
    ],
)
data class RecitationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val deityId: String,
    val naamavaliId: String,
    val mode: String,
    val count: Int,
    val naamaId: String?,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long,
    val source: String,
)
