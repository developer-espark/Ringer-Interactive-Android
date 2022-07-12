package com.ringer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer
import com.ringer.interactive.permission.RingerInteractive

class AccessContactActivity : AppCompatActivity() {

    lateinit var btn_allow_contact: Button
    lateinit var txt_privacy1: TextView
    lateinit var txt_privacy2: TextView
    lateinit var txt_terms_condition: TextView
    lateinit var btn_no: Button

    val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access_contact)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_allow_contact.setOnClickListener {


            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                //Request Permission to Continue

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    ),
                    PERMISSIONS_REQUEST_READ_CONTACTS
                )

            } else {
                PreferencesApp().setScreenNumber(this, 2)
                startActivity(
                    Intent(
                        this@AccessContactActivity,
                        AppearOnTopActivity::class.java
                    )
                )
                finish()
            }


        }

        txt_terms_condition.setOnClickListener {

            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.terms_url)))
            startActivity(browserIntent)

        }
        txt_privacy1.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_url)))
            startActivity(browserIntent)
        }
        txt_privacy2.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_url)))
            startActivity(browserIntent)
        }
    }

    private fun initialize() {
        btn_allow_contact = findViewById(R.id.btn_allow_contact)
        txt_privacy1 = findViewById(R.id.txt_privacy1)
        txt_privacy2 = findViewById(R.id.txt_privacy2)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
        btn_no = findViewById(R.id.btn_no)
    }


    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SDK){

            if (resultCode == RESULT_OK){
                InitializeToken(
                    this,
                    resources.getString(R.string.ringer_user_name),
                    resources.getString(R.string.ringer_password),
                    resources.getString(R.string.app_name)
                )
                startActivity(Intent(this@MainActivity,ThankYouScreen::class.java))
                finish()
            }

        }

    }*/
    //Permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        RingerInteractive().onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                PreferencesApp().setScreenNumber(this, 2)

                startActivity(
                    Intent(
                        this@AccessContactActivity,
                        AppearOnTopActivity::class.java
                    )
                )
                finish()
            }

        }

    }

}