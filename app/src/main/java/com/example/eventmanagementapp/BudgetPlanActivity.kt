package com.example.eventmanagementapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.ExpenseAdapter
import com.example.eventmanagementapp.model.Budget
import com.example.eventmanagementapp.model.ExpenseItem
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class BudgetPlanActivity : AppCompatActivity() {

    private lateinit var tvTotalBudget: TextView
    private lateinit var tvRemainingBudget: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddExpense: Button
    private lateinit var expenseAdapter: ExpenseAdapter

    private val db = FirebaseFirestore.getInstance()
    private var eventId: Int = 0
    private var currentBudget: Budget? = null
    private var expenseList = mutableListOf<ExpenseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget_plan)

        // Ánh xạ view
        tvTotalBudget = findViewById(R.id.tv_total_budget)
        tvRemainingBudget = findViewById(R.id.tv_remaining_budget)

        recyclerView = findViewById(R.id.recyclerViewExpenses)
        btnAddExpense = findViewById(R.id.btn_add_expense)


        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        // Nhận eventId
        eventId = intent.getIntExtra("eventId", 0)
        if (eventId == 0) {
            Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupRecyclerView()
        loadBudget()


        btnAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(expenseList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = expenseAdapter
    }

    private fun loadBudget() {
        db.collection("Budget")  // Truy cập vào collection "Budget"
            .whereEqualTo("event_id", eventId)  // Tìm theo event_id
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    askForTotalBudget()
                } else {
                    val doc = documents.documents[0]

                    // Lấy thông tin từ document
                    val totalBudget = (doc.getLong("total_budget") ?: 0).toInt()
                    val eventId = (doc.getLong("event_id") ?: 0).toInt()
                    // Truy xuất khoản chi từ sub-collection "expenses"
                    db.collection("Budget")
                        .document(doc.id)  // Dùng document id để truy cập sub-collection
                        .collection("expenses")  // Truy cập sub-collection "expenses"
                        .get()
                        .addOnSuccessListener { expenseDocuments ->
                            val expenses = expenseDocuments.map {
                                ExpenseItem(
                                    expense_name = it.getString("expense_name") ?: "",
                                    price = (it.getLong("price") ?: 0L).toInt()
                                )

                            }
                            val totalSpent = expenses.sumOf { it.price }
                            val remainingBudget = totalBudget - totalSpent

                            // Hiển thị ngân sách còn lại
                            tvRemainingBudget.text = "Ngân sách còn lại: $remainingBudget VND"
                            // Tạo đối tượng Budget và lưu vào currentBudget
                            currentBudget = Budget(
                                id = doc.getLong("id")?.toInt() ?: 0,
                                event_id = eventId,
                                total_budget = totalBudget,
                                expenses = expenses
                            )

                            // Hiển thị dữ liệu ngân sách
                            showBudget()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Không tải được khoản chi", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Không tải được ngân sách", Toast.LENGTH_SHORT).show()
            }
    }



    /*private fun loadExpenses(doc: DocumentSnapshot): List<ExpenseItem> {
        val expensesList = mutableListOf<ExpenseItem>()
        val expenses = doc.get("expenses") as? List<Map<String, Any>> ?: emptyList()

        expenses.forEach {
            val expenseName = it["expense_name"] as? String ?: ""
            val price = (it["price"] as? Long ?: 0L).toInt()
            expensesList.add(ExpenseItem(expense_name = expenseName, price = price))
        }

        return expensesList
    }*/

    private fun askForTotalBudget() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nhập tổng ngân sách")

        val input = EditText(this)
        input.hint = "Nhập số tiền ngân sách"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        builder.setView(input)

        builder.setPositiveButton("Lưu") { dialog, _ ->
            val totalBudgetStr = input.text.toString().trim()
            if (totalBudgetStr.isNotEmpty()) {
                val totalBudget = totalBudgetStr.toInt()
                saveNewBudget(totalBudget)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Vui lòng nhập tổng ngân sách", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun saveNewBudget(totalBudget: Int) {
        val newId = (0..1000000).random()

        val newBudget = Budget(
            id = newId,
            event_id = eventId,
            total_budget = totalBudget,
            expenses = emptyList()
        )

        db.collection("Budget") // Sửa collection đúng tên là "Budget"
            .document(newId.toString())
            .set(newBudget)
            .addOnSuccessListener {
                currentBudget = newBudget
                showBudget()
                Toast.makeText(this, "Đã lưu ngân sách", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lưu ngân sách thất bại", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showBudget() {
        tvTotalBudget.text = "Ngân sách dự kiến: ${currentBudget?.total_budget} VND"
        expenseList.clear()
        currentBudget?.expenses?.let {
            expenseList.addAll(it)
        }
        expenseAdapter.notifyDataSetChanged()
    }

    private fun showAddExpenseDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm khoản chi")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val edtExpenseName = EditText(this)
        edtExpenseName.hint = "Tên khoản chi"
        layout.addView(edtExpenseName)

        val edtExpensePrice = EditText(this)
        edtExpensePrice.hint = "Số tiền (VND)"
        edtExpensePrice.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        layout.addView(edtExpensePrice)

        builder.setView(layout)

        builder.setPositiveButton("Thêm") { dialog, _ ->
            val name = edtExpenseName.text.toString().trim()
            val priceStr = edtExpensePrice.text.toString().trim()

            if (name.isNotEmpty() && priceStr.isNotEmpty()) {
                val price = priceStr.toInt()
                addExpense(ExpenseItem(name, price))
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private fun addExpense(expense: ExpenseItem) {
        // Truy cập vào collection "Budget" và tìm theo event_id
        db.collection("Budget")
            .whereEqualTo("event_id", eventId)  // Tìm các document có event_id trùng với eventId
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Nếu không tìm thấy document nào, yêu cầu người dùng nhập ngân sách
                    askForTotalBudget()
                } else {
                    // Lấy document đầu tiên
                    val doc = documents.documents[0]

                    // Lấy tổng ngân sách
                    val totalBudget = (doc.getLong("total_budget") ?: 0).toInt()

                    // Truy xuất khoản chi từ sub-collection "expenses"
                    db.collection("Budget")
                        .document(doc.id)  // Dùng document id để truy cập sub-collection
                        .collection("expenses")  // Truy cập vào sub-collection "expenses"
                        .get()
                        .addOnSuccessListener { expenseDocuments ->
                            // Tính tổng chi tiêu hiện tại
                            val totalSpent = expenseDocuments.sumOf { (it.getLong("price") ?: 0L).toInt() }

                            // Tính toán ngân sách còn lại
                            val remainingBudget = totalBudget - totalSpent

                            // Kiểm tra nếu ngân sách còn lại trừ đi khoản chi nhỏ hơn 0
                            if (remainingBudget - expense.price < 0) {
                                // Nếu ngân sách không đủ, hiển thị thông báo và không thêm khoản chi
                                Toast.makeText(this, "Ngân sách không đủ để thêm khoản chi", Toast.LENGTH_SHORT).show()
                            } else {
                                // Nếu ngân sách còn lại đủ, tiến hành thêm khoản chi vào sub-collection "expenses"
                                val expenseData = hashMapOf(
                                    "expense_name" to expense.expense_name,
                                    "price" to expense.price
                                )

                                db.collection("Budget")
                                    .document(doc.id)  // Dùng ID của document để truy cập
                                    .collection("expenses")  // Truy cập vào sub-collection "expenses"
                                    .document(expense.expense_name)  // Sử dụng expense_name làm ID của document
                                    .set(expenseData)  // Thêm dữ liệu vào sub-collection "expenses"
                                    .addOnSuccessListener {
                                        // Khi thêm thành công, load lại các khoản chi và hiển thị dữ liệu ngân sách
                                        loadBudget()  // Gọi lại loadBudget để cập nhật dữ liệu
                                        Toast.makeText(this, "Đã thêm khoản chi", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        // Hiển thị thông báo khi thêm thất bại
                                        Toast.makeText(this, "Thêm khoản chi thất bại", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            // Hiển thị thông báo khi không tải được các khoản chi
                            Toast.makeText(this, "Không tải được khoản chi", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                // Nếu không tìm thấy ngân sách, hiển thị thông báo lỗi
                Toast.makeText(this, "Không tìm thấy ngân sách", Toast.LENGTH_SHORT).show()
            }
    }



}
