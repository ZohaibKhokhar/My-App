package com.example.easypropertydealer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DealAdapter(
    context: Context,
    private val deals: List<Deal>
) : ArrayAdapter<Deal>(context, 0, deals) {

    private class ViewHolder {
        lateinit var priceTextView: TextView
        lateinit var dateTextView: TextView
        lateinit var noteTextView: TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_deal, parent, false)
            viewHolder = ViewHolder().apply {
                priceTextView = view.findViewById(R.id.dealPrice)
                dateTextView = view.findViewById(R.id.dealDate)
                noteTextView = view.findViewById(R.id.dealNote)
            }
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val deal = getItem(position)
        viewHolder.priceTextView.text = "Price: ${deal?.finalSalePrice}"
        viewHolder.dateTextView.text = "Date: ${deal?.agreementDate}"
        viewHolder.noteTextView.text = "Note: ${deal?.note}"

        return view
    }
}
