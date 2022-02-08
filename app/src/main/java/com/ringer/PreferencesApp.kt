package com.ringer

import android.content.Context
import android.content.SharedPreferences

class PreferencesApp {
    private lateinit var preferences: SharedPreferences

    fun setTermsAndCondition(context: Context,isCalled : Boolean){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isCalled", isCalled)
        editor.commit()
    }

    fun getTermsAndCondition(context: Context) : Boolean?{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isCalled", true)
        return isCalled
    }
}