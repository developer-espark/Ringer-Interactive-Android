package com.ringer.interactive.contact

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ringer.interactive.call.AuthAPICall

fun CallLog(context: Context){

    val PERMISSIONS_REQUEST_CALL_LOG = 101
    try {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            //Request Permission to Continue

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG
                ),
                PERMISSIONS_REQUEST_CALL_LOG
            )

        } else {

            //Permission Granted
//            AuthAPICall().apiCallAuth(context)


        }
    } catch (e: Exception) {

        Log.e("errorLib",""+e.message)
    }

}