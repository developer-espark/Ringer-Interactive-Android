package com.ringer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {

    lateinit var btn_allow_notifications: Button
    lateinit var txt_privacy1 : TextView
    lateinit var txt_terms_condition : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_allow_notifications.setOnClickListener {

                startActivity(
                    Intent(
                        this@NotificationActivity,
                        AccessContactActivity::class.java
                    )
                )
                finish()
        }
        txt_terms_condition.setOnClickListener {

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.terms_url)))
            startActivity(browserIntent)

        }
        txt_privacy1.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_url)))
            startActivity(browserIntent)
        }
    }

    private fun initialize() {
        btn_allow_notifications = findViewById(R.id.btn_allow_notifications)
        txt_privacy1 = findViewById(R.id.txt_privacy1)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
    }
}