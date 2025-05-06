package com.example.eventmanagementapp

import android.content.Intent
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

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<ImageView>(R.id.icon_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<ImageView>(R.id.icon_event).setOnClickListener {
            startActivity(Intent(this, ListEventActivity::class.java))
        }

        findViewById<ImageView>(R.id.icon_supplier).setOnClickListener {
            startActivity(Intent(this, ListSupplierActivity::class.java))
        }

        findViewById<ImageView>(R.id.icon_user).setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
    }

    private fun loadTasks() {
        db.collection("Tasks")
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener { documents ->
                val newTaskList = mutableListOf<Task>()
                for (doc in documents) {
                    val task = Task(
                        id = (doc.getLong("id") ?: 0L).toInt(),
                        event_id = (doc.getLong("event_id") ?: 0L).toInt(),
                        task_name = doc.getString("task_name") ?: "",
                        status = doc.getString("status") ?: "Chưa hoàn thành",
                        docId = doc.id // <-- lấy đúng document ID trong Firestore
                    )
                    newTaskList.add(task)
                }
                taskList.clear()
                taskList.addAll(newTaskList)
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không tải được công việc", Toast.LENGTH_SHORT).show()
            }
    }



    private fun addTask(taskName: String) {
        val tasksRef = db.collection("Tasks")

        tasksRef.get()
            .addOnSuccessListener { documents ->
                val currentCount = documents.size()  // số lượng document hiện tại
                val newId = currentCount + 1  // ID mới = số lượng hiện có + 1

                val taskData = hashMapOf(
                    "id" to newId,
                    "event_id" to eventId,
                    "task_name" to taskName,
                    "status" to "Chưa hoàn thành"
                )

                tasksRef.document(taskName)
                    .set(taskData)
                    .addOnSuccessListener {
                        loadTasks()
                        Toast.makeText(this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Thêm công việc thất bại", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể tạo ID mới", Toast.LENGTH_SHORT).show()
            }
    }

}
