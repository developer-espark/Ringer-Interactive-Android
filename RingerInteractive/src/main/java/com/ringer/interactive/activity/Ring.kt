package com.ringer.interactive.activity

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gu.toolargetool.TooLargeTool


class Ring : Application() {

    override fun onCreate() {
        super.onCreate()
        TooLargeTool.startLogging(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//        startService(Intent(baseContext, OnClearFromRecentService::class.java))

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

}