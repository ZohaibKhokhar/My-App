package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PropertyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val addProperty = findViewById<LinearLayout>(R.id.addProperty)
        addProperty.setOnClickListener {
            val intent = Intent(this, AddPropertyActivity::class.java)
            startActivity(intent)
        }
    }
}