package com.ringer.interactive.askSDK

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult

fun offerReplacingDefaultDialer(
    context: Context,
    packageName: String,
    REQUEST_CODE_SDK: Int
) {



    //Above Android 10 & 11
    if (Build.VERSION.SDK_INT >= 29) {
        try {

            val roleManager =
                context.getSystemService(AppCompatActivity.ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            startActivityForResult(context as Activity, intent, REQUEST_CODE_SDK, null)
        } catch (e: Exception) {

        }

    }

    //Below Android Below 10
    else {

        val telecomManager =
            context.getSystemService(AppCompatActivity.TELECOM_SERVICE) as TelecomManager
        if (packageName != telecomManager.defaultDialerPackage) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            context.startActivity(intent)
        }
    }

}