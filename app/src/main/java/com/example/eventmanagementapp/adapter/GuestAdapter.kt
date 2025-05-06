package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.databinding.GuestItemBinding
import com.example.eventmanagementapp.model.Guest

class GuestAdapter(private val guests: List<Guest>) : RecyclerView.Adapter<GuestAdapter.GuestViewHolder>() {

    inner class GuestViewHolder(val binding: GuestItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val binding = GuestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        val guest = guests[position]
        holder.binding.tvEmail.text = guest.email
    }

    override fun getItemCount(): Int = guests.size
}
