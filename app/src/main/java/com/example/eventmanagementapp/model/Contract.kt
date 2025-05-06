package com.example.eventmanagementapp.model

import com.google.firebase.Timestamp

data class Contract(
    val id: Int? = null,
    val event_id: Int? = null,            // 🔥 ID của sự kiện liên kết
    val supplier_id: Int? = null,          // 🔥 ID của nhà cung cấp liên kết
    val created_at: Timestamp? = null,     // 🔥 Thời gian tạo hợp đồng
    val contract_detail: String = ""       // 🔥 Nội dung chi tiết hợp đồng
)
