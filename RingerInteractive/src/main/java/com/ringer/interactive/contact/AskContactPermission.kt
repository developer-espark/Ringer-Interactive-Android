package com.ringer.interactive

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ringer.interactive.call.AuthAPICall

fun ContactData(context: Context){
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100


    try {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            //Request Permission to Continue

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                ),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )

        } else {


            //Permission Granted
            AuthAPICall().apiCallAuth(context)



        }
    } catch (e: Exception) {

        Log.e("errorLib",""+e.message)
    }
}