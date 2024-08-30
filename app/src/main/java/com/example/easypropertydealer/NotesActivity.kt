// NotesActivity.kt
package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val db = MySQLiteHelper(this, "MyDataBase", null, 4)
        val notes = db.getAllNotes() // Fetch all notes from the database

        val notesTitle = findViewById<TextView>(R.id.notesTitle)
        notesTitle.text = "Notes(${notes.size})"

        val listView = findViewById<ListView>(R.id.notes_List)
        val adapter = NoteAdapter(this, notes)
        listView.adapter = adapter

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val button = findViewById<LinearLayout>(R.id.addnotes)
        button.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        updateNotesList()
    }


    // NotesActivity.kt
    fun updateNotesList() {
        val db = MySQLiteHelper(this, "MyDataBase", null, 4)
        val notes = db.getAllNotes()

        val notesTitle = findViewById<TextView>(R.id.notesTitle)
        notesTitle.text = "Notes(${notes.size})"

        val listView = findViewById<ListView>(R.id.notes_List)
        val adapter = NoteAdapter(this, notes)
        listView.adapter = adapter
    }

}
