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
        noteDate.text = "Creation Date: ${note?.creationDate}"

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
                    val db = MySQLiteHelper(context, "MyDataBase", null, 4)
                    db.updateNote(note.id, newContent)
                    // Refresh the notes list
                    if (context is NotesActivity) {
                        (context as NotesActivity).updateNotesList()
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
                val db = MySQLiteHelper(context, "MyDataBase", null, 4)
                db.deleteNote(note.id)
                // Refresh the notes list
                if (context is NotesActivity) {
                    (context as NotesActivity).updateNotesList()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
