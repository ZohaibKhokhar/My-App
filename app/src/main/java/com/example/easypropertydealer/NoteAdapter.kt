package com.example.easypropertydealer

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.easypropertydealer.RetrofitInstance.noteApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NoteAdapter(private val context: Context, private var notes: List<Note>) : BaseAdapter() {

    private val dbHelper = MySQLiteHelper(context, "MyDataBase", null, 4)


    override fun getCount(): Int {
        return notes.size
    }

    override fun getItem(position: Int): Any {
        return notes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)

        val noteContent = view.findViewById<TextView>(R.id.note_content)
        val noteDate = view.findViewById<TextView>(R.id.note_date)
        val noteImage = view.findViewById<ImageView>(R.id.note_image)

        val note = getItem(position) as Note
        noteContent.text = note.content
        noteDate.text = "Creation Date: ${note.creationDate}"

        noteImage.setOnClickListener {
            showPopupMenu(noteImage, note, position)
        }

        return view
    }

    private fun showPopupMenu(view: View, note: Note, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.note_options_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_update -> {
                    handleUpdate(note, position)
                    true
                }
                R.id.action_delete -> {
                    handleDelete(note, position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun handleUpdate(note: Note, position: Int) {
        val editText = EditText(context)
        editText.setText(note.content)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Update Note")
            .setMessage("Enter new content:")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newContent = editText.text.toString()
                if (newContent.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Call API to update note on the server
                            val updatedNote = note.copy(content = newContent)
                            val response = noteApi.updateNote(note.id, updatedNote)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
                                    // Update local db and refresh UI
                                    dbHelper.updateNote(note.id, newContent)
                                    if (context is NotesActivity) {
                                        context.updateNotesList()
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to update note", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun handleDelete(note: Note, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Call API to delete the note from the server
                        val response = noteApi.deleteNote(note.id)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                                // Delete from local db and refresh UI
                                dbHelper.deleteNote(note.id)
                                if (context is NotesActivity) {
                                    context.updateNotesList()
                                }
                            } else {
                                Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
