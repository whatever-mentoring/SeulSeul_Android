package com.timi.seulseul.data.repository

import android.content.SharedPreferences

class SPRepo (private val prefs: SharedPreferences) {
    fun checkOnBoarding(): Boolean = prefs.getBoolean("KEY_ONBOARDING", false)

}