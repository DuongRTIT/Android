package com.example.eventmanagementapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventmanagementapp.EndedEventDetailActivity
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Event

class TopEventAdapter(
    private val eventList: List<Pair<Event, Float>>
) : RecyclerView.Adapter<TopEventAdapter.TopEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopEventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.top_event_item, parent, false)
        return TopEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopEventViewHolder, position: Int) {
        val (event, avgRating) = eventList[position]

        holder.tvEventName.text = event.event_name
        holder.tvEventRating.text = "Rating trung bình: %.1f".format(avgRating)

        Glide.with(holder.itemView.context)
            .load(event.imageURL)
            //.placeholder(R.drawable.image_placeholder)
            .into(holder.imgEvent)

        // 👉 Khi click vào item thì mở EndedEventDetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EndedEventDetailActivity::class.java)
            intent.putExtra("eventId", event.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = eventList.size

    class TopEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEvent: ImageView = itemView.findViewById(R.id.img_event)
        val tvEventName: TextView = itemView.findViewById(R.id.tv_event_name)
        val tvEventRating: TextView = itemView.findViewById(R.id.tv_event_rating)
    }
}
