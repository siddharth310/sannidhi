package com.smaranam.app

import android.app.Application
import com.smaranam.app.di.AppContainer

class SmaranamApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
