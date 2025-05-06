package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)

        btnLogin.setOnClickListener { handleLogin() }

//        tvRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
    }

    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance().collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { docs ->
                if (docs.isEmpty) {
                    Toast.makeText(this, "Thông tin đăng nhập không chính xác", Toast.LENGTH_SHORT).show()
                } else {
                    val storedPass = docs.documents[0].getString("password")
                    if (storedPass == password) {
                        val userId = docs.documents[0].getLong("id")?.toInt()
                        SessionManager.currentUserId = userId
                        Log.d("PROFILE_DEBUG", "userId nhận được: $userId")

                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Thông tin đăng nhập không chính xác", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi kết nối đến hệ thống", Toast.LENGTH_SHORT).show()
            }
    }
}
