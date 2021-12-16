package com.ringer.interactive.pref

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.Gson
import com.ringer.interactive.model.CallLogDetail
import java.lang.reflect.Type


class Preferences {
    private lateinit var preferences: SharedPreferences

    //set contact url
    fun setImageUrl(context: Context, image_url: Uri?) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("image_url", image_url.toString())
        editor.commit()
    }

    fun getImageUrl(context: Context): Uri {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val image_url: String? = preferences.getString("image_url", "")
        return image_url!!.toUri()

    }


    //set email address
    fun setEmailAddress(context: Context, email: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("user_email", email)
        editor.commit()
    }

    fun getEmailAddress(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val contact_email: String? = preferences.getString("user_email", "")
        return contact_email.toString()

    }

    //set password
    fun setPassword(context: Context, password: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("user_password", password)
        editor.commit()
    }

    fun getUserPassword(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val user_password: String? = preferences.getString("user_password", "")
        return user_password.toString()

    }

    //set base url

    fun setTokenBaseUrl(context: Context, base_url: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("base_url", base_url)
        editor.commit()
    }

    fun getTokenBaseUrl(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val base_url: String? = preferences.getString("base_url", "")
        return base_url.toString()
    }



    //set api auth token
    fun setAuthToken(context: Context, auth_token: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("auth_token", auth_token)
        editor.commit()
    }

    fun getAuthToken(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val auth_token: String? = preferences.getString("auth_token", "")
        return auth_token.toString()
    }

    //set fcm token
    fun setFCMToken(context: Context,fcm_token : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("fcm_token", fcm_token)
        editor.commit()
    }

    fun getFCMToken(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val fcm_token: String? = preferences.getString("fcm_token", "")
        return fcm_token.toString()
    }

    //set app name
    fun setApplicationName(context: Context,app_name : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("app_name", app_name)
        editor.commit()
    }

    fun getApplicationName(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val app_name: String? = preferences.getString("app_name", "")
        return app_name.toString()
    }


    fun setCallLogArrayList(context: Context,callLogList : ArrayList<CallLogDetail>){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(callLogList)
        editor.putString("call_log", json)
        editor.commit()

    }

}