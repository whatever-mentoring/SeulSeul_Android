package com.timi.seulseul

import android.app.Application
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {

    companion object {
        lateinit var prefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        prefs = applicationContext.getSharedPreferences("SeulSeul", MODE_PRIVATE)
    }
}