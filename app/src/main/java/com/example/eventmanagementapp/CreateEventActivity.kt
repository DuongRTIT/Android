package com.example.eventmanagementapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var spinnerLocation: Spinner
    private lateinit var etPrice: EditText
    private lateinit var etImageUrl: EditText
    private lateinit var etStartDateTime: EditText
    private lateinit var etEndDateTime: EditText
    private lateinit var btnContinue: Button

    private var startTimestamp: Timestamp? = null
    private var endTimestamp: Timestamp? = null
    private val venueNameList = mutableListOf<String>()
    private var selectedVenueName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event)

        etName = findViewById(R.id.et_event_name)
        etDesc = findViewById(R.id.et_description)
        spinnerLocation = findViewById(R.id.spinner_location)
        etPrice = findViewById(R.id.et_price)
        etImageUrl = findViewById(R.id.et_image_url)
        etStartDateTime = findViewById(R.id.et_start_datetime)
        etEndDateTime = findViewById(R.id.et_end_datetime)
        btnContinue = findViewById(R.id.btn_continue)

        loadVenueNames()

        etStartDateTime.setOnClickListener {
            pickDateTime { ts ->
                startTimestamp = ts
                etStartDateTime.setText(formatTimestamp(ts))
            }
        }

        etEndDateTime.setOnClickListener {
            pickDateTime { ts ->
                endTimestamp = ts
                etEndDateTime.setText(formatTimestamp(ts))
            }
        }

        btnContinue.setOnClickListener {
            val venueName = selectedVenueName
            if (venueName.isNullOrEmpty()) {
                Toast.makeText(this, "Vui lòng chọn địa điểm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseFirestore.getInstance().collection("Venues")
                .whereEqualTo("venue_name", venueName)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        val venueId = snapshot.documents[0].getLong("id")?.toInt() ?: 0
                        getNextEventId { newId ->
                            saveEvent(newId, venueId)
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy địa điểm phù hợp", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lỗi khi tìm địa điểm", Toast.LENGTH_SHORT).show()
                }
        }

        // Navigation buttons
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

    private fun loadVenueNames() {
        FirebaseFirestore.getInstance().collection("Venues")
            .orderBy("venue_name")
            .get()
            .addOnSuccessListener { snapshot ->
                venueNameList.clear()
                for (doc in snapshot) {
                    doc.getString("venue_name")?.let { venueNameList.add(it) }
                }
                venueNameList.add(0, "Địa chỉ") // Dòng này thêm mục hiển thị đầu tiên là "Địa chỉ"
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, venueNameList)
                spinnerLocation.adapter = adapter
                spinnerLocation.setSelection(0)
                spinnerLocation.adapter = adapter
                spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, pos: Int, id: Long) {
                        selectedVenueName = venueNameList[pos]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedVenueName = null
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi tải danh sách địa điểm", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getNextEventId(onResult: (Int) -> Unit) {
        FirebaseFirestore.getInstance().collection("Events")
            .orderBy("id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { docs ->
                val lastId = if (!docs.isEmpty) docs.documents[0].getLong("id") ?: 0 else 0
                onResult(lastId.toInt() + 1)
            }
            .addOnFailureListener {
                onResult(1)
            }
    }

    private fun saveEvent(newId: Int, venueId: Int) {
        val name = etName.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val price = etPrice.text.toString().toLongOrNull() ?: 0
        val imageUrl = etImageUrl.text.toString().trim()

        if (name.isEmpty() || desc.isEmpty() || startTimestamp == null || endTimestamp == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val eventData = hashMapOf(
            "id" to newId,
            "event_name" to name,
            "description" to desc,
            "price" to price,
            "imageURL" to imageUrl,
            "start_time" to startTimestamp,
            "end_time" to endTimestamp,
            "status" to "sắp diễn ra",
            "user_id" to SessionManager.currentUserId,
            "venue_id" to venueId,
            "created_at" to Timestamp.now()
        )

        FirebaseFirestore.getInstance().collection("Events")
            .document(name)
            .set(eventData)
            .addOnSuccessListener {
                Toast.makeText(this, "Tạo sự kiện thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, InviteGuestActivity::class.java)
                intent.putExtra("event_id", newId)
                intent.putExtra("event_name", name)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi lưu sự kiện", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pickDateTime(callback: (Timestamp) -> Unit) {
        val now = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            TimePickerDialog(this, { _, h, min ->
                val cal = Calendar.getInstance()
                cal.set(y, m, d, h, min)
                callback(Timestamp(cal.time))
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun formatTimestamp(ts: Timestamp): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(ts.toDate())
    }
}
