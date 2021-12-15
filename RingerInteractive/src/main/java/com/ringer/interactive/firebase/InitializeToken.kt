package com.ringer.interactive

import android.content.Context
import android.os.StrictMode
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ringer.interactive.api.base_url
import com.ringer.interactive.pref.Preferences

fun InitializeToken(context: Context, username: String, password: String, app_name: String) {


    Preferences().setEmailAddress(context, username)
    Preferences().setPassword(context, password)
    Preferences().setTokenBaseUrl(context,base_url)
    Preferences().setApplicationName(context, app_name)


    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)


    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result!!
        Preferences().setFCMToken(context,token)


        //ask contact permission

        ContactData(context)

    })

}