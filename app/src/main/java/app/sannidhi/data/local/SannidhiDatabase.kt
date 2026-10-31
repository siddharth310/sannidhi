package app.sannidhi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.sannidhi.practice.PracticeDao
import app.sannidhi.practice.PracticeEntry
import app.sannidhi.practice.Sankalpam

@Database(
    entities = [Sankalpam::class, PracticeEntry::class],
    version = 8,
    exportSchema = false,
)
abstract class SannidhiDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao

    companion object {
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sankalpam ADD COLUMN goalNoted INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun create(context: Context): SannidhiDatabase =
            Room.databaseBuilder(context, SannidhiDatabase::class.java, "smaranam.db")
                .addMigrations(MIGRATION_7_8)
                .fallbackToDestructiveMigration()
                .build()
    }
}
