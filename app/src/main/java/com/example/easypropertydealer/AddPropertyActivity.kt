package com.example.easypropertydealer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddPropertyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }


    }
}