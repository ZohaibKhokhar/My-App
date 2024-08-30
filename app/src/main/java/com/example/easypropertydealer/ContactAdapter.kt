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
import com.example.easypropertydealer.Contact
import com.example.easypropertydealer.ContactsActivity
import com.example.easypropertydealer.R
import com.example.easypropertydealer.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

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
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dailog_update_contact, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editMobileNo = dialogView.findViewById<EditText>(R.id.editMobileNo)
        val editAddress = dialogView.findViewById<EditText>(R.id.editAddress)

        // Set existing values
        editName.setText(contact.name)
        editMobileNo.setText(contact.mobileNo)
        editAddress.setText(contact.address)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Update Contact")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newName = editName.text.toString()
                val newMobileNo = editMobileNo.text.toString()
                val newAddress = editAddress.text.toString()

                if (newName.isNotEmpty() && newMobileNo.isNotEmpty()) {
                    val updatedContact = contact.copy(
                        name = newName,
                        mobileNo = newMobileNo,
                        address = newAddress
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitInstance.contactApi.updateContact(contact.id, updatedContact)
                            if (response.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    // Refresh the contacts list
                                    if (context is ContactsActivity) {
                                        (context as ContactsActivity).updateContactsList()
                                    }
                                }
                            } else {
                                // Handle unsuccessful response
                                withContext(Dispatchers.Main) {
                                    showToast("Update failed: ${response.message()}")
                                }
                            }
                        } catch (e: HttpException) {
                            withContext(Dispatchers.Main) {
                                showToast("Update failed: ${e.message()}")
                            }
                            e.printStackTrace()
                        }
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
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.contactApi.deleteContact(contact.id)
                        if (response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                // Refresh the contacts list
                                if (context is ContactsActivity) {
                                    (context as ContactsActivity).updateContactsList()
                                }
                            }
                        } else {
                            // Handle unsuccessful response
                            withContext(Dispatchers.Main) {
                                showToast("Delete failed: ${response.message()}")
                            }
                        }
                    } catch (e: HttpException) {
                        withContext(Dispatchers.Main) {
                            showToast("Delete failed: ${e.message()}")
                        }
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private class ViewHolder(view: View) {
        val contactName: TextView = view.findViewById(R.id.contactName)
        val contactPhone: TextView = view.findViewById(R.id.contactPhone)
        val contactIcon: ImageView = view.findViewById(R.id.contactIcon)
        val moreOptions: ImageView = view.findViewById(R.id.btnMore)
    }
}
