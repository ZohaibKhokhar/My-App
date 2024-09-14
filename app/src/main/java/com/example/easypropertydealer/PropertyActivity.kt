package com.example.easypropertydealer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class PropertyActivity : AppCompatActivity() {

    private lateinit var propertyListView: ListView
    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var noPropertyText: TextView
    private var properties: List<Property> = emptyList()
    private lateinit var filteredProperties: List<Property>

    private lateinit var tabAll: TextView
    private lateinit var tabSale: TextView
    private lateinit var tabPurchase: TextView
    private lateinit var tabTenant: TextView
    private lateinit var tabRent: TextView
    private lateinit var tabWithdraw: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val addProperty = findViewById<LinearLayout>(R.id.addProperty)
        addProperty.setOnClickListener {
            val intent = Intent(this, AddPropertyActivity::class.java)
            startActivity(intent)
        }

        progressBar = findViewById(R.id.progressBar)
        noPropertyText = findViewById(R.id.noPropertyText)
        noPropertyText.visibility = View.GONE
        propertyListView = findViewById(R.id.propertyListView)

        tabAll = findViewById(R.id.tabAll)
        tabSale = findViewById(R.id.tabSale)
        tabPurchase = findViewById(R.id.tabPurchase)
        tabTenant = findViewById(R.id.tabTenant)
        tabRent = findViewById(R.id.tabForRent)
        tabWithdraw = findViewById(R.id.tabWithDraw)

        setTabListeners()

        progressBar.visibility = View.VISIBLE
        fetchProperties()
    }

    override fun onResume() {
        super.onResume()
        fetchProperties()
    }

    private fun fetchProperties() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                properties = RetrofitInstance.propertyApi.getProperties()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (properties.isNotEmpty()) {
                        noPropertyText.visibility = View.GONE
                        // Initially display all properties
                        filterPropertiesByPurpose("All")
                    } else {
                        noPropertyText.visibility = View.VISIBLE
                        propertyListView.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PropertyActivity, "Error fetching properties: ${e.message}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    noPropertyText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setTabListeners() {
        tabAll.setOnClickListener { filterPropertiesByPurpose("All") }
        tabSale.setOnClickListener { filterPropertiesByPurpose("Sale") }
        tabPurchase.setOnClickListener { filterPropertiesByPurpose("Purchase") }
        tabTenant.setOnClickListener { filterPropertiesByPurpose("Tenant") }
        tabRent.setOnClickListener { filterPropertiesByPurpose("Rentout") }
        tabWithdraw.setOnClickListener { filterPropertiesByPurpose("Withdraw") }
    }

    private fun filterPropertiesByPurpose(purpose: String) {
        // Change tab colors
        resetTabColors()
        resetTabText()
        when (purpose) {
            "All" -> {
                tabAll.setTextColor(resources.getColor(R.color.rosepink))
                filteredProperties = properties
                tabAll.text = "All(${filteredProperties.size})"
            }
            "Sale" -> {
                tabSale.setTextColor(resources.getColor(R.color.rosepink))
                filteredProperties = properties.filter { it.purpose == "Sale" }
                tabSale.text = "Sale(${filteredProperties.size})"
            }
            "Purchase" -> {
                tabPurchase.setTextColor(resources.getColor(R.color.rosepink))
                filteredProperties = properties.filter { it.purpose == "Purchase" }
                tabPurchase.text = "Purchase(${filteredProperties.size})"
            }
            "Tenant" -> {
                tabTenant.setTextColor(resources.getColor(R.color.rosepink))
                filteredProperties = properties.filter { it.purpose == "Tenant" }
                tabTenant.text = "Tenant(${filteredProperties.size})"
            }
            "Rentout" -> {
                tabRent.setTextColor(resources.getColor(R.color.rosepink))
                filteredProperties = properties.filter { it.purpose == "Rent Out" }
                tabRent.text = "Rent Out(${filteredProperties.size})"
            }
            "Withdraw" -> {
                tabWithdraw.setTextColor(resources.getColor(R.color.rosepink))
                filteredProperties = properties.filter {
                    it.purpose != "Sale" &&
                            it.purpose != "Purchase" &&
                            it.purpose != "Tenant" &&
                            it.purpose != "Rent Out"
                }
                tabWithdraw.text = "Withdraw(${filteredProperties.size})"
            }
        }

        // Update the ListView with the filtered properties
        propertyAdapter = PropertyAdapter(this@PropertyActivity, filteredProperties, RetrofitInstance.propertyApi)
        propertyListView.adapter = propertyAdapter
    }

    private fun resetTabColors() {
        tabAll.setTextColor(resources.getColor(R.color.darkgray))
        tabSale.setTextColor(resources.getColor(R.color.darkgray))
        tabPurchase.setTextColor(resources.getColor(R.color.darkgray))
        tabTenant.setTextColor(resources.getColor(R.color.darkgray))
        tabRent.setTextColor(resources.getColor(R.color.darkgray))
        tabWithdraw.setTextColor(resources.getColor(R.color.darkgray))
    }
    private fun resetTabText() {
        tabAll.text = "All"
        tabSale.text = "Sale"
        tabPurchase.text = "Purchase"
        tabTenant.text = "Tenant"
        tabRent.text = "Rent Out"
        tabWithdraw.text = "Withdraw"
    }

}
