package com.example.eventmanagementapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.TaskAdapter
import com.example.eventmanagementapp.model.Task
import com.google.firebase.firestore.FirebaseFirestore

class ListTaskActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTask: Button
    private lateinit var edtTaskName: EditText
    private lateinit var taskAdapter: TaskAdapter
    private var taskList = mutableListOf<Task>()

    private val db = FirebaseFirestore.getInstance()
    private var eventId: Int = 0 // ID sự kiện được nhận từ Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_list)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        btnAddTask = findViewById(R.id.btnAddTask)
        edtTaskName = findViewById(R.id.edtTaskName)

        eventId = intent.getIntExtra("eventId", 0)
        if (eventId == 0) {
            Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupRecyclerView()
        loadTasks()

        btnAddTask.setOnClickListener {
            val taskName = edtTaskName.text.toString().trim()
            if (taskName.isNotEmpty()) {
                addTask(taskName)
                edtTaskName.text.clear() // Clear sau khi thêm
            } else {
                Toast.makeText(this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
    }

    private fun loadTasks() {
        db.collection("tasks")
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener { documents ->
                taskList.clear()
                for (doc in documents) {
                    val task = Task(
                        id = (doc.getLong("id") ?: 0L).toInt(),
                        event_id = (doc.getLong("event_id") ?: 0L).toInt(),
                        task_name = doc.getString("task_name") ?: "",
                        status = doc.getString("status") ?: "Chưa hoàn thành"
                    )
                    taskList.add(task)
                }
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không tải được công việc", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTask(taskName: String) {
        val newId = (0..1000000).random()  // tạo ID ngẫu nhiên

        val taskData = hashMapOf(
            "id" to newId,
            "event_id" to eventId,
            "task_name" to taskName,
            "status" to "Chưa hoàn thành"
        )

        db.collection("tasks")
            .document(newId.toString()) // Đặt document ID theo số id
            .set(taskData)
            .addOnSuccessListener {
                loadTasks()
                Toast.makeText(this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Thêm công việc thất bại", Toast.LENGTH_SHORT).show()
            }
    }
}
