package com.ringer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class AllDetailScreenActivity : AppCompatActivity() {
    lateinit var img_back : ImageView
    lateinit var txt_license : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_detail_screen)

        img_back = findViewById(R.id.img_back)
        txt_license = findViewById(R.id.txt_license)

        img_back.setOnClickListener {
            finish()
        }
        txt_license.setOnClickListener {

            startActivity(Intent(this,TermsAndConditionActivity::class.java))

        }

    }
}