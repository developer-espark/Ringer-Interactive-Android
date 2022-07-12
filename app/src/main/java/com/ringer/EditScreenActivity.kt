package com.ringer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.ringer.interactive.InitializeToken


class EditScreenActivity : AppCompatActivity() {

    lateinit var btn_allow_setting: Button
    lateinit var switch_noti: SwitchCompat
    lateinit var switch_contact: SwitchCompat
    lateinit var switch_default_phone: SwitchCompat
    lateinit var switch_appear_on_top: SwitchCompat

    lateinit var txt_privacy1: TextView
    lateinit var txt_terms_condition: TextView

    lateinit var txt_contact_us: TextView

    lateinit var img_menu: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_screen)

        PreferencesApp().setScreenNumber(this, 5)

        initialize()
        InitializeToken(
            this,
            resources.getString(R.string.ringer_user_name),
            resources.getString(R.string.ringer_password),
            resources.getString(R.string.app_name),
            ""
        )

        onClick()


    }

    @SuppressLint("WrongConstant")
    private fun onClick() {
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
        txt_contact_us.setOnClickListener {

            val intent = Intent(Intent.ACTION_SENDTO) // it's not ACTION_SEND
            intent.data = Uri.parse("mailto:info@flashappllc.com") // or just "mailto:" for blank
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent)

        }
        btn_allow_setting.setOnClickListener {

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)


        }


    }

    @SuppressLint("WrongConstant")
    private fun initialize() {
        btn_allow_setting = findViewById(R.id.btn_allow_setting)
        switch_noti = findViewById(R.id.switch_noti)
        switch_contact = findViewById(R.id.switch_contact)
        switch_default_phone = findViewById(R.id.switch_default_phone)
        switch_appear_on_top = findViewById(R.id.switch_appear_on_top)
        txt_privacy1 = findViewById(R.id.txt_privacy1)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
        txt_contact_us = findViewById(R.id.txt_contact_us)


        img_menu = findViewById(R.id.img_menu)


        img_menu.setOnClickListener {


            /*drawer_layout.openDrawer(Gravity.START)*/
            Constant.openuserMenuDialog(it, this@EditScreenActivity)
        }

    }
}