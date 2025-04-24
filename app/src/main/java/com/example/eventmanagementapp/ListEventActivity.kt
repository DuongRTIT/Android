package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.EventAdapter
import com.example.eventmanagementapp.model.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ListEventActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_event)

        recyclerView = findViewById(R.id.recycler_events)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter

        loadEventsFromFirestore()

        // Footer navigation
        findViewById<ImageView>(R.id.icon_home).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.icon_event).setOnClickListener {
            val intent = Intent(this, ListEventActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.icon_supplier).setOnClickListener {
            val intent = Intent(this, ListSupplierActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.icon_user).setOnClickListener {
            val userId = SessionManager.currentUserId
            if (userId != null) {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<FloatingActionButton>(R.id.fab_add_event).setOnClickListener {
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadEventsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Events")
            .get()
            .addOnSuccessListener { result ->
                eventList.clear()
                val now = Date()  // Date hiện tại để so sánh

                for (document in result) {
                    try {
                        val startTimestamp = document.getTimestamp("start_time")
                        val endTimestamp = document.getTimestamp("end_time")

                        val startDate = startTimestamp?.toDate()
                        val endDate = endTimestamp?.toDate()

                        // Tính trạng thái dựa trên Date
                        val newStatus = when {
                            startDate != null && endDate != null && now.before(startDate) -> "sắp diễn ra"
                            startDate != null && endDate != null && now.after(endDate) -> "đã kết thúc"
                            startDate != null && endDate != null && now >= startDate && now <= endDate -> "đang diễn ra"
                            else -> document.getString("status") ?: ""
                        }

                        val event = Event(
                            id = (document.getLong("id") ?: 0).toInt(),
                            event_name = document.getString("event_name") ?: "",
                            description = document.getString("description") ?: "",
                            price = document.getLong("price") ?: 0L,
                            imageURL = document.getString("imageURL") ?: "",
                            start_time = startTimestamp,
                            end_time = endTimestamp,
                            created_at = document.getTimestamp("created_at"),
                            status = newStatus,
                            user_id = (document.getLong("user_id") ?: 0).toInt(),
                            venue_id = (document.getLong("venue_id") ?: 0).toInt()
                        )

                        eventList.add(event)

                        val currentStatus = document.getString("status") ?: ""
                        if (currentStatus != newStatus) {
                            document.reference.update("status", newStatus)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Không thể tải sự kiện", Toast.LENGTH_SHORT).show()
            }
    }
}






