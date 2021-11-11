package com.ringer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.permission.RingerInteractive

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Firebase Token Generation

        InitializeToken(this,resources.getString(R.string.ringer_user_name),resources.getString(R.string.ringer_password))

    }


    //Permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        RingerInteractive().onRequestPermissionsResult(requestCode, permissions, grantResults,this)

    }

}