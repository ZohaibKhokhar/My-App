package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ContactsActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)


        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val db = MySQLiteHelper(this, "MyDataBase", null, 3)
        val contacts = db.getAllContacts()

        val textView = findViewById<TextView>(R.id.noContactText)
        if (contacts.isNotEmpty()) {
            textView.visibility = View.GONE
        }
        val contactsTitle = findViewById<TextView>(R.id.contactsTitle)
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabDealer = findViewById<TextView>(R.id.tabDealer)
        val tabInvestor = findViewById<TextView>(R.id.tabInvestor)
        contactsTitle.text = "Contacts(${contacts.size})"
        tabAll.text = "All(${contacts.size})"
        tabDealer.text="Dealer(${contacts.count { it.dealer }})"
        tabInvestor.text="Invester(${contacts.count { it.investor }})"

        tabDealer.setOnClickListener{
            tabDealer.setTextColor(ContextCompat.getColor(this, R.color.rosepink))
            tabAll.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
            tabInvestor.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
            val filteredContacts = contacts.filter { it.dealer }
            adapter = ContactAdapter(this, filteredContacts)
            listView.adapter = adapter
        }

        tabInvestor.setOnClickListener{
            tabDealer.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
            tabAll.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
            tabInvestor.setTextColor(ContextCompat.getColor(this, R.color.rosepink))
            val filteredContacts = contacts.filter { it.investor }
            adapter = ContactAdapter(this, filteredContacts)
            listView.adapter = adapter
        }

        tabAll.setOnClickListener {
            tabDealer.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
            tabAll.setTextColor(ContextCompat.getColor(this, R.color.rosepink))
            tabInvestor.setTextColor(ContextCompat.getColor(this, R.color.darkgray))
            val contacts2=db.getAllContacts()
            adapter = ContactAdapter(this, contacts2)
            listView.adapter = adapter
        }

        listView = findViewById(R.id.contactsList)
        adapter = ContactAdapter(this, contacts)
        listView.adapter = adapter

        val addContactButton = findViewById<LinearLayout>(R.id.addContactsButton)
        addContactButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateContactsList()
    }

    fun updateContactsList() {
        val db = MySQLiteHelper(this, "MyDataBase", null, 3)
        val contacts = db.getAllContacts()

        val contactsTitle = findViewById<TextView>(R.id.contactsTitle)
        val tabAll = findViewById<TextView>(R.id.tabAll)
        val tabDealer = findViewById<TextView>(R.id.tabDealer)
        val tabInvestor = findViewById<TextView>(R.id.tabInvestor)
        contactsTitle.text = "Contacts(${contacts.size})"
        tabAll.text = "All(${contacts.size})"
        tabDealer.text="Dealer(${contacts.count { it.dealer }})"
        tabInvestor.text="Invester(${contacts.count { it.investor }})"

        adapter = ContactAdapter(this, contacts)
        listView.adapter = adapter
    }


}
