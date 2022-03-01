package com.ringer

import android.content.Context
import android.content.SharedPreferences

class PreferencesApp {
    private lateinit var preferences: SharedPreferences

    //Terms
    fun setTermsAndCondition(context: Context, isCalled: Boolean) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isCalled", isCalled)
        editor.commit()
    }

    fun getTermsAndCondition(context: Context): Boolean? {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isCalled", true)
        return isCalled
    }

    // setScreenNumber
    fun setScreenNumber(context: Context, isCalled: Int) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt("isScreen", isCalled)
        editor.commit()
    }

    fun getScreenNumber(context: Context): Int? {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Int? = preferences.getInt("isScreen", 0)
        return isCalled
    }

    fun setContactNumber(context: Context, mobileNumber: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("contactNumber", mobileNumber)
        editor.commit()
    }

    fun getContactNumber(context: Context): String? {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val contactNumber = preferences.getString("contactNumber", "")
        return contactNumber
    }
}