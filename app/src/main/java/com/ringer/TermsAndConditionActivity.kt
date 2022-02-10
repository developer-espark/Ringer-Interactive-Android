package com.ringer

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class TermsAndConditionActivity : AppCompatActivity() {

    lateinit var btn_allow_terms : Button
    lateinit var txt_privacy : TextView
    lateinit var txt_termsc : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)

        initialize()

        onClick()

    }

    private fun onClick() {
        btn_allow_terms.setOnClickListener {

            startActivity(Intent(this@TermsAndConditionActivity,AccessContactActivity::class.java))
            finish()


        }
        txt_privacy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.privacy_url)))
            startActivity(browserIntent)
        }
        txt_termsc.setOnClickListener {

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.terms_url)))
            startActivity(browserIntent)

        }


    }

    private fun initialize() {
        btn_allow_terms = findViewById(R.id.btn_allow_terms)
        txt_privacy = findViewById(R.id.txt_privacy)
        txt_termsc = findViewById(R.id.txt_termsc)
    }
}