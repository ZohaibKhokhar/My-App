package com.example.easypropertydealer

import ContactAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactListActivity : AppCompatActivity() {

    private lateinit var adapter:ContactAdapter
    private lateinit var listView: ListView
    private var contacts: List<Contact> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        listView = findViewById(R.id.contactsList) // Assuming you have a ListView with this id

        // Fetch contacts
        fetchContacts { fetchedContacts ->
            contacts = fetchedContacts
            updateAdapter(contacts)
        }

        // Set up item click listener to handle contact selection
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedContact = contacts[position]
            sendResult(selectedContact)
        }
    }

    private fun fetchContacts(onResult: (List<Contact>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedContacts = RetrofitInstance.contactApi.getContacts()
                withContext(Dispatchers.Main) {
                    if (fetchedContacts.isNotEmpty()) {
                        onResult(fetchedContacts)
                    } else {
                        onResult(emptyList()) // No contacts fetched
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Return an empty list in case of an error
                }
                e.printStackTrace()
            }
        }
    }

    private fun updateAdapter(newContacts: List<Contact>) {
        adapter = ContactAdapter(this, newContacts)
        listView.adapter = adapter
    }

    private fun sendResult(selectedContact: Contact) {

               val resultIntent = Intent().apply {
            putExtra("contactId", selectedContact.id)
            putExtra("contactName", selectedContact.name)
            putExtra("contactMobileNo", selectedContact.mobileNo)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}
