package com.ringer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer
import com.ringer.interactive.permission.RingerInteractive


class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE_SDK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Firebase Token Generation

        InitializeToken(this,resources.getString(R.string.ringer_user_name),resources.getString(R.string.ringer_password),resources.getString(R.string.app_name))
        val timer = object: CountDownTimer(7000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {

                startActivity(Intent(this@MainActivity,TermsAndConditionActivity::class.java))
                finish()

            }
        }
        timer.start()

    }

    //Permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        RingerInteractive().onRequestPermissionsResult(requestCode, permissions, grantResults,this)

    }

    override fun onStart() {
        super.onStart()

        //SDK Default Dialer
        offerReplacingDefaultDialer(this,applicationContext.packageName,REQUEST_CODE_SDK)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SDK){

            if (resultCode == RESULT_OK){
                InitializeToken(
                    this,
                    resources.getString(R.string.ringer_user_name),
                    resources.getString(R.string.ringer_password),
                    resources.getString(R.string.app_name)
                )
            }

        }

    }



}