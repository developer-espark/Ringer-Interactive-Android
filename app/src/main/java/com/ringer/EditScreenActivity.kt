package com.ringer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.ringer.interactive.InitializeToken

class EditScreenActivity : AppCompatActivity() {

    lateinit var btn_allow_setting : Button
    lateinit var switch_noti : SwitchCompat
    lateinit var switch_contact : SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_screen)

        initialize()
        InitializeToken(this,resources.getString(R.string.ringer_user_name),resources.getString(R.string.ringer_password),resources.getString(R.string.app_name))
    }

    private fun initialize() {
        btn_allow_setting = findViewById(R.id.btn_allow_setting)
        switch_noti = findViewById(R.id.switch_noti)
        switch_contact = findViewById(R.id.switch_contact)
    }
}