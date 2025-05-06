package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddSupplierActivity : AppCompatActivity() {

    private lateinit var etSupplierName: EditText
    private lateinit var etContactInfo: EditText
    private lateinit var etServiceType: EditText
    private lateinit var btnContinue: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_supplier)

        etSupplierName = findViewById(R.id.et_supplier_name)
        etContactInfo = findViewById(R.id.et_contact_info)
        etServiceType = findViewById(R.id.et_service_type)
        btnContinue = findViewById(R.id.btn_add_supplier)

        btnContinue.setOnClickListener {
            val name = etSupplierName.text.toString().trim()
            val contact = etContactInfo.text.toString().trim()
            val service = etServiceType.text.toString().trim()

            if (name.isEmpty() || contact.isEmpty() || service.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Đếm số lượng supplier để đặt id
            db.collection("Suppliers").get().addOnSuccessListener { snapshot ->
                val newId = snapshot.size() + 1
                val data = hashMapOf(
                    "id" to newId,
                    "supplier_name" to name,
                    "contact_info" to contact,
                    "service_type" to service
                )

                // Lưu vào Firestore với tên document là supplier_name
                db.collection("Suppliers").document(name)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Đã thêm nhà cung cấp", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AddContractActivity::class.java)
                        intent.putExtra("supplierId", newId)
                        intent.putExtra("supplierName", name)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Lỗi khi thêm: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this, "Lỗi khi truy cập CSDL: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
