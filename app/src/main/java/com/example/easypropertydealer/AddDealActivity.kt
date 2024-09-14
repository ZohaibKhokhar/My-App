package com.example.easypropertydealer

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.easypropertydealer.AddPropertyActivity.Companion
import com.example.easypropertydealer.RetrofitInstance.dealApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.properties.Delegates

class AddDealActivity : AppCompatActivity() {

    private lateinit var date:TextView
    private lateinit var agreeDate:LinearLayout
    private lateinit var saveDeal:LinearLayout
    private lateinit var selectPropertyButton: LinearLayout
    private lateinit var propertyLayout: LinearLayout
    private var selectedContactId:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deal)

        date = findViewById(R.id.dateSelected)
        agreeDate = findViewById(R.id.agreementDate)
        saveDeal = findViewById(R.id.saveDeal)
        propertyLayout=findViewById(R.id.propertydata)
        selectPropertyButton=findViewById(R.id.selectPropertyButton)

        agreeDate.setOnClickListener {
            openDatePicker(it)
        }

        val documentDetail: LinearLayout = findViewById(R.id.documentsDetail)
        val documentSwitch: LinearLayout = findViewById(R.id.document)
        val documentTextView: TextView = findViewById(R.id.doctext) // The TextView to change color

        documentDetail.setOnClickListener {
            if (documentSwitch.visibility == View.GONE) {
                documentSwitch.visibility = View.VISIBLE
                documentDetail.isActivated = true
                documentTextView.setTextColor(ContextCompat.getColor(this, R.color.activated_color))  // Change text color when activated
            } else {
                documentSwitch.visibility = View.GONE
                documentDetail.isActivated = false
                documentTextView.setTextColor(ContextCompat.getColor(this, R.color.deactivated_color))  // Change text color when deactivated
            }
        }


        selectPropertyButton.setOnClickListener {
            val intent = Intent(this, PropertyListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_Property)
        }




        val commission: LinearLayout = findViewById(R.id.commission)
        val commisionDetail: LinearLayout = findViewById(R.id.commisionDetails)
        val tvcommission: TextView = findViewById(R.id.tvCommision) // The TextView to change color

       commission.setOnClickListener {
            if (commisionDetail.visibility == View.GONE) {
                commisionDetail.visibility = View.VISIBLE
                commission.isActivated = true
                tvcommission.setTextColor(ContextCompat.getColor(this, R.color.activated_color))  // Change text color when activated
            } else {
                commisionDetail.visibility = View.GONE
                commission.isActivated = false
                tvcommission.setTextColor(ContextCompat.getColor(this, R.color.deactivated_color))  // Change text color when deactivated
            }
        }

        //save Deal Button
        saveDeal.setOnClickListener{
           saveDeal()
            finish()
        }


    }//oncreate ()



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddDealActivity.REQUEST_CODE_SELECT_Property && resultCode == Activity.RESULT_OK) {
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
                selectedContactId =propertyId
            }

        }
    }


    companion object {
        private const val REQUEST_CODE_SELECT_Property = 1005
    }

    private fun saveDeal() {
        // Get values from UI elements
        val finalSalePriceText = findViewById<EditText>(R.id.salePrice).text.toString()
        val agreementDateText = findViewById<TextView>(R.id.dateSelected).text.toString()
        val documentsTransferred = findViewById<Switch>(R.id.switchDocuments).isChecked
        val note = findViewById<EditText>(R.id.dealNote).text.toString().takeIf { it.isNotEmpty() }
        val commissionFromBuyer = findViewById<EditText>(R.id.commisionFromBuyer).text.toString().toDoubleOrNull()
        val commissionFromSeller = findViewById<EditText>(R.id.Commisionfromseller).text.toString().toDoubleOrNull()

        // Validation: Check if required fields are empty
        if (finalSalePriceText.isEmpty()) {
            Toast.makeText(this, "Sale Price is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (agreementDateText.isEmpty()) {
            Toast.makeText(this, "Agreement Date is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedContactId == null || selectedContactId == 0) {
            Toast.makeText(this, "Contact is required", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse finalSalePrice after validation
        val finalSalePrice = finalSalePriceText.toDoubleOrNull() ?: 0.0

        // Create a Deal object
        val deal = Deal(
            id = 0,
            contactId = selectedContactId,
            finalSalePrice = finalSalePrice,
            agreementDate = agreementDateText,
            documentsTransfered = documentsTransferred,
            note = note,
            commissionFromBuyer = commissionFromBuyer,
            commissionFromSeller = commissionFromSeller
        )

        // Call Retrofit API to save the deal
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = dealApi.addDeal(deal)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddDealActivity, "Deal saved successfully", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    } else {
                        Toast.makeText(this@AddDealActivity, "Failed to save deal: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddDealActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    fun openDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format the selected date and set it in the EditText
            val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            date.setText(dateString)
        }, year, month, day)

        datePickerDialog.show()
    }
}
