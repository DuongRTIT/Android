package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.ExpenseItem
import java.text.NumberFormat
import java.util.*

class ExpenseAdapter(private val expenseList: List<ExpenseItem>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExpenseName: TextView = itemView.findViewById(R.id.tv_expense_name)
        val tvExpensePrice: TextView = itemView.findViewById(R.id.tv_expense_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.tvExpenseName.text = expense.expense_name
        holder.tvExpensePrice.text = formatCurrency(expense.price)
    }

    override fun getItemCount(): Int = expenseList.size

    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return "${format.format(amount)} VND"
    }
}
