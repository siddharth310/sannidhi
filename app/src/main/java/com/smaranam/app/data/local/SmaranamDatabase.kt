package com.smaranam.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smaranam.app.data.local.dao.RecitationDao
import com.smaranam.app.data.local.entity.RecitationEntity

@Database(
    entities = [RecitationEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SmaranamDatabase : RoomDatabase() {
    abstract fun recitationDao(): RecitationDao

    companion object {
        private const val DATABASE_NAME = "smaranam.db"

        fun create(context: Context): SmaranamDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SmaranamDatabase::class.java,
                DATABASE_NAME,
            ).build()
        }
    }
}
