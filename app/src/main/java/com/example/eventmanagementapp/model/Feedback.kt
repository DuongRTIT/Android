package com.example.eventmanagementapp.model

import com.google.firebase.Timestamp

data class Feedback(
    val id: Int? = null,
    val event_id: Int? = null,
    val guest_id: Int? = null,
    val rating: Int? = null,
    val feedback_text: String = "",
    val created_at: Timestamp? = null,
    val docId: String? = null,
    // Cho phép lưu reply cuối (null nếu chưa có)
    var replyText: String? = null
)
