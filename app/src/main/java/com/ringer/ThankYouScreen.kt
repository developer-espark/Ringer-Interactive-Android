package com.ringer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer

class ThankYouScreen : AppCompatActivity() {
    companion object {
        val REQUEST_CODE_SDK = 2
    }

    lateinit var drawer_layout: DrawerLayout
    lateinit var nv: NavigationView
    lateinit var img_menu: ImageView
    lateinit var version_name: TextView
    private var t: ActionBarDrawerToggle? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you_screen)

        version_name = findViewById(R.id.version_name)
        version_name.text = "V" + BuildConfig.VERSION_NAME.toString()
        drawer_layout = findViewById(R.id.drawer_layout)
        nv = findViewById(R.id.nv)
        img_menu = findViewById(R.id.img_menu)
        t = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)

        drawer_layout.addDrawerListener(t!!)
        t!!.syncState()


        img_menu.setOnClickListener {


            drawer_layout.openDrawer(Gravity.START)
        }


        //Firebase Token Generation

        InitializeToken(
            this,
            resources.getString(R.string.ringer_user_name),
            resources.getString(R.string.ringer_password),
            resources.getString(R.string.app_name),
            PreferencesApp().getContactNumber(this)
        )
    }
    /*//Permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        RingerInteractive().onRequestPermissionsResult(requestCode, permissions, grantResults,this)

    }*/

}