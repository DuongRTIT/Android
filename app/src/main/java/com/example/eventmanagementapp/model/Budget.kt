package com.example.eventmanagementapp.model

data class Budget(
    val id: Int = 0,
    val event_id: Int =0,
    val total_budget: Int = 0,
    val expenses: List<ExpenseItem> = emptyList()
)

data class ExpenseItem(
    val expense_name: String = "",
    val price: Int = 0
)

