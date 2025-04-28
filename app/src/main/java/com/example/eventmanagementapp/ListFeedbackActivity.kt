package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.FeedbackAdapter
import com.example.eventmanagementapp.model.Feedback
import com.google.firebase.firestore.FirebaseFirestore

class ListFeedbackActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedbackAdapter
    private val feedbackList = mutableListOf<Feedback>()
    private lateinit var tvTotalFeedbacks: TextView
    private lateinit var tvAverageRating: TextView


    private var eventId: Int = 0 // ID sự kiện để xem feedback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_feedback)

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.recycler_feedbacks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FeedbackAdapter(feedbackList)
        recyclerView.adapter = adapter

        // Lấy eventId từ Intent
        eventId = intent.getIntExtra("event_id", 0)
        if (eventId != 0) {
            loadFeedbacks(eventId)
        } else {
            Toast.makeText(this, "Không tìm thấy sự kiện để xem phản hồi", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupFooterNavigation()
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        tvTotalFeedbacks = findViewById(R.id.tv_total_feedbacks)
        tvAverageRating = findViewById(R.id.tv_average_rating)

    }

    private fun loadFeedbacks(eventId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Feedbacks")
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener { result ->
                feedbackList.clear()
                var totalRating = 0f  // tổng số sao
                var count = 0         // số lượng feedback

                for (doc in result) {
                    try {
                        val feedback = Feedback(
                            docId       = doc.id,
                            id = doc.getLong("id")?.toInt(),
                            event_id = doc.getLong("event_id")?.toInt(),
                            guest_id = doc.getLong("guest_id")?.toInt(),
                            feedback_text = doc.getString("feedback_text") ?: "",
                            rating = doc.getLong("rating")?.toInt(),
                            created_at = doc.getTimestamp("created_at")
                        )
                        feedbackList.add(feedback)

                        // Tính tổng rating
                        if (feedback.rating != null) {
                            totalRating += feedback.rating
                            count++
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                adapter.notifyDataSetChanged()

                // ✅ Cập nhật thống kê
                tvTotalFeedbacks.text = "Tổng số feedback: $count"
                if (count > 0) {
                    val averageRating = totalRating / count
                    tvAverageRating.text = "Số sao trung bình: %.1f".format(averageRating)
                } else {
                    tvAverageRating.text = "Số sao trung bình: 0.0"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể tải phản hồi", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupFooterNavigation() {
        findViewById<ImageView>(R.id.icon_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }

        findViewById<ImageView>(R.id.icon_event).setOnClickListener {
            startActivity(Intent(this, ListEventActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }

        findViewById<ImageView>(R.id.icon_supplier).setOnClickListener {
            startActivity(Intent(this, ListSupplierActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
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
    }
}
