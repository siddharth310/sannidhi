package app.sannidhi

import android.app.Application
import app.sannidhi.di.AppContainer

class SannidhiApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
