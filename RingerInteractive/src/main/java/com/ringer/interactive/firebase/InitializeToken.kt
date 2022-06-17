package com.ringer.interactive

import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ringer.interactive.api.base_url
import com.ringer.interactive.contact.DeviceData
import com.ringer.interactive.pref.Preferences


fun InitializeToken(context: Context, username: String, password: String, app_name: String,phone:String?) {


    Log.e("username",""+username)
    Log.e("password",""+password)
    Preferences().setEmailAddress(context, username)
    Preferences().setPassword(context, password)
    Preferences().setTokenBaseUrl(context,base_url)
    Preferences().setApplicationName(context, app_name)
    if (phone.equals("")){
        Preferences().setPhone(context,"")
    }else{
        Preferences().setPhone(context,phone)
    }



    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)


    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result!!
        Log.e("tFCMoken",""+token)
        Preferences().setFCMToken(context,token)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName: String = context.getPackageName()
            val pm = context.getSystemService(POWER_SERVICE) as PowerManager?
            if (!pm!!.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                context.startActivity(intent)
            }
        }*/


        //ask contact permission





        DeviceData(context)

        ContactData(context)
//        CallLog(context)

    })

}