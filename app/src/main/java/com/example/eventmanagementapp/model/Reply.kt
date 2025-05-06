package com.example.eventmanagementapp.model

import com.google.firebase.Timestamp

data class Reply(
    val id: String = "",
    val reply_text: String = "",
    val replied_at: Timestamp? = null,
    val replied_by: Int? = null
)
