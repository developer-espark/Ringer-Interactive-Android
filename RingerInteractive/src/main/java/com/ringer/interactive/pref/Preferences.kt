package com.ringer.interactive.pref

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri


class Preferences {
    private lateinit var preferences: SharedPreferences

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

    fun getTokenBaseUrl(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val base_url: String? = preferences.getString("base_url", "")
        return base_url.toString()
    }

    fun setTokenBaseUrl(context: Context, base_url: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("base_url", base_url)
        editor.commit()
    }

    fun getAuthToken(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val auth_token: String? = preferences.getString("auth_token", "")
        return auth_token.toString()
    }

    fun setAuthToken(context: Context, auth_token: String) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("auth_token", auth_token)
        editor.commit()
    }

}