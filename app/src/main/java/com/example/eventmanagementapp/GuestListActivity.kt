package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventmanagementapp.adapter.GuestAdapter
import com.example.eventmanagementapp.databinding.GuestListBinding
import com.example.eventmanagementapp.model.Guest
import com.google.firebase.firestore.FirebaseFirestore

class GuestListActivity : AppCompatActivity() {

    private lateinit var binding: GuestListBinding
    private lateinit var adapter: GuestAdapter
    private val guestList = mutableListOf<Guest>()
    private lateinit var tvGuestCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GuestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getIntExtra("event_id", -1)
        if (eventId == -1) {
            Toast.makeText(this, "Không tìm thấy mã sự kiện", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = GuestAdapter(guestList)
        binding.recyclerViewGuests.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewGuests.adapter = adapter

        tvGuestCount = findViewById(R.id.tv_guest_count)

        loadGuestsFromFirestore(eventId)

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

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadGuestsFromFirestore(eventId: Int) {
        FirebaseFirestore.getInstance().collection("Guests")
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener { result ->
                guestList.clear()
                for (doc in result) {
                    val email = doc.getString("email") ?: ""
                    guestList.add(Guest(email, eventId))
                }
                adapter.notifyDataSetChanged()
                tvGuestCount.text = "Tổng số khách mời: ${guestList.size}"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải danh sách khách", Toast.LENGTH_SHORT).show()
            }
    }
}
