package com.ringer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer
import com.ringer.interactive.permission.RingerInteractive


class MainActivity : AppCompatActivity() {

    lateinit var btn_continue: Button

    companion object {
        val REQUEST_CODE_SDK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//


        btn_continue = findViewById(R.id.btn_continue)


        btn_continue.setOnClickListener {

            //SDK Default Dialer
            offerReplacingDefaultDialer(
                this, applicationContext.packageName,
                REQUEST_CODE_SDK
            )

        }
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {

                if (PreferencesApp().getAppearOnTop(this@MainActivity) == false){
                    startActivity(Intent(this@MainActivity,EditScreenActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this@MainActivity,DefaultPhoneActivity::class.java))
                    finish()
                }
            }
        }
        timer.start()


    }
/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SDK){

            if (resultCode == RESULT_OK){
                InitializeToken(
                    this,
                    resources.getString(R.string.ringer_user_name),
                    resources.getString(R.string.ringer_password),
                    resources.getString(R.string.app_name)
                )
                startActivity(Intent(this@MainActivity,ThankYouScreen::class.java))
                finish()
            }

        }

    }*/


}