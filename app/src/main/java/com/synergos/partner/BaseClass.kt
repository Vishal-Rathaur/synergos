package com.synergos.partner

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseClass : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("BaseClass", "Hilt has been initialized")

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }




}