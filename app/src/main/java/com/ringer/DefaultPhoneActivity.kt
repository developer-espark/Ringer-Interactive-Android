package com.ringer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer

class DefaultPhoneActivity : AppCompatActivity() {

    lateinit var btn_default : Button

    companion object {
        val REQUEST_CODE_SDK = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_phone)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_default.setOnClickListener {

            offerReplacingDefaultDialer(this,applicationContext.packageName,
                REQUEST_CODE_SDK
            )

        }
    }

    private fun initialize() {
        btn_default = findViewById(R.id.btn_default)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SDK){

            if (resultCode == RESULT_OK){
                /*InitializeToken(
                    this,
                    resources.getString(R.string.ringer_user_name),
                    resources.getString(R.string.ringer_password),
                    resources.getString(R.string.app_name)
                )*/
                startActivity(Intent(this@DefaultPhoneActivity,AppearOnTopActivity::class.java))
                finish()
            }

        }
    }
}