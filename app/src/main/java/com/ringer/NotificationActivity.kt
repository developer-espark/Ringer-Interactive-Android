package com.ringer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NotificationActivity : AppCompatActivity() {

    lateinit var btn_allow_notifications: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_allow_notifications.setOnClickListener {

            if (PreferencesApp().getTermsAndCondition(this) == true) {
                startActivity(
                    Intent(
                        this@NotificationActivity,
                        TermsAndConditionActivity::class.java
                    )
                )
                finish()
            }else{
                startActivity(
                    Intent(
                        this@NotificationActivity,
                        AccessContactActivity::class.java
                    )
                )
                finish()
            }
        }
    }

    private fun initialize() {
        btn_allow_notifications = findViewById(R.id.btn_allow_notifications)
    }
}