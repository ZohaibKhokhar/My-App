package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {

    private val PICK_CONTACT_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // Kotlin
        val tvMoreDetails: TextView = findViewById(R.id.tv_more_details)
        val detailsContainer: LinearLayout = findViewById(R.id.details_container)

        tvMoreDetails.setOnClickListener {

            if (detailsContainer.visibility == View.GONE) {
                detailsContainer.visibility = View.VISIBLE
                tvMoreDetails.isActivated = true
            } else {
                detailsContainer.visibility = View.GONE
                tvMoreDetails.isActivated = false
            }
        }


        val saveBtn:LinearLayout = findViewById(R.id.saveContact)
        saveBtn.setOnClickListener {

            val name=findViewById<EditText>(R.id.edit_text_name)
            val phone=findViewById<EditText>(R.id.edit_text_mobile)
            val address=findViewById<EditText>(R.id.edit_text_address)
            val dealer=findViewById<Switch>(R.id.switch_dealer)
            val investor=findViewById<Switch>(R.id.switch_investor)
            val company=findViewById<EditText>(R.id.edit_text_company)
            val notes=findViewById<EditText>(R.id.edit_text_notes)
            val email=findViewById<EditText>(R.id.edit_text_email)
            val website=findViewById<EditText>(R.id.edit_text_website)

            val db = MySQLiteHelper(this, "MyDataBase", null, 4)
            db.insertContact(
                name.text.toString(),               // name: String
                phone.text.toString(),              // mobileNo: String
                address.text.toString(),            // address: String?
                dealer.isChecked,                   // dealer: Boolean
                investor.isChecked,                 // investor: Boolean
                company.text.toString(),            // companyName: String?
                notes.text.toString(),              // personDetails: String?
                email.text.toString(),              // email: String?
                website.text.toString()             // website: String?
            )
            finish()
        }

        // Choose From Mobile Contacts Button
        val chooseContactsBtn:LinearLayout= findViewById(R.id.btn_choose_contacts)
        chooseContactsBtn.setOnClickListener {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            val contactUri = data?.data
            // Handle the contact data here (e.g., fetch name, phone number, etc.)
        }
    }
}
