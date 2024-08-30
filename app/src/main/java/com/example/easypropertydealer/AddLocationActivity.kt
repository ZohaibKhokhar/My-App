package com.example.easypropertydealer

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easypropertydealer.RetrofitInstance.api
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class AddLocationActivity : AppCompatActivity() {

    private lateinit var countrySpinner: Spinner
    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        countrySpinner = findViewById(R.id.country_spinner)
        stateSpinner = findViewById(R.id.state_spinner)
        citySpinner = findViewById(R.id.city_spinner)

        // Load JSON data
        val locationJson = loadJsonFromFile()

        // Parse JSON data
        val type = object : TypeToken<Map<String, Map<String, List<String>>>>() {}.type
        val locationMap: Map<String, Map<String, List<String>>> = Gson().fromJson(locationJson, type)

        // Set up Spinners
        setupCountrySpinner(locationMap)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        val save = findViewById<LinearLayout>(R.id.saveLocation)
        save.setOnClickListener {
            // Get the selected values from the Spinners
            val country = countrySpinner.selectedItem.toString()
            val state = stateSpinner.selectedItem.toString()
            val city = citySpinner.selectedItem.toString()
            val locationName = findViewById<EditText>(R.id.location_name).text.toString()

            // Create a Location object
            val location = Location(
                id = 0, // Assuming id is auto-incremented by the database
                country = country,
                state = state,
                city = city,
                locationName = locationName
            )

            // Post the Location to the API
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.addLocation(location)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddLocationActivity, "Location added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddLocationActivity, "Failed to add location: ${response.toString()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddLocationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadJsonFromFile(): String {
        val inputStream: InputStream = resources.openRawResource(R.raw.locations)
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun setupCountrySpinner(locationMap: Map<String, Map<String, List<String>>>) {
        val countries = locationMap.keys.toList()
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        countrySpinner.adapter = countryAdapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCountry = countries[position]
                val states = locationMap[selectedCountry]?.keys?.toList()
                if (states != null) {
                    setupStateSpinner(locationMap[selectedCountry]!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupStateSpinner(stateMap: Map<String, List<String>>) {
        val states = stateMap.keys.toList()
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        stateSpinner.adapter = stateAdapter

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedState = states[position]
                val cities = stateMap[selectedState]
                if (cities != null) {
                    setupCitySpinner(cities)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupCitySpinner(cities: List<String>) {
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        citySpinner.adapter = cityAdapter
    }
}
