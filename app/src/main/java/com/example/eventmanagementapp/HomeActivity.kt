package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var iconHome: ImageView
    private lateinit var iconEvent: ImageView
    private lateinit var iconSupplier: ImageView
    private lateinit var iconUser: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view từ layout
        iconHome = findViewById(R.id.icon_home)
        iconEvent = findViewById(R.id.icon_event)
        iconSupplier = findViewById(R.id.icon_supplier)
        iconUser = findViewById(R.id.icon_user)

        // Chuyển sang danh sách sự kiện
        iconEvent.setOnClickListener {
            val intent = Intent(this, ListEventActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }

        // Ở lại HomeActivity
        iconHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }

        // (Sau này) xử lý mở VendorActivity nếu bạn có
        iconSupplier.setOnClickListener {
            // startActivity(Intent(this, VendorActivity::class.java))
            val intent = Intent(this, ListSupplierActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId)
            startActivity(intent)
        }

        // Mở hồ sơ người dùng
        iconUser.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("userId", SessionManager.currentUserId) // 👈 dùng SessionManager
            startActivity(intent)
        }
    }
}
