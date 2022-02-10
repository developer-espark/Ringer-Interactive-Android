package com.ringer

import android.content.Context
import android.content.SharedPreferences

class PreferencesApp {
    private lateinit var preferences: SharedPreferences

    //Terms
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

    //Notification
    fun setNotification(context: Context,isCalled : Boolean){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isCalledNotification", isCalled)
        editor.commit()
    }

    fun getNotification(context: Context) : Boolean?{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isCalledNotification", true)
        return isCalled
    }

    //Contact Access
    fun setContact(context: Context,isCalled : Boolean){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isContact", isCalled)
        editor.commit()
    }

    fun getContact(context: Context) : Boolean?{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isContact", true)
        return isCalled
    }

    //Contact Access
    fun setDefaultApp(context: Context,isCalled : Boolean){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isDefault", isCalled)
        editor.commit()
    }

    fun getDefaultApp(context: Context) : Boolean?{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isDefault", true)
        return isCalled
    }


    //Appear On Top
    fun setAppearOnTop(context: Context,isCalled : Boolean){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("isAppear", isCalled)
        editor.commit()
    }

    fun getAppearOnTop(context: Context) : Boolean?{
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isCalled: Boolean? = preferences.getBoolean("isAppear", true)
        return isCalled
    }
}