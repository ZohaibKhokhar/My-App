package com.example.easypropertydealer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddPropertyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }
        val optional=findViewById<TextView>(R.id.addressOptional)
        val addressDetail=findViewById<LinearLayout>(R.id.addressDetail)

        optional.setOnClickListener {

            if (addressDetail.visibility == View.GONE) {
                addressDetail.visibility = View.VISIBLE
                optional.isActivated = true
            } else {
                addressDetail.visibility = View.GONE
                optional.isActivated = false
            }
        }


    }
}