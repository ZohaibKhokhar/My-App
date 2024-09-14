package com.example.easypropertydealer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPropertyActivity : AppCompatActivity() {

    private lateinit var addressEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var areaEditText: EditText
    private lateinit var areaUnitEditText: EditText
    private lateinit var saveButton: LinearLayout
    private var propertyId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_property)

        addressEditText = findViewById(R.id.addressEditText)
        areaEditText = findViewById(R.id.areaEditText)
        areaUnitEditText = findViewById(R.id.areaUnitEditText)
        saveButton = findViewById(R.id.saveButton)

        // Retrieve property details from Intent
        propertyId = intent.getIntExtra("property_id", -1)
        addressEditText.setText(intent.getStringExtra("property_address"))
        areaEditText.setText(intent.getIntExtra("property_area", 0).toString())
        areaUnitEditText.setText(intent.getStringExtra("property_area_unit"))

        saveButton.setOnClickListener {
            updateProperty()
        }
    }

    private fun updateProperty() {
        val updatedAddress = addressEditText.text.toString()
        val updatedArea = areaEditText.text.toString().toIntOrNull() ?: 0
        val updatedAreaUnit = areaUnitEditText.text.toString()

        propertyId?.let { id ->
            val property = Property(
                id = id,
                completeAddress = updatedAddress,
                area = updatedArea,
                areaUnit = updatedAreaUnit
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.propertyApi.updateProperty(id, property)
                    if (response.isSuccessful) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(this@EditPropertyActivity, "Property updated successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(this@EditPropertyActivity, "Failed to update property", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@EditPropertyActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
