package com.example.easypropertydealer

import android.app.Activity
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

class AddPropertyActivity : AppCompatActivity() {

    private var selectedContactId: Int? = null
    private var selectedLocationId:Int?=null
    private var selectedPurpose: String? = null
    private lateinit var contactTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val optional = findViewById<LinearLayout>(R.id.addressOptional)
        val addressDetail = findViewById<LinearLayout>(R.id.addressDetail)
        var addressText=findViewById<TextView>(R.id.tvOptional)

        optional.setOnClickListener {
            if (addressDetail.visibility == View.GONE) {
                addressDetail.visibility = View.VISIBLE
                optional.isActivated = true
                addressText.setTextColor(ContextCompat.getColor(this, R.color.activated_color))
            } else {
                addressDetail.visibility = View.GONE
                optional.isActivated = false
                addressText.setTextColor(ContextCompat.getColor(this, R.color.deactivated_color))
            }
        }

        // Initialize contactTextView
        contactTextView = findViewById(R.id.contactTextView)

        val selectContactButton = findViewById<LinearLayout>(R.id.selectContactButton)
        selectContactButton.setOnClickListener {
            val intent = Intent(this, ContactListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACT)
        }

        val locationButton = findViewById<LinearLayout>(R.id.selectLocationButton)
        locationButton.setOnClickListener {
            val intent = Intent(this, LocationListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_LOCATION)
        }



        // Spinner setup
        setupSpinners()


        // Initialize option buttons
        initializeOptionButtons()

        val saveButton = findViewById<LinearLayout>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveProperty()
        }

    }

    // Method to set up the spinners
    private fun setupSpinners() {
        val unitSpinner: Spinner = findViewById(R.id.unitSpinner)
        val units = arrayOf("Marla", "Kanal", "Akar", "Square Feet", "Square Yard", "Square Meter")
        val adapter1 = ArrayAdapter(this, R.layout.custom_spinner_item, units)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = adapter1

        val propertyTypeSpinner: Spinner = findViewById(R.id.propertyTypeSpinner)
        val options = arrayOf("Residential Plot", "Commercial Plot", "House", "Apartment", "Room", "Shop", "Office", "Plaza", "Factory", "Warehouse", "Commercial Building", "Farm House", "Agricultural Land", "Industrial Land", "Others")
        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        propertyTypeSpinner.adapter = adapter
    }

    // Method to handle saving the property
    private fun saveProperty() {
        // Retrieve values from the UI
        val propertyTypeSpinner = findViewById<Spinner>(R.id.propertyTypeSpinner)
        val selectedPropertyType = propertyTypeSpinner.selectedItem?.toString() ?: ""

        val areaEditText = findViewById<EditText>(R.id.areaEditText)
        val area = areaEditText.text.toString().toIntOrNull()

        val unitSpinner = findViewById<Spinner>(R.id.unitSpinner)
        val selectedUnit = unitSpinner.selectedItem?.toString() ?: ""

        val estimatedDemandEditText = findViewById<EditText>(R.id.estimatedDemand)
        val estimatedDemand = estimatedDemandEditText.text.toString().toDoubleOrNull()

        val noteEditText = findViewById<EditText>(R.id.note)
        val note = noteEditText.text.toString()

        val completeAddressEditText = findViewById<EditText>(R.id.completeAddress)
        val completeAddress = completeAddressEditText.text.toString()

        val plotNoEditText = findViewById<EditText>(R.id.plotno)
        val plotNo = plotNoEditText.text.toString()

        val blockEditText = findViewById<EditText>(R.id.block)
        val block = blockEditText.text.toString()

        val phaseEditText = findViewById<EditText>(R.id.phase)
        val phase = phaseEditText.text.toString()

        // Validate required fields
        if (selectedPropertyType.isEmpty()) {
            Toast.makeText(this, "Please select a property type", Toast.LENGTH_LONG).show()
            return
        }

        if (area == null || area <= 0) {
            Toast.makeText(this, "Please enter a valid area", Toast.LENGTH_LONG).show()
            return
        }

        if (estimatedDemand == null || estimatedDemand <= 0) {
            Toast.makeText(this, "Please enter a valid estimated demand", Toast.LENGTH_LONG).show()
            return
        }

        // Create a Property object with the collected data
        val property = Property(
            id = 0,
            contactId = selectedContactId ?: 1,
            purpose = selectedPurpose,
            propertyType = selectedPropertyType,
            area = area,
            areaUnit = selectedUnit,
            locationId = selectedLocationId,
            estimatedDemand = estimatedDemand,
            note = note,
            completeAddress = completeAddress,
            plotNo = plotNo,
            block = block,
            phase = phase
        )

        // Make the network request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.propertyApi.addProperty(property)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddPropertyActivity, "Property added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddPropertyActivity, "Failed to add property", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddPropertyActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Handle the result from ContactListActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_CONTACT && resultCode == Activity.RESULT_OK) {

            val contactId = data?.getIntExtra("contactId", -1)
            val contactName = data?.getStringExtra("contactName")
            val mobileNumber = data?.getStringExtra("contactMobileNo")


            val selectContactButton = findViewById<LinearLayout>(R.id.selectContactButton)
            val contactLayout = findViewById<LinearLayout>(R.id.contactdata)
            selectContactButton.visibility = View.GONE
            contactLayout.visibility = View.VISIBLE

            val mobileNo = findViewById<TextView>(R.id.contactPhone)
            val name = findViewById<TextView>(R.id.contactName)
            mobileNo.text = mobileNumber
            name.text = contactName

            // Update the selected contact ID
            selectedContactId = contactId
        }
        else if(requestCode==REQUEST_CODE_SELECT_LOCATION&&resultCode==Activity.RESULT_OK)
        {
            val locationId = data?.getIntExtra("locationId", -1)
            val locationName = data?.getStringExtra("name")
            val country = data?.getStringExtra("country")
            val state = data?.getStringExtra("state")
            val city = data?.getStringExtra("city")

            val selectButton:LinearLayout=findViewById(R.id.selectLocationButton)
            val selectedLocation:LinearLayout=findViewById(R.id.locationSelected)
            selectButton.visibility=View.GONE
            selectedLocation.visibility=View.VISIBLE

            val name=findViewById<TextView>(R.id.locationNameTextView)
            val countrytv=findViewById<TextView>(R.id.countryTextView)
            val statetv=findViewById<TextView>(R.id.stateTextView)
            val citytv=findViewById<TextView>(R.id.cityTextView)
            name.text=locationName
            countrytv.text=country
            statetv.text=state
            citytv.text=city
            selectedLocationId=locationId

        }

    }

    companion object {
        const val REQUEST_CODE_SELECT_CONTACT = 1001
        const val REQUEST_CODE_SELECT_LOCATION = 1002
    }

    private fun initializeOptionButtons() {
        val saleOptionLayout = findViewById<LinearLayout>(R.id.saleOptionLayout)
        val purchaseOptionLayout = findViewById<LinearLayout>(R.id.purchaseOptionLayout)
        val tenantOptionLayout = findViewById<LinearLayout>(R.id.tenantOptionLayout)
        val rentOutOptionLayout = findViewById<LinearLayout>(R.id.rentOutOptionLayout)

        // Set labels
        setButtonLabel(saleOptionLayout, "Sale")
        setButtonLabel(purchaseOptionLayout, "Purchase")
        setButtonLabel(tenantOptionLayout, "Tenant")
        setButtonLabel(rentOutOptionLayout, "Rent Out")

        saleOptionLayout.setOnClickListener {
            selectOption(saleOptionLayout, "Sale")
        }

        purchaseOptionLayout.setOnClickListener {
            selectOption(purchaseOptionLayout, "Purchase")
        }

        tenantOptionLayout.setOnClickListener {
            selectOption(tenantOptionLayout, "Tenant")
        }

        rentOutOptionLayout.setOnClickListener {
            selectOption(rentOutOptionLayout, "Rent Out")
        }
    }

    private fun setButtonLabel(layout: LinearLayout, text: String) {
        val labelTextView = layout.findViewById<TextView>(R.id.label)
        labelTextView.text = text
    }

    private fun selectOption(selectedLayout: LinearLayout, purpose: String) {
        // Deselect previous option
        deselectAllOptions()

        // Select the new option
        val selectedImage = selectedLayout.findViewById<ImageView>(R.id.icon)
        selectedImage.visibility = View.VISIBLE
        selectedImage.setColorFilter(ContextCompat.getColor(this, R.color.rosepink))
        selectedLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        val textview=selectedLayout.findViewById<TextView>(R.id.label)
        textview.setTextColor(ContextCompat.getColor(this, R.color.rosepink))

        selectedPurpose = purpose
    }

    private fun deselectAllOptions() {
        val layouts = listOf(
            findViewById<LinearLayout>(R.id.saleOptionLayout),
            findViewById<LinearLayout>(R.id.purchaseOptionLayout),
            findViewById<LinearLayout>(R.id.tenantOptionLayout),
            findViewById<LinearLayout>(R.id.rentOutOptionLayout)
        )

        for (layout in layouts) {
            val imageView = layout.findViewById<ImageView>(R.id.icon)
            val textView=layout.findViewById<TextView>(R.id.label)
            textView.setTextColor(ContextCompat.getColor(this, R.color.black))
            imageView.visibility = View.GONE
            imageView.clearColorFilter() // Remove tint color
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.gray)) // Optional background color change
        }
    }
}
