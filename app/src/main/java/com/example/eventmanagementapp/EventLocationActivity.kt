package com.example.eventmanagementapp

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EventLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private var eventId: Int = 0
    private val db = FirebaseFirestore.getInstance()

    // ✅ Các TextView để hiển thị dữ liệu
    private lateinit var tvEventName: TextView
    private lateinit var tvEventDate: TextView
    private lateinit var tvEventVenue: TextView
    private lateinit var tvEventAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_location)

        // Lấy eventId từ Intent
        eventId = intent.getIntExtra("eventId", 0)

        // Gắn bản đồ
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Gán view
        tvEventName = findViewById(R.id.tv_event_name)
        tvEventDate = findViewById(R.id.tv_event_date)
        tvEventVenue = findViewById(R.id.tv_event_venue)
        tvEventAddress = findViewById(R.id.tv_event_address)

        // Nút back
        findViewById<android.widget.ImageButton>(R.id.btn_back)?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        if (eventId != 0) {
            fetchVenueAddressFromEvent(eventId)
        } else {
            Toast.makeText(this, "Thiếu eventId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchVenueAddressFromEvent(eventId: Int) {
        db.collection("Events")
            .whereEqualTo("id", eventId)
            .get()
            .addOnSuccessListener { eventDocs ->
                if (!eventDocs.isEmpty) {
                    val eventDoc = eventDocs.documents[0]
                    val venueId = eventDoc.getLong("venue_id")?.toInt()
                    val eventName = eventDoc.getString("event_name") ?: "Không rõ"
                    val createdAt = eventDoc.getTimestamp("created_at")?.toDate()

                    // Hiển thị dữ liệu event
                    tvEventName.text = eventName
                    tvEventDate.text = "Ngày tạo: ${formatDate(createdAt)}"
                    Log.d("EventLocation", "Tên sự kiện: $eventName, ngày tạo: ${formatDate(createdAt)}")

                    if (venueId != null) {
                        fetchAddressFromVenue(venueId)
                    } else {
                        Toast.makeText(this, "Không tìm thấy venue_id", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi truy vấn Events: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAddressFromVenue(venueId: Int) {
        db.collection("Venues")
            .whereEqualTo("id", venueId)
            .get()
            .addOnSuccessListener { venueDocs ->
                if (!venueDocs.isEmpty) {
                    val venueDoc = venueDocs.documents[0]
                    val address = venueDoc.getString("address")
                    val venueName = venueDoc.getString("venue_name") ?: "Địa điểm"

                    // Hiển thị dữ liệu venue
                    tvEventVenue.text = "Địa điểm: $venueName"
                    tvEventAddress.text = "Địa chỉ: $address"
                    Log.d("EventLocation", "Venue: $venueName, Address: $address")

                    if (!address.isNullOrEmpty()) {
                        geocodeAndShowLocation(address, venueName)
                    } else {
                        Toast.makeText(this, "Không có địa chỉ cho venue", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy venue", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi truy vấn Venues: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun geocodeAndShowLocation(address: String, venueName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val result = geocoder.getFromLocationName(address, 1)
            if (!result.isNullOrEmpty()) {
                val location = result[0]
                val latLng = LatLng(location.latitude, location.longitude)
                gMap.addMarker(MarkerOptions().position(latLng).title(venueName))
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } else {
                Toast.makeText(this, "Không tìm thấy vị trí trên bản đồ", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Lỗi geocoder: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(date: Date?): String {
        return if (date != null) {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
        } else {
            "Không rõ"
        }
    }
}
