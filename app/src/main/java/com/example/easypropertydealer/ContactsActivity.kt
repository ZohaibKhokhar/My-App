package com.example.easypropertydealer

import ContactAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private lateinit var listView: ListView
    private lateinit var noContactsText: TextView
    private lateinit var contactsTitle: TextView
    private lateinit var tabAll: TextView
    private lateinit var tabDealer: TextView
    private lateinit var tabInvestor: TextView
    private lateinit var progressBar: ProgressBar
    private var contacts: List<Contact> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        // Initialize views
        noContactsText = findViewById(R.id.noContactText)
        contactsTitle = findViewById(R.id.contactsTitle)
        tabAll = findViewById(R.id.tabAll)
        tabDealer = findViewById(R.id.tabDealer)
        tabInvestor = findViewById(R.id.tabInvestor)
        listView = findViewById(R.id.contactsList)
        progressBar = findViewById(R.id.progressBar)
        // Back button
        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        // Fetch contacts
        fetchContacts { fetchedContacts ->
            noContactsText.visibility= View.GONE
            progressBar.visibility = View.VISIBLE
            contacts = fetchedContacts
            updateUI()
        }

        tabDealer.setOnClickListener {
            updateTabColors(tabDealer, "Dealer(${contacts.count { it.dealer }})")
            val filteredContacts = contacts.filter { it.dealer }
            updateAdapter(filteredContacts)
        }

        tabInvestor.setOnClickListener {
            updateTabColors(tabInvestor, "Investor(${contacts.count { it.investor }})")
            val filteredContacts = contacts.filter { it.investor }
            updateAdapter(filteredContacts)
        }

        tabAll.setOnClickListener {
            updateTabColors(tabAll, "All(${contacts.size})")
            updateAdapter(contacts)
        }

        // Add contact button
        val addContactButton = findViewById<LinearLayout>(R.id.addContactsButton)
        addContactButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchContacts { fetchedContacts ->
            contacts = fetchedContacts
            updateUI()
        }
    }

    private fun fetchContacts(onResult: (List<Contact>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedContacts = RetrofitInstance.contactApi.getContacts()
                withContext(Dispatchers.Main) {
                    if (fetchedContacts.isNotEmpty()) {
                        noContactsText.visibility = View.GONE
                    } else {
                        noContactsText.visibility = View.VISIBLE
                    }
                    onResult(fetchedContacts)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    noContactsText.text = "Failed to fetch data: ${e.message}"
                    onResult(emptyList()) // Return an empty list in case of an error
                }
                e.printStackTrace()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI() {
        contactsTitle.text = "Contacts(${contacts.size})"
        tabAll.text = "All(${contacts.size})"

        // Initialize adapter and set it to the list view
        updateAdapter(contacts)
    }

    private fun updateAdapter(newContacts: List<Contact>) {
        adapter = ContactAdapter(this, newContacts)
        listView.adapter = adapter
    }

    private fun updateTabColors(selectedTab: TextView, newText: String) {
        // Reset all tabs to default color
        tabAll.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
        tabDealer.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
        tabInvestor.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
        tabAll.text="All"
        tabDealer.text="Dealer"
        tabInvestor.text="Invester"

        // Set the selected tab's color and text
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.rosepink))
        selectedTab.text = newText
    }

    fun updateContactsList() {
        fetchContacts { fetchedContacts ->
            contacts = fetchedContacts
            updateUI()
        }
    }


}
