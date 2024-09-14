package com.example.easypropertydealer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PropertyAdapter(
    context: Context,
    private val properties: List<Property>,
    private val propertyApiService: PropertyApiService
) : ArrayAdapter<Property>(context, 0, properties) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.property_item, parent, false)
        val property = properties[position]

        val addressTextView = view.findViewById<TextView>(R.id.address)
        val typeTextView = view.findViewById<TextView>(R.id.propertyType)
        val areaTextView = view.findViewById<TextView>(R.id.propertyArea)
        val menuImageView = view.findViewById<ImageView>(R.id.menu)
        val startImageView = view.findViewById<ImageView>(R.id.itemImage)

        addressTextView.text = property.completeAddress
        typeTextView.text = property.propertyType
        areaTextView.text = "${property.area} ${property.areaUnit}"

        menuImageView.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.property_item_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.update -> {
                        updateProperty(property)
                        true
                    }
                    R.id.delete -> {
                        deleteProperty(property.id)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        return view
    }

    private fun updateProperty(property: Property) {
        // Start an activity or show a dialog to update the property
        val intent = Intent(context, EditPropertyActivity::class.java).apply {
            putExtra("property_id", property.id)
            putExtra("property_address", property.completeAddress)
            putExtra("property_type", property.propertyType)
            putExtra("property_area", property.area)
            putExtra("property_area_unit", property.areaUnit)
        }
        context.startActivity(intent)
    }

    private fun deleteProperty(propertyId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = propertyApiService.deleteProperty(propertyId)
                if (response.isSuccessful) {
                    // Remove property from the list and notify adapter
                    (properties as MutableList).removeIf { it.id == propertyId }
                    CoroutineScope(Dispatchers.Main).launch {
                        notifyDataSetChanged()
                    }
                } else {
                    // Handle error
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Failed to delete property", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle exception
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
