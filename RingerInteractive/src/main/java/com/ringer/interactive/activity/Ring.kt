package com.ringer.interactive.activity

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.gu.toolargetool.TooLargeTool
import com.ringer.interactive.pref.Preferences
import com.ringer.interactive.service.OnClearFromRecentService


class Ring : Application() {

    override fun onCreate() {
        super.onCreate()
        TooLargeTool.startLogging(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        createNotificationChannel()

//        startService(Intent(applicationContext, OnClearFromRecentService::class.java))


        Preferences().setIsCallMerged(applicationContext,"0")
//        startService(Intent(baseContext, OnClearFromRecentService::class.java))

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "CHANNEL_ID",
                "CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

}