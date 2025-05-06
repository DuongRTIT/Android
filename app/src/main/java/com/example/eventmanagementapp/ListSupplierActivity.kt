package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.SupplierAdapter
import com.example.eventmanagementapp.model.Supplier
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ListSupplierActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SupplierAdapter
    private val supplierList = mutableListOf<Supplier>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_suppliers)

        recyclerView = findViewById(R.id.recycler_suppliers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SupplierAdapter(supplierList)
        recyclerView.adapter = adapter

        loadSuppliers()

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

        val fabAddSupplier = findViewById<FloatingActionButton>(R.id.fab_add_supplier)
        fabAddSupplier.setOnClickListener {
            val intent = Intent(this, AddSupplierActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadSuppliers() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Suppliers")
            .get()
            .addOnSuccessListener { result ->
                supplierList.clear()
                for (doc in result) {
                    val supplier = Supplier(
                        id = doc.getLong("id")?.toInt(),
                        supplier_name = doc.getString("supplier_name") ?: "",
                        service_type = doc.getString("service_type") ?: "",
                        contact_info = doc.getString("contact_info") ?: ""
                    )
                    supplierList.add(supplier)
                    Log.d("SUPPLIER_DEBUG", "Loaded: $supplier")

                }
                Log.d("SUPPLIER_DEBUG", "Loi")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không thể tải danh sách nhà cung cấp", Toast.LENGTH_SHORT).show()
            }
    }
}
