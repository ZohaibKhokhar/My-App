package com.example.easypropertydealer

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.easypropertydealer.RetrofitInstance.api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddLocationActivity : AppCompatActivity() {

    private lateinit var countrySpinner: Spinner
    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var layout:LinearLayout
    private lateinit var  progressBar:ProgressBar
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        countrySpinner = findViewById(R.id.country_spinner)
        stateSpinner = findViewById(R.id.state_spinner)
        citySpinner = findViewById(R.id.city_spinner)
        layout=findViewById(R.id.layout_location_form)
        progressBar=findViewById(R.id.progressBar)


        // Fetch Authorization token and then fetch countries
        fetchAuthTokenAndCountries()

        val save = findViewById<LinearLayout>(R.id.saveLocation)
        save.setOnClickListener {
            // Get the selected values from the Spinners
            val country = countrySpinner.selectedItem?.toString()
            val state = stateSpinner.selectedItem?.toString()
            val city = citySpinner.selectedItem?.toString()
            val locationName = findViewById<EditText>(R.id.location_name).text.toString()

            // Check if any field is empty
            if (country.isNullOrEmpty() || state.isNullOrEmpty() || city.isNullOrEmpty() || locationName.isEmpty()) {
                Toast.makeText(this@AddLocationActivity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

    private fun fetchAuthTokenAndCountries() {
        // Perform network request to get auth token first, then fetch countries
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getAuthToken()
                if (response.isSuccessful) {
                    authToken = response.body()?.auth_token
                    if (authToken != null) {
                        fetchCountries()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddLocationActivity, "Failed to fetch auth token", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLocationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchCountries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCountries("Bearer $authToken")
                if (response.isSuccessful) {
                    val countries = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        setupCountrySpinner(countries)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLocationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCountrySpinner(countryList: List<Country>) {
        val countryNames = countryList.map { it.country_name }
        val countryAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, countryNames)
        countrySpinner.adapter = countryAdapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCountry = countryList[position].country_name
                fetchStates(selectedCountry)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchStates(countryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getStates(countryName, "Bearer $authToken")
                if (response.isSuccessful) {
                    val states = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        setupStateSpinner(states)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLocationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupStateSpinner(stateList: List<State>) {
        val stateNames = stateList.map { it.state_name }
        val stateAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, stateNames)
        stateSpinner.adapter = stateAdapter

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedState = stateList[position].state_name
                fetchCities(selectedState)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchCities(stateName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCities(stateName, "Bearer $authToken")
                if (response.isSuccessful) {
                    val cities = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        setupCitySpinner(cities)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddLocationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCitySpinner(cityList: List<City>) {
        val cityNames = cityList.map { it.city_name }
        val cityAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, cityNames)
        citySpinner.adapter = cityAdapter
    }
}
