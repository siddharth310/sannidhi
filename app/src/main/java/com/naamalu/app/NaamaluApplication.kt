package com.naamalu.app

import android.app.Application
import com.naamalu.app.di.AppContainer

class NaamaluApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
