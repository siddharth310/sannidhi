package com.smaranam.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smaranam.app.practice.PracticeDao
import com.smaranam.app.practice.PracticeEntry
import com.smaranam.app.practice.Sankalpam

@Database(
    entities = [Sankalpam::class, PracticeEntry::class],
    version = 8,
    exportSchema = false,
)
abstract class SmaranamDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao

    companion object {
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sankalpam ADD COLUMN goalNoted INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun create(context: Context): SmaranamDatabase =
            Room.databaseBuilder(context, SmaranamDatabase::class.java, "smaranam.db")
                .addMigrations(MIGRATION_7_8)
                .build()
    }
}
