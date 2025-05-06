package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.PendingTaskEventAdapter
import com.example.eventmanagementapp.adapter.TopEventAdapter
import com.example.eventmanagementapp.model.Event
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var iconHome: ImageView
    private lateinit var iconEvent: ImageView
    private lateinit var iconSupplier: ImageView
    private lateinit var iconUser: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerPendingTask: RecyclerView


    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view
        iconHome = findViewById(R.id.icon_home)
        iconEvent = findViewById(R.id.icon_event)
        iconSupplier = findViewById(R.id.icon_supplier)
        iconUser = findViewById(R.id.icon_user)
        recyclerView = findViewById(R.id.top_event_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load dữ liệu top 3
        loadTopRatedEvents { topList ->
            val adapter = TopEventAdapter(topList)
            recyclerView.adapter = adapter
        }

        recyclerPendingTask = findViewById(R.id.recycler_pending_tasks)
        recyclerPendingTask.layoutManager = LinearLayoutManager(this)

// Gọi hàm tải dữ liệu
        loadEventsWithPendingTasks { pendingList ->
            val pendingAdapter = PendingTaskEventAdapter(pendingList)
            recyclerPendingTask.adapter = pendingAdapter
        }


        // Footer navigation
        iconEvent.setOnClickListener {
            val intent = Intent(this, ListEventActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }

        iconHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }

        iconSupplier.setOnClickListener {
            val intent = Intent(this, ListSupplierActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }

        iconUser.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }
    }

    private fun loadTopRatedEvents(callback: (List<Pair<Event, Float>>) -> Unit) {
        db.collection("Feedbacks").get().addOnSuccessListener { snapshot ->
            val ratingMap = mutableMapOf<Int, MutableList<Int>>() // event_id -> ratings

            for (doc in snapshot.documents) {
                val eventId = doc.getLong("event_id")?.toInt() ?: continue
                val rating = doc.getLong("rating")?.toInt() ?: continue
                ratingMap.getOrPut(eventId) { mutableListOf() }.add(rating)
            }

            val avgRatings = ratingMap.mapValues { it.value.average().toFloat() }
            val topEventIds = avgRatings.entries.sortedByDescending { it.value }.take(3)

            val resultList = mutableListOf<Pair<Event, Float>>()
            var counter = 0

            for ((eventId, avg) in topEventIds) {
                db.collection("Events")
                    .whereEqualTo("id", eventId)
                    .whereEqualTo("status", "đã kết thúc") // 👈 chỉ lấy sự kiện đã kết thúc
                    .get()
                    .addOnSuccessListener { eventSnap ->
                        if (!eventSnap.isEmpty) {
                            val doc = eventSnap.documents[0]
                            val event = Event(
                                id = (doc.getLong("id") ?: 0).toInt(),
                                event_name = doc.getString("event_name") ?: "",
                                description = doc.getString("description") ?: "",
                                price = doc.getLong("price") ?: 0L,
                                imageURL = doc.getString("imageURL") ?: "",
                                start_time = doc.getTimestamp("start_time"),
                                end_time = doc.getTimestamp("end_time"),
                                created_at = doc.getTimestamp("created_at"),
                                status = doc.getString("status") ?: "",
                                user_id = (doc.getLong("user_id") ?: 0).toInt(),
                                venue_id = (doc.getLong("venue_id") ?: 0).toInt()
                            )
                            resultList.add(event to avg)
                        }
                        counter++
                        if (counter == topEventIds.size) {
                            callback(resultList)
                        }
                    }
                    .addOnFailureListener {
                        counter++
                        if (counter == topEventIds.size) {
                            callback(resultList)
                        }
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi tải Feedbacks: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadEventsWithPendingTasks(callback: (List<Pair<Event, Int>>) -> Unit) {
        val pendingMap = mutableMapOf<Int, Int>()

        db.collection("Tasks")
            .whereEqualTo("status", "Chưa hoàn thành")
            .get()
            .addOnSuccessListener { taskDocs ->
                if (taskDocs.isEmpty) {
                    Toast.makeText(this, "Không có task nào chưa hoàn thành", Toast.LENGTH_SHORT).show()
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                for (doc in taskDocs) {
                    val eventId = doc.getLong("event_id")?.toInt() ?: continue
                    pendingMap[eventId] = pendingMap.getOrDefault(eventId, 0) + 1
                }

                val resultList = mutableListOf<Pair<Event, Int>>()
                val eventIds = pendingMap.keys.toList()
                var counter = 0

                for (eventId in eventIds) {
                    db.collection("Events")
                        .whereEqualTo("id", eventId)
                        .get()
                        .addOnSuccessListener { eventDocs ->
                            if (!eventDocs.isEmpty) {
                                val doc = eventDocs.documents[0]
                                val status = doc.getString("status") ?: ""
                                if (status == "đang diễn ra" || status == "sắp diễn ra") {
                                    val event = Event(
                                        id = (doc.getLong("id") ?: 0).toInt(),
                                        event_name = doc.getString("event_name") ?: "",
                                        description = doc.getString("description") ?: "",
                                        price = doc.getLong("price") ?: 0L,
                                        imageURL = doc.getString("imageURL") ?: "",
                                        start_time = doc.getTimestamp("start_time"),
                                        end_time = doc.getTimestamp("end_time"),
                                        created_at = doc.getTimestamp("created_at"),
                                        status = status,
                                        user_id = (doc.getLong("user_id") ?: 0).toInt(),
                                        venue_id = (doc.getLong("venue_id") ?: 0).toInt()
                                    )
                                    resultList.add(event to pendingMap[eventId]!!)
                                }
                            }
                            counter++
                            if (counter == eventIds.size) {
                                callback(resultList)
                            }
                        }
                        .addOnFailureListener {
                            counter++
                            if (counter == eventIds.size) {
                                callback(resultList)
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi tải Tasks: ${it.message}", Toast.LENGTH_SHORT).show()
                callback(emptyList())
            }
    }

}
