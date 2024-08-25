package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.calculator)
        imageView.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }

        val contacts = findViewById<ImageView>(R.id.contacts)
        contacts.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        val property = findViewById<ImageView>(R.id.property)
        property.setOnClickListener {
            val intent = Intent(this, PropertyActivity::class.java)
            startActivity(intent)
        }

        val deal = findViewById<ImageView>(R.id.deals)
       deal.setOnClickListener {
            val intent = Intent(this, DealsActivity::class.java)
            startActivity(intent)
        }

        val notes = findViewById<ImageView>(R.id.notes)
        notes.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
        }

        val locations = findViewById<ImageView>(R.id.locations)
        locations.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }


        val reminders = findViewById<ImageView>(R.id.reminders)
        reminders.setOnClickListener {
            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
        }
    }
}
