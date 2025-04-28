package com.example.eventmanagementapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventmanagementapp.EndedEventDetailActivity
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Event
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(private val eventList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEvent: ImageView = itemView.findViewById(R.id.imgEvent)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        holder.tvTitle.text = event.event_name
        holder.tvDescription.text = event.description
        holder.tvStatus.text = event.status
        holder.tvDate.text = formatTimestamp(event.start_time)

        // Set background color by status
        val statusBg = when (event.status.lowercase(Locale.getDefault())) {
            "sắp diễn ra" -> R.drawable.status_bg_green
            "đã kết thúc" -> R.drawable.status_bg_red
            else -> R.drawable.status_bg_green
        }
        holder.tvStatus.setBackgroundResource(statusBg)

        // Load ảnh sự kiện
        Glide.with(holder.itemView.context)
            .load(event.imageURL ?: "")
            .centerCrop()
            .into(holder.imgEvent)

        // OnClick: Hiện toast (có thể mở EventDetailActivity nếu muốn)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (event.status.lowercase(Locale.getDefault()) == "đã kết thúc") {
                val intent = Intent(context, EndedEventDetailActivity::class.java)
                intent.putExtra("eventId", event.id)  // Gửi ID sự kiện đúng
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Sự kiện chưa kết thúc", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int = eventList.size

    private fun formatTimestamp(timestamp: Timestamp?): String {
        return timestamp?.toDate()?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
        } ?: ""
    }
}
