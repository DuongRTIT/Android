package com.example.eventmanagementapp.model

import com.google.firebase.Timestamp

// Class Activity trong Schedule
data class Activity(
    val activity_name: String = "",
    val start_time: Timestamp? = null,
    val end_time: Timestamp? = null
)

// Class Schedule chứa event_id và danh sách activities
data class Schedule(
    val event_id: Int = 0,
    val activities: List<Activity> = emptyList()
)


