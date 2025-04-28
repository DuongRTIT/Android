package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.model.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(var taskList: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.tvTaskName.text = task.task_name
        holder.cbCompleted.setOnCheckedChangeListener(null) // Reset listener tránh lỗi
        holder.cbCompleted.isChecked = task.status == "Đã hoàn thành"

        holder.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) "Đã hoàn thành" else "Chưa hoàn thành"

            // Update giao diện trước cho nhanh (optimistic update)
            holder.cbCompleted.isEnabled = false  // Tạm disable trong lúc cập nhật

            db.collection("tasks").document(task.id.toString())
                .update("status", newStatus)
                .addOnSuccessListener {
                    // Update local list (nếu có cơ chế tự cập nhật)
                    Toast.makeText(holder.itemView.context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    holder.cbCompleted.isEnabled = true
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                    holder.cbCompleted.isChecked = !isChecked // rollback checkbox
                    holder.cbCompleted.isEnabled = true
                }
        }
    }

    override fun getItemCount(): Int = taskList.size
}
