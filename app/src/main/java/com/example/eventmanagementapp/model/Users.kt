package com.example.eventmanagementapp.model

import com.google.firebase.Timestamp

data class Users(
        var id: Int = 0,
        var username: String = "",
        var email: String = "",
        var password: String = "",
        var other_info: String = "",
        var created_at: Timestamp? = null,
        var imageURL: String = ""
)
