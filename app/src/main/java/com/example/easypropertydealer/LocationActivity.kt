package com.example.easypropertydealer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationActivity : AppCompatActivity() {

    private lateinit var locationListView: ListView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var mySQLiteHelper: MySQLiteHelper
    private lateinit var noLocationsText: TextView
    private lateinit var locationsTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locations)

        mySQLiteHelper = MySQLiteHelper(this, "MyDataBase", null, 4)
        locationListView = findViewById(R.id.location_list_view)
        noLocationsText = findViewById(R.id.noLocationsText)
        locationsTitle = findViewById(R.id.locationsTitle)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val addLocation = findViewById<LinearLayout>(R.id.addlocation)
        addLocation.setOnClickListener {
            val intent = Intent(this, AddLocationActivity::class.java)
            startActivity(intent)
        }

        // Fetch locations and update UI
        fetchLocations()
    }

    private fun fetchLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locations = RetrofitInstance.api.getLocations()
                withContext(Dispatchers.Main) {

                    if (locations.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            noLocationsText.visibility = View.GONE
                            locationsTitle.text = "Locations(${locations.size})"
                        }
                        locationAdapter = LocationAdapter(this@LocationActivity, locations)
                        locationListView.adapter = locationAdapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    noLocationsText.text = "data fetched successfully: ${e.message}"
                }
                e.printStackTrace()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        updateLocationsList()
    }

    fun updateLocationsList() {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locations = RetrofitInstance.api.getLocations()
                withContext(Dispatchers.Main) {
                    locationsTitle.text = "Locations(${locations.size})"
                    locationAdapter = LocationAdapter(this@LocationActivity, locations)
                    locationListView.adapter = locationAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    noLocationsText.text = "Error fetching data"
                }
            }
        }
    }
}
