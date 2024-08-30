package com.example.easypropertydealer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddNoteActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val saveBtn = findViewById<LinearLayout>(R.id.saveNote)

        saveBtn.setOnClickListener {
            // Get the note content from the EditText
            val notes=findViewById<EditText>(R.id.etNote)
            val db = MySQLiteHelper(this, "MyDataBase", null, 4)
            val content = notes.text.toString()
            db.insertNote(content,getCurrentDate())
            finish()
        }
    }


    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Format date as YYYY-MM-DD
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

}