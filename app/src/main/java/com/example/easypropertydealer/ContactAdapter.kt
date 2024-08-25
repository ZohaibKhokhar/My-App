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

class ContactAdapter(private val context: Context, private val contacts: List<Contact>) : BaseAdapter() {

    override fun getCount(): Int = contacts.size

    override fun getItem(position: Int): Any = contacts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val contact = contacts[position]
        viewHolder.contactName.text = contact.name
        viewHolder.contactPhone.text = contact.mobileNo
        viewHolder.contactAddress.text = contact.address
        viewHolder.contactIcon.setImageResource(R.drawable.contact)

        viewHolder.moreOptions.setOnClickListener {
            showPopupMenu(viewHolder.moreOptions, contact, position)
        }

        return view
    }

    private fun showPopupMenu(view: View, contact: Contact, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.note_options_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_update -> {
                    handleUpdate(contact, position)
                    true
                }
                R.id.action_delete -> {
                    handleDelete(contact, position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun handleUpdate(contact: Contact, position: Int) {
        val editText = EditText(context)
        editText.setText(contact.name)  // Assuming we're updating the name; adjust as needed

        val dialog = AlertDialog.Builder(context)
            .setTitle("Update Contact")
            .setMessage("Enter new name:")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotEmpty()) {
                    val db = MySQLiteHelper(context, "MyDataBase", null, 2)
                    db.updateContact(contact.id, newName, contact.mobileNo, contact.address, contact.dealer, contact.investor, contact.companyName, contact.personDetails, contact.email, contact.website)
                    // Refresh the contacts list
                    if (context is ContactsActivity) {
                        (context as ContactsActivity).updateContactsList()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun handleDelete(contact: Contact, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete this contact?")
            .setPositiveButton("Delete") { _, _ ->
                val db = MySQLiteHelper(context, "MyDataBase", null, 2)
                db.deleteContact(contact.id)
                // Refresh the contacts list
                if (context is ContactsActivity) {
                    (context as ContactsActivity).updateContactsList()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private class ViewHolder(view: View) {
        val contactName: TextView = view.findViewById(R.id.contactName)
        val contactPhone: TextView = view.findViewById(R.id.contactPhone)
        val contactAddress: TextView = view.findViewById(R.id.contactAddress)
        val contactIcon: ImageView = view.findViewById(R.id.contactIcon)
        val moreOptions: ImageView = view.findViewById(R.id.btnMore)
    }
}
