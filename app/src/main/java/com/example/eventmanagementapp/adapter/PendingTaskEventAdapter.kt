package com.example.eventmanagementapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventmanagementapp.ListTaskActivity
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Event
class PendingTaskEventAdapter(
    private val data: List<Pair<Event, Int>>
) : RecyclerView.Adapter<PendingTaskEventAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgEvent: ImageView = view.findViewById(R.id.img_event)
        val tvEventName: TextView = view.findViewById(R.id.tv_event_name)
        val tvTaskCount: TextView = view.findViewById(R.id.tv_task_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_task_event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (event, taskCount) = data[position]
        holder.tvEventName.text = event.event_name
        holder.tvTaskCount.text = "Còn $taskCount công việc chưa hoàn thành"
        Glide.with(holder.itemView.context)
            .load(event.imageURL)
            .into(holder.imgEvent)
        // Bắt sự kiện click
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ListTaskActivity::class.java)
            intent.putExtra("eventId", event.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = data.size
}
