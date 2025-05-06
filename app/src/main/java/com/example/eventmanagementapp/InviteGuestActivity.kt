package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventmanagementapp.adapter.InviteAdapter
import com.example.eventmanagementapp.databinding.InviteGuestBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class InviteGuestActivity : AppCompatActivity() {

    private lateinit var binding: InviteGuestBinding
    private lateinit var inviteAdapter: InviteAdapter
    private val guestList = mutableListOf("", "", "", "") // mặc định 4 dòng

    private var eventId: Int = -1
    private var eventName: String? = null

    // Gmail dùng để gửi (đã tạo App Password)
    private val senderEmail = "anhmjolnir@gmail.com"
    private val senderPassword = "wnxrifrbasxmypcn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InviteGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getIntExtra("event_id", -1)
        eventName = intent.getStringExtra("event_name")
        binding.headerTitle.text = "Mời khách - $eventName"

        inviteAdapter = InviteAdapter(this, guestList) { invitedEmail ->
            handleInvite(invitedEmail)
        }

        binding.recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewExpenses.adapter = inviteAdapter

        binding.btnBack.setOnClickListener { finish() }
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

    private fun handleInvite(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendEmailWithGmail(email, eventName ?: "Sự kiện")

                val firestore = FirebaseFirestore.getInstance()

                val invitationData = hashMapOf(
                    "event_id" to eventId,
                    "email" to email,
                    "invited_at" to Timestamp.now()
                )

                // Ghi vào Invitations
                firestore.collection("Invitations")
                    .document(email)
                    .set(invitationData)
                    .addOnSuccessListener {
                        // Ghi tiếp vào Guests
                        val guestData = mapOf(
                            "email" to email,
                            "event_id" to eventId
                        )

                        firestore.collection("Guests")
                            .document(email)
                            .set(guestData)
                            .addOnSuccessListener {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@InviteGuestActivity,
                                        "✅ Đã gửi và lưu khách mời: $email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@InviteGuestActivity,
                                        "Gửi OK nhưng lỗi lưu Guests",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                    .addOnFailureListener {
                        runOnUiThread {
                            Toast.makeText(
                                this@InviteGuestActivity,
                                "❌ Lỗi lưu lời mời Firestore",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@InviteGuestActivity,
                        "❌ Lỗi gửi email: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun sendEmailWithGmail(recipientEmail: String, eventName: String) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(senderEmail))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
            subject = "Lời mời tham gia sự kiện"
            setText("Xin chào,\n\nBạn đã được mời tham gia sự kiện: $eventName.\n\nTrân trọng,\nEvent App")
        }

        Transport.send(message)
    }
}
