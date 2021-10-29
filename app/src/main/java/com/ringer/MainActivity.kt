package com.ringer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ringer.interactive.RingerScreen

class MainActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Firebase Token Generation

        RingerScreen().initializeToken().toString()

        //ask contact permission

        askContactPermission()

    }

    private fun askContactPermission() {
        //Not Allowed SDK

        if (checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            //Request Permission to Continue

            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                ),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )

        } else {

            //Permission Granted

//            RingerScreen().loadContacts(this, "0")


        }
    }

    //Permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {

            //Permission Granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //LoadContact

//                RingerScreen().loadContacts(this, "0")
            } else {

                //Permission Denied
                Toast.makeText(
                    this,
                    "Please Grant the Permission in order to continue",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}