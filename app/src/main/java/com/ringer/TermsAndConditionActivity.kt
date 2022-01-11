package com.ringer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast

class TermsAndConditionActivity : AppCompatActivity() {

    lateinit var cb_terms : CheckBox
    lateinit var btn_continue : Button
    lateinit var img_back : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)

        cb_terms = findViewById(R.id.cb_terms)
        btn_continue = findViewById(R.id.btn_continue)
        img_back = findViewById(R.id.img_back)

        btn_continue.setOnClickListener {

            if (cb_terms.isChecked){

                startActivity(Intent(this@TermsAndConditionActivity,ThankYouScreen::class.java))
                finish()

            }else{
                Toast.makeText(this@TermsAndConditionActivity,"Please Select Terms and Condition to continue",Toast.LENGTH_LONG).show()
            }

        }
        img_back.setOnClickListener {
            finish()
        }


    }
}