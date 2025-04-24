package com.example.eventmanagementapp.model

import com.google.firebase.Timestamp

data class Event(
        val id: Int? = null,
        val event_name: String = "",
        val description: String = "",
        val price: Long? = null,
        val imageURL: String = "",
        val start_time: Timestamp? = null,
        val end_time: Timestamp? = null,
        val created_at: Timestamp? = null,
        val status: String = "",
        val user_id: Int? = null,
        val venue_id: Int? = null
)
