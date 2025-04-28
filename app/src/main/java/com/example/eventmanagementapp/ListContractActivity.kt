package com.example.eventmanagementapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.ContractAdapter
import com.example.eventmanagementapp.model.Contract
import com.google.firebase.firestore.FirebaseFirestore

class ListContractActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContractAdapter
    private val contractList = mutableListOf<Contract>()

    private var supplierId: Int? = null   // 🔥 ID nhà cung cấp truyền từ SupplierAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_contract)

        recyclerView = findViewById(R.id.recycler_contracts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContractAdapter(contractList)
        recyclerView.adapter = adapter

        // Lấy supplierId được truyền sang
        supplierId = intent.getIntExtra("supplier_id", -1)
        if (supplierId == -1) {
            Toast.makeText(this, "Không tìm thấy nhà cung cấp", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val supplierName = intent.getStringExtra("supplierName") ?: "Nhà cung cấp"
        val headerTitle = findViewById<TextView>(R.id.header_title)
        val contactUrl = intent.getStringExtra("contact_info") // <-- lấy URL
        headerTitle.text = "Chi tiết hợp đồng với  $supplierName"


        loadContracts()

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

        val btnContact = findViewById<Button>(R.id.btn_contact_supplier)
        btnContact.setOnClickListener {
            if (!contactUrl.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(contactUrl)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không có thông tin liên hệ", Toast.LENGTH_SHORT).show()
            }
        }


        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadContracts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("EventSuppliers")
            .whereEqualTo("supplier_id", supplierId)
            .get()
            .addOnSuccessListener { result ->
                contractList.clear()
                for (doc in result) {
                    try {
                        val contract = Contract(
                            id = doc.getLong("id")?.toInt(),
                            event_id = doc.getLong("event_id")?.toInt(),
                            supplier_id = doc.getLong("supplier_id")?.toInt(),
                            created_at = doc.getTimestamp("created_at"),
                            contract_detail = doc.getString("contract_detail") ?: ""
                        )
                        contractList.add(contract)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể tải danh sách hợp đồng", Toast.LENGTH_SHORT).show()
            }
    }
}
