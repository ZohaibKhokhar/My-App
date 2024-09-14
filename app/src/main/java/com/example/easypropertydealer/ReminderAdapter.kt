package com.example.easypropertydealer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(
    private val context: Context,
    private val reminders: List<Reminder>
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.reminderPurpose.text = reminder.reminderPurpose
        holder.reminderAbout.text = reminder.about
        holder.reminderDateAndTime.text = reminder.dateAndTime
        holder.reminderDetail.text = reminder.detail ?: "No details available"
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderPurpose: TextView = itemView.findViewById(R.id.reminder_purpose)
        val reminderAbout: TextView = itemView.findViewById(R.id.reminder_about)
        val reminderDateAndTime: TextView = itemView.findViewById(R.id.reminder_date_and_time)
        val reminderDetail: TextView = itemView.findViewById(R.id.reminder_detail)
    }
}
