package com.ringer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ringer.interactive.permission.RingerCallLogDetail
import com.ringer.interactive.permission.RingerInteractive

class AppearOnTopActivity : AppCompatActivity() {

    lateinit var btn_appear: Button
    lateinit var txt_privacy1: TextView
    lateinit var txt_terms_condition: TextView

    val PERMISSIONS_REQUEST_CALL_LOG = 101
    var isAskForPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appear_on_top)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_appear.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SYSTEM_ALERT_WINDOW
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                //Request Permission to Continue

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.SYSTEM_ALERT_WINDOW
                    ),
                    PERMISSIONS_REQUEST_CALL_LOG
                )
            } else {

                PreferencesApp().setScreenNumber(this, 3)
                startActivity(Intent(this@AppearOnTopActivity, NotificationActivity::class.java))
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
    }


    private fun initialize() {
        btn_appear = findViewById(R.id.btn_appear)
        txt_privacy1 = findViewById(R.id.txt_privacy1)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        RingerCallLogDetail().onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
        if (requestCode == PERMISSIONS_REQUEST_CALL_LOG) {

            Log.e("grantResult",grantResults[0].toString());
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PreferencesApp().setScreenNumber(this, 3)
                isAskForPermission = true;
                startActivity(
                    Intent(
                        this@AppearOnTopActivity,
                        NotificationActivity::class.java
                    )
                )
                finish()

            }

        }

    }

    override fun onRestart() {
        super.onRestart()
            PreferencesApp().setScreenNumber(this, 3)
            startActivity(Intent(this@AppearOnTopActivity, NotificationActivity::class.java))
            finish()
    }

}