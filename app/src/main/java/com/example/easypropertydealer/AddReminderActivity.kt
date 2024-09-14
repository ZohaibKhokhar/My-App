package com.example.easypropertydealer
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class AddReminderActivity : AppCompatActivity() {

    private lateinit var agreementDateLayout: LinearLayout
    private lateinit var dateSelectedTextView: TextView
    private lateinit var contactLayout: LinearLayout
    private lateinit var propertyLayout: LinearLayout
    private lateinit var dealLayout: LinearLayout
    private lateinit var saveButton: LinearLayout
    private val calendar = Calendar.getInstance()
    private var selectedPurpose: String? = null
    private lateinit var selectPropertyButton: LinearLayout
    private lateinit var selectContactButton: LinearLayout
    private lateinit var selectDealButton: LinearLayout//i have written location in pllace of 
    private var selectedPurposeId:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        // Initialize views
        agreementDateLayout = findViewById(R.id.agreementDate)
        dateSelectedTextView = findViewById(R.id.dateSelected)
        selectPropertyButton = findViewById(R.id.selectPropertyButton)
        selectContactButton = findViewById(R.id.selectContactButton)
        selectDealButton = findViewById(R.id.selectLocationButton)
        saveButton = findViewById(R.id.saveReminder)
        contactLayout=findViewById(R.id.contactdata)
        propertyLayout=findViewById(R.id.propertydata)
        dealLayout=findViewById(R.id.dealdata)



        setupSpinners()
        initializeOptionButtons()

        agreementDateLayout.setOnClickListener {
            showDatePicker()
        }


        selectContactButton.setOnClickListener {
            val intent = Intent(this, ContactListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACT)
        }

        selectPropertyButton.setOnClickListener {

            val intent = Intent(this, PropertyListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_Property)
        }

        selectDealButton.setOnClickListener {

            val intent = Intent(this, DealListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_Deal)
        }


        saveButton.setOnClickListener {
            saveReminder()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddReminderActivity.REQUEST_CODE_SELECT_CONTACT && resultCode == Activity.RESULT_OK) {

            val contactId = data?.getIntExtra("contactId", -1)
            val contactName = data?.getStringExtra("contactName")
            val mobileNumber = data?.getStringExtra("contactMobileNo")



            selectContactButton.visibility = View.GONE
            contactLayout.visibility = View.VISIBLE

            val mobileNo = findViewById<TextView>(R.id.contactPhone)
            val name = findViewById<TextView>(R.id.contactName)
            mobileNo.text = mobileNumber
            name.text = contactName


            if (contactId != null) {
                selectedPurposeId =contactId
            }
        }
        else if (requestCode == AddReminderActivity.REQUEST_CODE_SELECT_Property && resultCode == Activity.RESULT_OK) {
            val propertyId = data?.getIntExtra("propertyId", -1)
            val purpose = data?.getStringExtra("purpose")
            val area = data?.getFloatExtra("area", 0f)
            val areaUnit = data?.getStringExtra("areaUnit")
            val propertyType = data?.getStringExtra("propertyType")
            val demand = data?.getStringExtra("demand")
            val address = data?.getStringExtra("address")


            selectPropertyButton.visibility = View.GONE
            propertyLayout.visibility = View.VISIBLE

            val propertyIdTextView = findViewById<TextView>(R.id.address)
            val propertyTypeTextView = findViewById<TextView>(R.id.propertyType)
            val propertyAreaTextView = findViewById<TextView>(R.id.propertyArea)

            propertyIdTextView.text = address
            propertyTypeTextView.text = propertyType
            propertyAreaTextView.text = "${area?.toString()} $areaUnit" // Format area

            if (propertyId != null) {
                selectedPurposeId =propertyId
            }

        }
        else if (requestCode == AddReminderActivity.REQUEST_CODE_SELECT_Deal && resultCode == Activity.RESULT_OK) {

            val dealId = data?.getIntExtra("dealId", -1)
            val date = data?.getStringExtra("date")
            val salePrice = data?.getFloatExtra("salePrice", 0f) // Use Float or Double
            val note = data?.getStringExtra("note")




            selectDealButton.visibility = View.GONE
            dealLayout.visibility = View.VISIBLE

            val salePriceTextView = findViewById<TextView>(R.id.dealPrice)
            val dealDate=findViewById<TextView>(R.id.dealDate)
            val dealNote=findViewById<TextView>(R.id.Dnote)
            dealNote.text=note
            salePriceTextView.text = salePrice?.toString()
            dealDate.text=date

            if (dealId != null) {
                selectedPurposeId =dealId
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_CONTACT = 1001
        private const val REQUEST_CODE_SELECT_Property = 1005
        private const val REQUEST_CODE_SELECT_Deal = 1006
    }

    private fun setupSpinners() {
        val unitSpinner: Spinner = findViewById(R.id.reminderSpinner)
        val units = arrayOf("Call", "Meeting", "Site Visit", "Other")
        val adapter1 = ArrayAdapter(this, R.layout.custom_spinner_item, units)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = adapter1
    }

    private fun initializeOptionButtons() {
        val contactOptionLayout = findViewById<LinearLayout>(R.id.contactOptionLayout)
        val propertyOptionLayout = findViewById<LinearLayout>(R.id.propertyOptionLayout)
        val dealOptionLayout = findViewById<LinearLayout>(R.id.DealOptionLayout)

        setButtonLabel(contactOptionLayout, "Contact")
        setButtonLabel(propertyOptionLayout, "Property")
        setButtonLabel(dealOptionLayout, "Deal")

        contactOptionLayout.setOnClickListener {
            selectOption(contactOptionLayout, "Contact")
        }

        propertyOptionLayout.setOnClickListener {
            selectOption(propertyOptionLayout, "Property")
        }

        dealOptionLayout.setOnClickListener {
            selectOption(dealOptionLayout, "Deal")
        }
    }

    private fun setButtonLabel(layout: LinearLayout, text: String) {
        val labelTextView = layout.findViewById<TextView>(R.id.label)
        labelTextView.text = text
    }

    private fun selectOption(selectedLayout: LinearLayout, purpose: String) {
        deselectAllOptions()

        val selectedImage = selectedLayout.findViewById<ImageView>(R.id.icon)
        selectedImage.visibility = View.VISIBLE
        selectedImage.setColorFilter(ContextCompat.getColor(this, R.color.rosepink))
        selectedLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        val selectedLabel = selectedLayout.findViewById<TextView>(R.id.label)
        selectedLabel.setTextColor(ContextCompat.getColor(this, R.color.rosepink))

        if (purpose == "Property") {
            selectPropertyButton.visibility = View.VISIBLE
            selectContactButton.visibility = View.GONE
            contactLayout.visibility = View.GONE
            selectDealButton.visibility = View.GONE
            dealLayout.visibility = View.GONE
        } else if (purpose == "Deal") {
            selectPropertyButton.visibility = View.GONE
            propertyLayout.visibility=View.GONE
            selectContactButton.visibility = View.GONE
            contactLayout.visibility = View.GONE
            selectDealButton.visibility = View.VISIBLE
        } else {
            selectPropertyButton.visibility = View.GONE
            propertyLayout.visibility=View.GONE
            selectDealButton.visibility = View.GONE
            dealLayout.visibility = View.GONE
            selectContactButton.visibility = View.VISIBLE
        }

        selectedPurpose = purpose
    }

    private fun deselectAllOptions() {
        val layouts = listOf(
            findViewById<LinearLayout>(R.id.contactOptionLayout),
            findViewById<LinearLayout>(R.id.propertyOptionLayout),
            findViewById<LinearLayout>(R.id.DealOptionLayout)
        )

        for (layout in layouts) {
            val imageView = layout.findViewById<ImageView>(R.id.icon)
            val textview=layout.findViewById<TextView>(R.id.label)
            textview.setTextColor(ContextCompat.getColor(this, R.color.black))
            imageView.visibility = View.GONE

            imageView.clearColorFilter() // Remove tint color
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                dateSelectedTextView.text = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun saveReminder() {
        val reminderPurpose = selectedPurpose ?: run {
            Toast.makeText(this@AddReminderActivity, "Reminder purpose is not selected", Toast.LENGTH_SHORT).show()
            return
        }

        val unitSpinner: Spinner = findViewById(R.id.reminderSpinner)
        val about = unitSpinner.selectedItem?.toString() ?: run {
            Toast.makeText(this@AddReminderActivity, "Please select an option from the spinner", Toast.LENGTH_SHORT).show()
            return
        }

        val dateAndTime = dateSelectedTextView.text.toString()
        val detail = findViewById<EditText>(R.id.detail).text.toString()

        // Check if any field is empty
        if (dateAndTime.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this@AddReminderActivity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val reminder = Reminder(
            id = 0, //
            reminderPurpose = reminderPurpose,
            purposeId = selectedPurposeId,
            about = about,
            dateAndTime = dateAndTime,
            detail = detail
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.reminderApi.addReminder(reminder)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddReminderActivity,
                            "Reminder saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddReminderActivity,
                            "Error saving reminder",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddReminderActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
