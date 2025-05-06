package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Contract
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ContractAdapter(private val contracts: List<Contract>) :
    RecyclerView.Adapter<ContractAdapter.ContractViewHolder>() {

    inner class ContractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEventName: TextView = itemView.findViewById(R.id.tv_event_name)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tv_created_at)
        val tvContractDetail: TextView = itemView.findViewById(R.id.tv_contract_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contract_item, parent, false)
        return ContractViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        val contract = contracts[position]

        // Format ngày giờ
        val createdAtFormatted = contract.created_at?.toDate()?.let {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
        } ?: "Không rõ"

        holder.tvCreatedAt.text = "Ngày tạo: $createdAtFormatted"
        holder.tvContractDetail.text = "Chi tiết: ${contract.contract_detail}"

        // Load tên sự kiện dựa trên event_id
        if (contract.event_id != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("Events")
                .whereEqualTo("id", contract.event_id)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val eventName = snapshot.documents[0].getString("event_name") ?: "Không rõ tên sự kiện"
                        holder.tvEventName.text = "Sự kiện: $eventName"
                    } else {
                        holder.tvEventName.text = "Sự kiện: Không tìm thấy"
                    }
                }
                .addOnFailureListener {
                    holder.tvEventName.text = "Sự kiện: Lỗi tải"
                }
        } else {
            holder.tvEventName.text = "Sự kiện: Không rõ"
        }
    }

    override fun getItemCount(): Int = contracts.size
}
