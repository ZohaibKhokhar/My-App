package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemindersActivity : AppCompatActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addReminderButton: LinearLayout
    private lateinit var reminderTitle: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        addReminderButton = findViewById(R.id.addReminder)
        reminderTitle=findViewById(R.id.remindersTitle)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = ProgressBar.VISIBLE

        fetchReminders()

        // Set up add reminder button
        addReminderButton.setOnClickListener {
            startActivity(Intent(this, AddReminderActivity::class.java))
        }

        // Set up back button
        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchReminders()
    }

    private fun fetchReminders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminders = RetrofitInstance.reminderApi.getReminders()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    reminderTitle.text = "Reminders(${reminders.size})"
                    reminderAdapter = ReminderAdapter(this@RemindersActivity, reminders)
                    recyclerView.adapter = reminderAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@RemindersActivity, "Error fetching reminders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
