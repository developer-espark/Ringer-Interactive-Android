package com.ringer

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.Integer.min


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
                    Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show()
                }else if(edtSimNumber.text.toString().trim().length<14)
                {
                    Toast.makeText(this, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show()
                }else {


                    var phone = edtSimNumber.text.toString().trim().replace("[()\\s-]+".toRegex(), "")


                    PreferencesApp().setContactNumber(this, phone)
                    PreferencesApp().setScreenNumber(this, 1)
                    startActivity(Intent(this, DefaultPhoneActivity::class.java))
                    finish()
                }
            }
        )

    }


}