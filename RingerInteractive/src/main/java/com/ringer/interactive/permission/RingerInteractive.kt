package com.ringer.interactive.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.ringer.interactive.activity.RingerScreen
import com.ringer.interactive.call.AuthAPICall


class RingerInteractive {

    //Permission
    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray, context: Context
    ) {
        if (requestCode == RingerScreen.PERMISSIONS_REQUEST_READ_CONTACTS) {

            //Permission Granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                AuthAPICall().apiCallAuth(context)


            } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_CONTACTS
                )
            ) {

                Toast.makeText(
                    context,
                    "Please Grant the Permission in order to continue",
                    Toast.LENGTH_SHORT
                ).show()

                val i = Intent()
                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.data = Uri.parse("package:" + context.getPackageName())
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                context.startActivity(i)

            } else {

                //Permission Denied
                Toast.makeText(
                    context,
                    "Please Grant the Permission in order to continue",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        if (requestCode == RingerScreen.PERMISSIONS_REQUEST_CALL_LOG) {
            //Permission Granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {



            } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_CALL_LOG
                )
            ) {

                Toast.makeText(
                    context,
                    "Please Grant the Permission in order to continue",
                    Toast.LENGTH_SHORT
                ).show()

                val i = Intent()
                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.data = Uri.parse("package:" + context.getPackageName())
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                context.startActivity(i)

            } else {

                //Permission Denied
                Toast.makeText(
                    context,
                    "Please Grant the Permission in order to continue",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }
}