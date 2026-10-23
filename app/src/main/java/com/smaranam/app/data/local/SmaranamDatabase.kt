package com.smaranam.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smaranam.app.practice.PracticeDao
import com.smaranam.app.practice.PracticeEntry
import com.smaranam.app.practice.Sankalpam

@Database(
    entities = [Sankalpam::class, PracticeEntry::class],
    version = 7,
    exportSchema = false,
)
abstract class SmaranamDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao

    companion object {
        fun create(context: Context): SmaranamDatabase =
            Room.databaseBuilder(context, SmaranamDatabase::class.java, "smaranam.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
