package com.example.easypropertydealer

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationAdapter(private val context: Context, private val locations: List<Location>) : BaseAdapter() {

    private val dbHelper = MySQLiteHelper(context, "MyDataBase", null, 4)

    override fun getCount(): Int = locations.size

    override fun getItem(position: Int): Any = locations[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.location_item, parent, false)

        val location = getItem(position) as Location

        val countryTextView = view.findViewById<TextView>(R.id.countryTextView)
        val stateTextView = view.findViewById<TextView>(R.id.stateTextView)
        val cityTextView = view.findViewById<TextView>(R.id.cityTextView)
        val locationNameTextView = view.findViewById<TextView>(R.id.locationNameTextView)
        val locationImageView = view.findViewById<ImageView>(R.id.btnMore)

        countryTextView.text = location.country
        stateTextView.text = location.state
        cityTextView.text = location.city
        locationNameTextView.text = location.locationName

        locationImageView.setOnClickListener {
            showPopupMenu(locationImageView, location, position)
        }

        return view
    }


    private fun showPopupMenu(view: View, location: Location, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.locations_option_menu)  // Ensure you have a menu resource named location_options_menu.xml
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_update -> {
                    handleUpdate(location, position)
                    true
                }
                R.id.action_delete -> {
                    handleDelete(location, position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun handleUpdate(location: Location, position: Int) {
        val editText = EditText(context).apply {
            setText(location.locationName)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Update Location")
            .setMessage("Enter new location name:")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newLocationName = editText.text.toString()
                if (newLocationName.isNotEmpty()) {
                    val updatedLocation = Location(
                        id = location.id,
                        country = location.country,
                        state = location.state,
                        city = location.city,
                        locationName = newLocationName
                    )

                    val api = RetrofitInstance.api

                    // Launch a coroutine for the update operation
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = api.updateLocation(location.id, updatedLocation)
                            if (response.isSuccessful) {
                                // Switch to the main thread to update the UI
                                withContext(Dispatchers.Main) {
                                    println("Location updated successfully: ${response.body()}")
                                    if (context is LocationActivity) {
                                        (context as LocationActivity).updateLocationsList()
                                    }
                                }
                            } else {
                                // Handle error response on the main thread
                                withContext(Dispatchers.Main) {
                                    println("Failed to update location: ${response.errorBody()?.string()}")
                                    AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage("Failed to update location.")
                                        .setPositiveButton("OK", null)
                                        .create()
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            // Handle exception on the main thread
                            withContext(Dispatchers.Main) {
                                println("Exception during update: ${e.message}")
                                AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage("An error occurred: ${e.message}")
                                    .setPositiveButton("OK", null)
                                    .create()
                                    .show()
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    private fun handleDelete(location: Location, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Location")
            .setMessage("Are you sure you want to delete this location?")
            .setPositiveButton("Delete") { _, _ ->
                // Launch a coroutine to perform the delete operation
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val apiService = RetrofitInstance.api
                        val response = apiService.deleteLocation(location.id)
                        if (response.isSuccessful) {
                            // Refresh the locations list on the main thread
                            CoroutineScope(Dispatchers.Main).launch {
                                if (context is LocationActivity) {
                                    (context as LocationActivity).updateLocationsList()
                                }
                            }
                        } else {
                            // Handle the error on the main thread
                            CoroutineScope(Dispatchers.Main).launch {
                                // Show an error message or handle accordingly
                                AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage("Failed to delete location: ${response.errorBody()?.string()}")
                                    .setPositiveButton("OK", null)
                                    .create()
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        // Handle the exception on the main thread
                        CoroutineScope(Dispatchers.Main).launch {
                            AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage("An error occurred: ${e.message}")
                                .setPositiveButton("OK", null)
                                .create()
                                .show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
   }
}

