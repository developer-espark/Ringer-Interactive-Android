package com.ringer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AppearOnTopActivity : AppCompatActivity() {

    lateinit var btn_appear : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appear_on_top)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_appear.setOnClickListener {

            startActivity(Intent(this@AppearOnTopActivity,NotificationActivity::class.java))
            finish()

        }
    }

    private fun initialize() {
        btn_appear = findViewById(R.id.btn_appear)
    }
}