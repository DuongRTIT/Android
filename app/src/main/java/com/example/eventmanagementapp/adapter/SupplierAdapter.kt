package com.example.eventmanagementapp.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.ListContractActivity
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Supplier

class SupplierAdapter(private val suppliers: List<Supplier>) :
    RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder>() {

    inner class SupplierViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        holder.tvService.text = "Loại dịch vụ: ${supplier.service_type}"
        holder.tvContact.text = "Liên hệ: ${supplier.contact_info}"

        Log.d("SUPPLIER_DEBUG", "Tên: ${supplier.supplier_name}, Dịch vụ: ${supplier.service_type}, Liên hệ: ${supplier.contact_info}")

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ListContractActivity::class.java)
            intent.putExtra("supplier_id", supplier.id)
            intent.putExtra("supplierName", supplier.supplier_name)
            intent.putExtra("contact_info", supplier.contact_info)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = suppliers.size
}
