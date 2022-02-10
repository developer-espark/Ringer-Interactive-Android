package com.ringer

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.ringer.interactive.InitializeToken
import com.ringer.interactive.askSDK.offerReplacingDefaultDialer

class AccessContactActivity : AppCompatActivity() {

    lateinit var btn_allow_contact : Button
    lateinit var txt_privacy1 : TextView
    lateinit var txt_terms_condition : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access_contact)

        initialize()

        onClick()
    }

    private fun onClick() {
        btn_allow_contact.setOnClickListener {

            startActivity(
                Intent(
                    this@AccessContactActivity,
                    EditScreenActivity::class.java
                )
            )
            finish()


    }
        txt_terms_condition.setOnClickListener {

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.terms_url)))
            startActivity(browserIntent)

        }
        txt_privacy1.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_url)))
            startActivity(browserIntent)
        }
    }

    private fun initialize() {
        btn_allow_contact = findViewById(R.id.btn_allow_contact)
        txt_privacy1 = findViewById(R.id.txt_privacy1)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
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