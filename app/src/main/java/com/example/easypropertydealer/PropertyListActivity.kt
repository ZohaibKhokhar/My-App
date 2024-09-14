package com.example.easypropertydealer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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

class PropertyListActivity : AppCompatActivity() {

    private lateinit var propertyListView: ListView
    private lateinit var propertyAdapter: PropertyAdapter
    private var properties: List<Property> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_list)

        propertyListView = findViewById(R.id.PropertyList)

        propertyListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            try {
                val property = properties[position]
                sendResult(property)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fetchProperties()
    }


    private fun fetchProperties() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                properties = RetrofitInstance.propertyApi.getProperties()
                withContext(Dispatchers.Main) {
                    if (properties.isNotEmpty()) {
                        propertyAdapter = PropertyAdapter(this@PropertyListActivity, properties,RetrofitInstance.propertyApi)
                        propertyListView.adapter = propertyAdapter
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PropertyListActivity, "Error fetching properties: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PropertyListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun sendResult(selectedproperty: Property) {

        val resultIntent = Intent().apply {
            putExtra("propertyId", selectedproperty.id)
            putExtra("purpose", selectedproperty.purpose)
            putExtra("area", selectedproperty.area)
            putExtra("areaUnit", selectedproperty.areaUnit)
            putExtra("propertyType", selectedproperty.propertyType)
            putExtra("demand", selectedproperty.estimatedDemand)
            putExtra("address", selectedproperty.completeAddress)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }



}
