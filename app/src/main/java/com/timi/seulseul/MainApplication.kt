package com.timi.seulseul

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.timi.seulseul.data.repository.LocationRepo
import com.timi.seulseul.data.service.LocationService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication (): Application() {

    companion object {
        lateinit var prefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        prefs = applicationContext.getSharedPreferences("SeulSeul", MODE_PRIVATE)

        val serviceIntent = Intent(this, LocationService::class.java)
        // O = 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}