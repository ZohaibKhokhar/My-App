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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.easypropertydealer.RetrofitInstance.dealApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DealsActivity : AppCompatActivity() {

    private lateinit var addDeal: LinearLayout
     private lateinit var dealsListView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var  nodealtext:TextView
    private lateinit var dealsTitle:TextView
    private lateinit var recyclerView: RecyclerView
    private var deals: List<Deal> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deals)

        addDeal = findViewById(R.id.addDealsButton)
        dealsListView = findViewById(R.id.dealsListView)
        progressBar=findViewById(R.id.progressBar)
        nodealtext=findViewById(R.id.noDealsText)
        dealsTitle=findViewById(R.id.dealsTitle)

        val back = findViewById<ImageView>(R.id.backButton)
        back.setOnClickListener {
            finish()
        }

        addDeal.setOnClickListener {
            startActivity(Intent(this, AddDealActivity::class.java))
        }

        nodealtext.visibility= View.GONE
        progressBar.visibility= View.VISIBLE
        fetchDeals()
    }


    override fun onResume() {
        super.onResume()
        fetchDeals()
    }

    private fun fetchDeals() {
        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                deals = dealApi.getDeals()
                withContext(Dispatchers.Main) {

                    progressBar.visibility=View.GONE
                    if (deals.isEmpty()) {
                        nodealtext.visibility = View.VISIBLE
                    } else {
                        nodealtext.visibility = View.GONE
                        val adapter = DealAdapter(this@DealsActivity, deals)
                        dealsListView.adapter = adapter
                        dealsTitle.text = "Deals(${deals.size})"
                    }

                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@DealsActivity, "Failed to load deals", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


}