package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvSex: TextView
    private lateinit var tvDob: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var imgAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        // Ánh xạ view
        tvUsername = findViewById(R.id.tv_username)
        tvEmail = findViewById(R.id.tv_email)
        tvSex = findViewById(R.id.tv_sex)
        tvDob = findViewById(R.id.tv_dob)
        tvCreatedAt = findViewById(R.id.tv_created_at)
        imgAvatar = findViewById(R.id.img_avatar)

        val userId = SessionManager.currentUserId
        if (userId != null) {
            loadUserInfo(userId)
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
        }

        // Nút back
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Footer navigation
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
            // Đang ở UserProfileActivity
        }

        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            SessionManager.currentUserId = null
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun loadUserInfo(userId: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val doc = docs.documents[0]
                    val username = doc.getString("username") ?: "Chưa có tên"
                    val email = doc.getString("email") ?: "Chưa có email"
                    val sex = doc.getString("sex") ?: "Không xác định"
                    val dob = doc.getTimestamp("dob")?.toDate()
                    val createdAt = doc.getTimestamp("created_at")?.toDate()
                    val imageUrl = doc.getString("imageURL")

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    tvUsername.text = "Tên người dùng: $username"
                    tvEmail.text = "Email: $email"
                    tvSex.text = "Giới tính: $sex"
                    tvDob.text = "Ngày sinh: ${dob?.let { dateFormat.format(it) } ?: "Không rõ"}"
                    tvCreatedAt.text = "Ngày tạo: ${createdAt?.let { dateFormat.format(it) } ?: "Không rõ"}"

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .into(imgAvatar)
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show()
            }
    }
}
