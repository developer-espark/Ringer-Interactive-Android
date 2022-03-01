package com.ringer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SimNumberActivity : AppCompatActivity() {

    lateinit var btnSubmit: Button
    lateinit var edtSimNumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sim_number)

        edtSimNumber = findViewById(R.id.edtSimNumber)
        btnSubmit = findViewById(R.id.btn_submit)

        btnSubmit.setOnClickListener(
            View.OnClickListener {
                if (edtSimNumber.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter contact number", Toast.LENGTH_SHORT).show()
                }else if(edtSimNumber.text.toString().trim().length<10)
                {
                    Toast.makeText(this, "Please enter valid contact number", Toast.LENGTH_SHORT).show()
                }else {
                    PreferencesApp().setContactNumber(this, edtSimNumber.text.toString().trim())
                    PreferencesApp().setScreenNumber(this, 1);
                    startActivity(Intent(this, DefaultPhoneActivity::class.java))
                    finish()
                }
            }
        )
    }
}