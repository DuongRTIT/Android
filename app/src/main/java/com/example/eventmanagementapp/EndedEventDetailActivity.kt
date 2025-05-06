package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventmanagementapp.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EndedEventDetailActivity : AppCompatActivity() {

    private lateinit var imgEvent: ImageView
    private lateinit var tvEventName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var tvVenue: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var btnFeedbackStats: Button
    private lateinit var btnGuestList: Button
    private lateinit var btnViewLocation: Button


    private var eventId: Int = 0  // nhận id của sự kiện từ intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ended_event_detail)

        // Ánh xạ view
        imgEvent = findViewById(R.id.img_event)
        tvEventName = findViewById(R.id.tv_event_name)
        tvDescription = findViewById(R.id.tv_event_description)
        tvCreatedAt = findViewById(R.id.tv_created_at)
        tvVenue = findViewById(R.id.tv_event_venue)
        tvStartTime = findViewById(R.id.tv_start_time)
        tvEndTime = findViewById(R.id.tv_end_time)
        btnFeedbackStats = findViewById(R.id.btn_feedback_stats)
        btnViewLocation=findViewById(R.id.btn_view_location)
        btnGuestList = findViewById(R.id.btn_guest_list) // 👉 Thêm ánh xạ nút

        // Nhận ID sự kiện từ Intent
        eventId = intent.getIntExtra("eventId", 0)

        if (eventId != 0) {
            loadEventDetail(eventId)
        } else {
            Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Nút back
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 👉 Xử lý nút mở danh sách khách mời
        btnGuestList.setOnClickListener {
            val intent = Intent(this, GuestListActivity::class.java)
            intent.putExtra("event_id", eventId)
            startActivity(intent)
        }

        // Nút Thống kê đánh giá
        btnFeedbackStats.setOnClickListener {
            val intent = Intent(this, ListFeedbackActivity::class.java)
            intent.putExtra("event_id", eventId)
            startActivity(intent)
        }

        btnViewLocation.setOnClickListener {
            val intent = Intent(this, EventLocationActivity::class.java)
            intent.putExtra("eventId", eventId)
            /*intent.putExtra("eventName", tvEventName.text.toString())
            intent.putExtra("eventVenue", tvVenue.text.toString())*/
            startActivity(intent)
        }



        // Footer navigation
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

    private fun loadEventDetail(id: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Events")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
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

                    updateUI(event)
                } else {
                    Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi tải sự kiện", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun loadVenueName(venueId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Venues")
            .whereEqualTo("id", venueId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val venueName = documents.documents[0].getString("venue_name") ?: "Không rõ địa điểm"
                    tvVenue.text = "Địa điểm: $venueName"
                } else {
                    tvVenue.text = "Địa điểm: Không rõ"
                }
            }
            .addOnFailureListener {
                tvVenue.text = "Địa điểm: Không rõ"
            }
    }

    private fun updateUI(event: Event) {
        Glide.with(this)
            .load(event.imageURL)
            .into(imgEvent)

        tvEventName.text = event.event_name
        tvDescription.text = event.description
        tvCreatedAt.text = "Ngày tạo: ${event.created_at?.let { formatDate(it) } ?: "Không rõ"}"
        tvStartTime.text = "Bắt đầu: ${event.start_time?.let { formatDate(it) } ?: "Không rõ"}"
        tvEndTime.text = "Kết thúc: ${event.end_time?.let { formatDate(it) } ?: "Không rõ"}"

        if (event.venue_id != null) {
            loadVenueName(event.venue_id)
        } else {
            tvVenue.text = "Địa điểm: Không rõ"
        }
    }

    private fun formatDate(timestamp: com.google.firebase.Timestamp): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}
