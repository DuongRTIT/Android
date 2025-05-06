package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Activity

class ActivityAdapter(private val activityList: List<Activity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvActivityName: TextView = itemView.findViewById(R.id.tv_activity_name)
        val tvStartTime: TextView = itemView.findViewById(R.id.tv_start_time)
        val tvEndTime: TextView = itemView.findViewById(R.id.tv_end_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activityList[position]

        holder.tvActivityName.text = activity.activity_name
        // Convert Timestamp to readable date format
        holder.tvStartTime.text = "Bắt đầu: ${activity.start_time?.toDate()?.let { formatDate(it) }}"
        holder.tvEndTime.text = "Kết thúc: ${activity.end_time?.toDate()?.let { formatDate(it) }}"
    }

    override fun getItemCount(): Int = activityList.size

    // Function to format the timestamp to readable date
    private fun formatDate(timestamp: java.util.Date): String {
        val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return format.format(timestamp)
    }
}
