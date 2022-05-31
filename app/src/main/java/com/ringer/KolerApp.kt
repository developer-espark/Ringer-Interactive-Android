package com.ringer

import android.preference.PreferenceManager
import android.telecom.TelecomManager
import androidx.appcompat.app.AppCompatDelegate
import com.ringer.interactive.BaseApp
import com.ringer.interactive.notification.CallNotification
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class KolerApp : BaseApp() {
    @Inject lateinit var telecomManager: TelecomManager
    @Inject lateinit var callNotification: CallNotification

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        PreferenceManager.setDefaultValues(this, R.xml.preferences_koler, false)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            callNotification.createNotificationChannel()
        }
    }
}