package com.ringer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast

class TermsAndConditionActivity : AppCompatActivity() {

    lateinit var btn_allow_terms : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)

        initialize()

        onClick()

    }

    private fun onClick() {
        btn_allow_terms.setOnClickListener {

            PreferencesApp().setTermsAndCondition(this,false)
            startActivity(Intent(this@TermsAndConditionActivity,AccessContactActivity::class.java))
            finish()


        }
    }

    private fun initialize() {
        btn_allow_terms = findViewById(R.id.btn_allow_terms)
    }
}