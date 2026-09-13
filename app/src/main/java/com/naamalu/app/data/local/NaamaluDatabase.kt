package com.naamalu.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.naamalu.app.data.local.dao.RecitationDao
import com.naamalu.app.data.local.entity.RecitationEntity

@Database(
    entities = [RecitationEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NaamaluDatabase : RoomDatabase() {
    abstract fun recitationDao(): RecitationDao

    companion object {
        private const val DATABASE_NAME = "naamalu.db"

        fun create(context: Context): NaamaluDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NaamaluDatabase::class.java,
                DATABASE_NAME,
            ).build()
        }
    }
}
