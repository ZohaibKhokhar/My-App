package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val button = findViewById<LinearLayout>(R.id.addlocation)
        button.setOnClickListener{
            val intent = Intent(this, AddLocationActivity::class.java)
            startActivity(intent)
        }



    }

}