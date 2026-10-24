package app.sannidhi.di

import android.content.Context
import app.sannidhi.content.ContentRepository
import app.sannidhi.data.local.SannidhiDatabase
import app.sannidhi.practice.PracticeRepository
import app.sannidhi.settings.AppSettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = SannidhiDatabase.create(appContext)

    val settingsStore = AppSettingsStore(appContext)
    val contentRepository: ContentRepository = ContentRepository(appContext)
    val practiceRepository: PracticeRepository = PracticeRepository(database.practiceDao())

    suspend fun clearPersonalData() {
        settingsStore.clear()
        withContext(Dispatchers.IO) {
            database.clearAllTables()
            appContext.cacheDir.deleteRecursively()
        }
    }
}
