package com.ringer.interactive

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ringer.interactive.interactor.preferences.PreferencesInteractor
import javax.inject.Inject

abstract class BaseApp : Application() {
    @Inject lateinit var preferences: PreferencesInteractor

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(preferences.themeMode.mode)
        androidx.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences_chooloo, false)
    }
}