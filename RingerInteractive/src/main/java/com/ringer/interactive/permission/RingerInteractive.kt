package com.ringer.interactive.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.ringer.interactive.activity.RingerScreen
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.ringer.interactive.call.AuthAPICall


class RingerInteractive {

    //Permission
    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray,context: Context
    ) {
        if (requestCode == RingerScreen.PERMISSIONS_REQUEST_READ_CONTACTS) {

            //Permission Granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //LoadContact
                Log.e("contact","contact")

                AuthAPICall().apiCallAuth(context)


//                RingerScreen().loadContacts(this, "0")
            } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(context as Activity,Manifest.permission.READ_CONTACTS)){

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


            }else{

                //Permission Denied
                Log.e("denied","denied")
                Toast.makeText(
                    context,
                    "Please Grant the Permission in order to continue",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}