package com.smaranam.app.di

import android.content.Context
import com.smaranam.app.content.ContentRepository
import com.smaranam.app.data.local.SmaranamDatabase
import com.smaranam.app.practice.PracticeRepository
import com.smaranam.app.settings.AppSettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = SmaranamDatabase.create(appContext)

    val settingsStore = AppSettingsStore(appContext)
    val contentRepository: ContentRepository = ContentRepository(appContext)
    val practiceRepository: PracticeRepository = PracticeRepository(database.practiceDao())

    suspend fun clearPersonalData() {
        settingsStore.clear()
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}
