package com.ringer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer
import com.ringer.interactive.permission.RingerInteractive


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val timer = object: CountDownTimer(7000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {

                startActivity(Intent(this@MainActivity,TermsAndConditionActivity::class.java))
                finish()

            }
        }
        timer.start()

    }



}