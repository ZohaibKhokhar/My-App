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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.easypropertydealer.RetrofitInstance.dealApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DealListActivity : AppCompatActivity() {

    private lateinit var dealsListView: ListView
    private  var deals:List<Deal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_list)

        dealsListView = findViewById(R.id.DealList)
        fetchDeals()

        // Set up item click listener to handle contact selection
        dealsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (position >= 0 && position < deals.size) {
                val selectedDeal = deals[position]
                sendResult(selectedDeal)
            } else {
                Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun fetchDeals() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                deals = dealApi.getDeals() // Fetch deals from the API
                runOnUiThread {
                    val adapter = DealAdapter(this@DealListActivity, deals)
                    dealsListView.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@DealListActivity, "Failed to load deals", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendResult(selectedDeal: Deal) {

        val resultIntent = Intent().apply {
            putExtra("dealId", selectedDeal.id)
            putExtra("date", selectedDeal.agreementDate)
            putExtra("salePrice", selectedDeal.finalSalePrice)

        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


}
