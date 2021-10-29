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


    fun setEmailAddress(context: Context,email : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("user_email", email.toString())
        editor.commit()
    }

    fun getEmailAddress(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val contact_email: String? = preferences.getString("user_email", "")
        return contact_email.toString()

    }

    fun setPassword(context: Context,email : String){
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("user_password", email.toString())
        editor.commit()
    }

    fun getUserPassword(context: Context): String {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val user_password: String? = preferences.getString("user_password", "")
        return user_password.toString()

    }
}