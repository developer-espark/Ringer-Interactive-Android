package com.ringer

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
}