package com.example.easypropertydealer
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AddContactActivity : AppCompatActivity() {

    private val PICK_CONTACT_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // Initialize views
        val tvMoreDetails: TextView = findViewById(R.id.tv_more_details)
        val llMoreDetails:LinearLayout=findViewById(R.id.ll_more_details)
        val detailsContainer: LinearLayout = findViewById(R.id.details_container)

        llMoreDetails.setOnClickListener {
            if (detailsContainer.visibility == View.GONE) {
                detailsContainer.visibility = View.VISIBLE
                tvMoreDetails.isActivated = true
                tvMoreDetails.setTextColor(ContextCompat.getColor(this, R.color.activated_color))
            } else {
                detailsContainer.visibility = View.GONE
                tvMoreDetails.isActivated = false
                tvMoreDetails.setTextColor(ContextCompat.getColor(this, R.color.deactivated_color))
            }
        }

        val saveBtn: LinearLayout = findViewById(R.id.saveContact)
        saveBtn.setOnClickListener {
            val nameEditText = findViewById<EditText>(R.id.edit_text_name)
            val phoneEditText = findViewById<EditText>(R.id.edit_text_mobile)
            val addressEditText = findViewById<EditText>(R.id.edit_text_address)
            val dealerSwitch = findViewById<Switch>(R.id.switch_dealer)
            val investorSwitch = findViewById<Switch>(R.id.switch_invester)
            val companyEditText = findViewById<EditText>(R.id.edit_text_company)
            val notesEditText = findViewById<EditText>(R.id.edit_text_notes)
            val emailEditText = findViewById<EditText>(R.id.edit_text_email)
            val websiteEditText = findViewById<EditText>(R.id.edit_text_website)

            // Retrieve text from EditTexts and states from Switches
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val dealer = dealerSwitch.isChecked
            val investor = investorSwitch.isChecked
            val company = companyEditText.text.toString().trim()
            val notes = notesEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val website = websiteEditText.text.toString().trim()

            // Check if required fields are filled
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show()
            }
            else {

                // Create a Contact object
                val contact = Contact(
                    id = 0,  // ID will be assigned by the server
                    name = name,
                    mobileNo = phone,
                    address = address,
                    dealer = dealer,
                    investor = investor,
                    companyName = company,
                    personDetails = notes,
                    email = email,
                    website = website
                )

                // Post the contact to the server
                postContact(contact)
            }
        }


        // Choose From Mobile Contacts Button
        val chooseContactsBtn: LinearLayout = findViewById(R.id.btn_choose_contacts)
        chooseContactsBtn.setOnClickListener {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            val contactUri = data?.data
            contactUri?.let { uri ->
                val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = contentResolver.query(uri, projection, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val displayName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        findViewById<EditText>(R.id.edit_text_name).setText(displayName)

                        // Fetch phone numbers
                        val phoneProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            phoneProjection,
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(contactId),
                            null
                        )
                        phoneCursor?.use { pCursor ->
                            if (pCursor.moveToFirst()) {
                                val phoneNumber = pCursor.getString(pCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                findViewById<EditText>(R.id.edit_text_mobile).setText(phoneNumber)
                            }
                        }

                        // Fetch email addresses
                        val emailProjection = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS)
                        val emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            emailProjection,
                            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                            arrayOf(contactId),
                            null
                        )
                        emailCursor?.use { eCursor ->
                            if (eCursor.moveToFirst()) {
                                val emailAddress = eCursor.getString(eCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                                findViewById<EditText>(R.id.edit_text_email).setText(emailAddress)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun postContact(contact: Contact) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<Contact> = RetrofitInstance.contactApi.addContact(contact)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Contact added successfully
                        showToast("Contact added successfully")
                        finish()
                    } else {
                        // Handle unsuccessful response
                        showToast("Failed to add contact: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
                e.printStackTrace()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
