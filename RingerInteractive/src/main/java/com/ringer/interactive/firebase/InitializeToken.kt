package com.ringer.interactive

import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ringer.interactive.pref.Preferences

fun InitializeToken(context: Context, username: String, password: String) {


    Preferences().setEmailAddress(context, username)
    Preferences().setPassword(context, password)


    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)


    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@OnCompleteListener
        }

        // Get new FCM registration token
        var token = task.result!!

        // Log and toast

        Log.e("adsads", token)
    })

}