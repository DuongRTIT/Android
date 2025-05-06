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

class TaskAdapter(var taskList: MutableList<Task>) :
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
        holder.cbCompleted.setOnCheckedChangeListener(null)
        holder.cbCompleted.isChecked = task.status == "Đã hoàn thành"

        holder.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) "Đã hoàn thành" else "Chưa hoàn thành"

            holder.cbCompleted.isEnabled = false

            task.docId?.let { docId ->
                db.collection("Tasks").document(docId)
                    .update("status", newStatus)
                    .addOnSuccessListener {
                        taskList[position] = task.copy(status = newStatus)
                        notifyItemChanged(position)
                        holder.cbCompleted.isEnabled = true
                    }
                    .addOnFailureListener {
                        Toast.makeText(holder.itemView.context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                        holder.cbCompleted.isChecked = !isChecked
                        holder.cbCompleted.isEnabled = true
                    }
            }
        }
    }


    override fun getItemCount(): Int = taskList.size
}
