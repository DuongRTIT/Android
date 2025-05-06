package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddContractActivity : AppCompatActivity() {

    private lateinit var spinnerEvent: Spinner
    private lateinit var etContractDetail: EditText
    private lateinit var btnCreateContract: Button

    private val db = FirebaseFirestore.getInstance()
    private var supplierId: Int = 0
    private var supplierName: String = ""
    private var selectedEventId: Int? = null
    private val eventMap = mutableMapOf<String, Int>() // Tên -> ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_contract)

        supplierId = intent.getIntExtra("supplierId", 0)
        supplierName = intent.getStringExtra("supplierName") ?: ""

        spinnerEvent = findViewById(R.id.spinner_event)
        etContractDetail = findViewById(R.id.et_contract_detail)
        btnCreateContract = findViewById(R.id.btn_create_contract)

        loadOngoingEvents()

        btnCreateContract.setOnClickListener {
            if (selectedEventId == null || etContractDetail.text.isNullOrEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sự kiện và nhập nội dung hợp đồng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createContract()
        }

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadOngoingEvents() {
        db.collection("Events")
            .whereIn("status", listOf("đang diễn ra", "sắp diễn ra"))
            .get()
            .addOnSuccessListener { snapshot ->
                val names = mutableListOf<String>()
                for (doc in snapshot.documents) {
                    val id = (doc.getLong("id") ?: continue).toInt()
                    val name = doc.getString("event_name") ?: ""
                    names.add(name)
                    eventMap[name] = id
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerEvent.adapter = adapter

                spinnerEvent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                        selectedEventId = eventMap[names[position]]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedEventId = null
                    }
                }
            }
    }

    private fun createContract() {
        db.collection("EventSuppliers").get().addOnSuccessListener { snapshot ->
            val newId = snapshot.size() + 1
            val data = hashMapOf(
                "id" to newId,
                "event_id" to selectedEventId,
                "supplier_id" to supplierId,
                "contract_detail" to etContractDetail.text.toString(),
                "created_at" to Timestamp.now()
            )

            val docName = "${getEventShortName()}_${supplierName}".replace(" ", "").replace("-", "_")
            db.collection("EventSuppliers").document(docName).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Đã tạo hợp đồng thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ListSupplierActivity::class.java)
                    intent.putExtra("newlyAddedSupplierId", supplierId)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getEventShortName(): String {
        return spinnerEvent.selectedItem?.toString()?.split(" ")?.firstOrNull() ?: "Event"
    }
}
