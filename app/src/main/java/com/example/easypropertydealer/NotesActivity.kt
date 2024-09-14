// NotesActivity.kt
package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easypropertydealer.RetrofitInstance.dealApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesActivity : AppCompatActivity() {

    private lateinit var notesTitle:TextView
    private lateinit var listView:ListView
    private var notes:List<Note> = emptyList()
    private lateinit var progressBar: ProgressBar
    private lateinit var noNoteText:TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)


         notesTitle = findViewById(R.id.notesTitle)
        progressBar=findViewById(R.id.progressBar)
        noNoteText=findViewById(R.id.noNoteText)
         listView = findViewById(R.id.notes_List)

        noNoteText.visibility=View.GONE
        progressBar.visibility=View.VISIBLE
        fetchNotes()

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
        fetchNotes()
    }

    public fun updateNotesList()
    {
        fetchNotes()
    }



    private fun fetchNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                notes=RetrofitInstance.noteApi.getNotes()
                withContext(Dispatchers.Main) {
                    progressBar.visibility=View.GONE
                    if (notes.isEmpty()) {
                        noNoteText.visibility=View.VISIBLE
                    } else {
                        progressBar.visibility=View.GONE
                        noNoteText.visibility = View.GONE
                        val adapter = NoteAdapter(this@NotesActivity, notes)
                        listView.adapter=adapter
                        notesTitle.text = "Notes(${notes.size})"
                    }

                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@NotesActivity, "Failed to load notes", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


}
