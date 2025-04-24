package com.example.eventmanagementapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Supplier

class SupplierAdapter(private val suppliers: List<Supplier>) :
    RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder>() {

    class SupplierViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_supplier_name)
        val tvService: TextView = itemView.findViewById(R.id.tv_service_type)
        val tvContact: TextView = itemView.findViewById(R.id.tv_contact_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.supplier_item, parent, false)
        return SupplierViewHolder(view)
    }

    override fun onBindViewHolder(holder: SupplierViewHolder, position: Int) {
        val supplier = suppliers[position]
        holder.tvName.text = supplier.supplier_name
        Log.d("SUPPLIER_DEBUG", "Tên: ${supplier.supplier_name}, Dịch vụ: ${supplier.service_type}, Liên hệ: ${supplier.contact_info}")
        holder.tvService.text = "Loại dịch vụ: ${supplier.service_type}"
        holder.tvContact.text = "Liên hệ: ${supplier.contact_info}"

    }

    override fun getItemCount(): Int = suppliers.size
}
