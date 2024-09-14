package com.example.easypropertydealer

import ContactAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationListActivity : AppCompatActivity() {

    private lateinit var adapter:LocationAdapter
    private lateinit var listView: ListView
    private var locations: List<Location> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)

        listView = findViewById(R.id.locationsList)

        // Fetch contacts
        fetchLocations { fetchedLocations ->
            locations = fetchedLocations
            updateAdapter(locations)
        }

        // Set up item click listener to handle location selection
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            try {
                val location = locations[position]
                sendResult(location)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun fetchLocations(onResult: (List<Location>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedLocations = RetrofitInstance.api.getLocations()
                withContext(Dispatchers.Main) {
                    if (fetchedLocations.isNotEmpty()) {
                        onResult(fetchedLocations)
                    } else {
                        onResult(emptyList()) // No contacts fetched
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Return an empty list in case of an error
                }
                e.printStackTrace()
            }
        }
    }

    private fun updateAdapter(newLocations: List<Location>) {
        adapter = LocationAdapter(this, newLocations)
        listView.adapter = adapter
    }

    private fun sendResult(selectedlocation: Location) {

        println("Selected location: $selectedlocation")
        val resultIntent = Intent().apply {
            putExtra("locationId", selectedlocation.id)
            putExtra("country", selectedlocation.country)
            putExtra("state", selectedlocation.state)
            putExtra("city", selectedlocation.city)
            putExtra("name", selectedlocation.locationName)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}
