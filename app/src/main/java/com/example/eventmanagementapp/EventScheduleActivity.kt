package com.example.eventmanagementapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.adapter.ActivityAdapter
import com.example.eventmanagementapp.model.Activity
import com.example.eventmanagementapp.model.Schedule
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EventScheduleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter
    private var activityList = mutableListOf<Activity>()
    private lateinit var btnAddActivity: Button
    private var startTime: Date? = null  // Khởi tạo startTime
    private var endTime: Date? = null    // Khởi tạo endTime


    private val db = FirebaseFirestore.getInstance()
    private var eventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.schedule)

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewActivities)
        btnAddActivity= findViewById(R.id.btn_add_activity)

        // Nhận eventId từ Intent
        eventId = intent.getIntExtra("eventId", 0)
        if (eventId == 0) {
            Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Thiết lập RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        activityAdapter = ActivityAdapter(activityList)
        recyclerView.adapter = activityAdapter

        // Lấy dữ liệu lịch trình sự kiện
        loadSchedule()

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

        // Quay lại trang trước
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
        btnAddActivity.setOnClickListener {
            showAddActivityDialog()
        }

    }

    private fun loadSchedule() {

        db.collection("Schedule")  // Truy cập vào collection "Schedule"
            .whereEqualTo("event_id", eventId)  // Tìm các document có event_id trùng với eventId
            .get()
            .addOnSuccessListener { documents ->


                if (documents.isEmpty) {
                    Toast.makeText(this, "Không có lịch trình cho sự kiện này", Toast.LENGTH_SHORT).show()
                } else {
                    val doc = documents.documents[0]

                    // Truy cập sub-collection "Activities" của document này
                    db.collection("Schedule")
                        .document(doc.id)  // Dùng document ID để truy cập sub-collection
                        .collection("Activities") // Truy cập sub-collection "Activities"
                        .get()
                        .addOnSuccessListener { activityDocuments ->

                            val activities = activityDocuments.map {activityDoc ->
                                Activity(
                                    activity_name = activityDoc.getString("activity_name") ?: "",
                                    start_time =activityDoc.getTimestamp("start_time"),
                                    end_time = activityDoc.getTimestamp("end_time")
                                )
                            }

                            // Tạo đối tượng Schedule và lưu vào currentSchedule
                            val currentSchedule = Schedule(
                                event_id = (doc.getLong("event_id") ?: 0).toInt(),
                                activities = activities
                            )

                            // Hiển thị dữ liệu lịch trình
                            showSchedule(currentSchedule)
                        }
                        .addOnFailureListener {

                            Toast.makeText(this, "Không tải được hoạt động", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                   Toast.makeText(this, "Không tải được lịch trình", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showSchedule(schedule: Schedule) {
                   // Duyệt qua các hoạt động trong schedule
        activityList.clear()
        activityList.addAll(schedule.activities)
        activityAdapter.notifyDataSetChanged()
    }


    private fun showAddActivityDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm hoạt động")

        // Tạo một layout để người dùng nhập dữ liệu
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val edtActivityName = EditText(this)
        edtActivityName.hint = "Tên hoạt động"
        layout.addView(edtActivityName)

        val btnStartTime = Button(this)
        btnStartTime.text = "Chọn thời gian bắt đầu"
        layout.addView(btnStartTime)

        val btnEndTime = Button(this)
        btnEndTime.text = "Chọn thời gian kết thúc"
        layout.addView(btnEndTime)

        builder.setView(layout)

        builder.setPositiveButton("Thêm") { dialog, _ ->
            val activityName = edtActivityName.text.toString().trim()

            // Nếu tên hoạt động không rỗng và thời gian đã được chọn
            if (activityName.isNotEmpty() && startTime != null && endTime != null) {
                checkTimeAvailability(startTime!!, endTime!!, activityName)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        builder.show()

        // Chọn thời gian bắt đầu
        btnStartTime.setOnClickListener {
            showDateTimePickerDialog { date, time ->
                startTime = combineDateAndTime(date, time)
                btnStartTime.text = "Bắt đầu: ${formatDateTime(startTime)}"
            }
        }

        // Chọn thời gian kết thúc
        btnEndTime.setOnClickListener {
            showDateTimePickerDialog { date, time ->
                endTime = combineDateAndTime(date, time)
                btnEndTime.text = "Kết thúc: ${formatDateTime(endTime)}"
            }
        }
    }

    // Hàm hiển thị DatePicker và TimePicker
    private fun showDateTimePickerDialog(callback: (date: Date, time: Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                callback(calendar.time, calendar.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    // Hàm kết hợp ngày và giờ thành một đối tượng Date
    private fun combineDateAndTime(date: Date, time: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, time.hours)
        calendar.set(Calendar.MINUTE, time.minutes)
        return calendar.time
    }

    // Hàm định dạng ngày giờ thành chuỗi
    private fun formatDateTime(date: Date?): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(date ?: Date())
    }



        // Lấy thời gian diễn ra sự kiện từ Firestore
        private fun checkTimeAvailability(start: Date, end: Date, activityName: String) {
            // Kiểm tra thời gian bắt đầu phải nhỏ hơn thời gian kết thúc
            if (start >= end) {
                Toast.makeText(this, "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc", Toast.LENGTH_SHORT).show()
                return
            }

            // Chuyển thời gian sự kiện và hoạt động vào UTC+7
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"))
            calendar.time = start
            val startTimeUTCPlus7 = calendar.time

            calendar.time = end
            val endTimeUTCPlus7 = calendar.time

            // Truy cập vào collection "Schedule" để lấy event_id
            db.collection("Schedule")
                .whereEqualTo("event_id", eventId)  // Lấy document dựa trên event_id
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val scheduleDoc = documents.documents[0]
                    val eventIdFromSchedule = scheduleDoc.getLong("event_id")?.toInt() ?: return@addOnSuccessListener

                    // Sử dụng event_id lấy từ Schedule để truy vấn vào collection "Events"
                    db.collection("Events")
                        .whereEqualTo("id", eventIdFromSchedule)  // Tìm sự kiện trong collection "Events" theo event_id
                        .get()
                        .addOnSuccessListener { eventDocuments ->
                            if (eventDocuments.isEmpty) {
                                Toast.makeText(this, "Không tìm thấy sự kiện trong collection Events", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            val eventDoc = eventDocuments.documents[0]
                            val eventStartTime = eventDoc.getTimestamp("start_time")?.toDate() ?: Date()
                            val eventEndTime = eventDoc.getTimestamp("end_time")?.toDate() ?: Date()

                            // Chuyển thời gian sự kiện sang UTC+7
                            val eventStartCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"))
                            eventStartCalendar.time = eventStartTime
                            val eventStartTimeUTCPlus7 = eventStartCalendar.time

                            val eventEndCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"))
                            eventEndCalendar.time = eventEndTime
                            val eventEndTimeUTCPlus7 = eventEndCalendar.time

                            // Kiểm tra thời gian hoạt động phải nằm trong thời gian của sự kiện
                            if (startTimeUTCPlus7.before(eventStartTimeUTCPlus7) || endTimeUTCPlus7.after(eventEndTimeUTCPlus7)) {
                                Toast.makeText(this, "Thời gian hoạt động phải nằm trong khoảng thời gian sự kiện", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            // Kiểm tra thời gian hoạt động không trùng với các hoạt động khác
                            var isValid = true
                            db.collection("Schedule")
                                .document(scheduleDoc.id)
                                .collection("Activities")
                                .orderBy("start_time")
                                .get()
                                .addOnSuccessListener { activityDocuments ->
                                    for (activityDoc in activityDocuments) {
                                        val activityStart = activityDoc.getTimestamp("start_time")?.toDate() ?: Date()
                                        val activityEnd = activityDoc.getTimestamp("end_time")?.toDate() ?: Date()

                                        // Chuyển thời gian hoạt động sang UTC+7
                                        val activityStartCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"))
                                        activityStartCalendar.time = activityStart
                                        val activityStartUTCPlus7 = activityStartCalendar.time

                                        val activityEndCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"))
                                        activityEndCalendar.time = activityEnd
                                        val activityEndUTCPlus7 = activityEndCalendar.time

                                        /*if( (startTimeUTCPlus7.before(activityStartUTCPlus7) && endTimeUTCPlus7.before(activityStartUTCPlus7)) ||
                                            (startTimeUTCPlus7.after(activityStartUTCPlus7) && endTimeUTCPlus7.after(activityEndUTCPlus7) )){
                                            isValid = true
                                            //break
                                        } */

                                        // Kiểm tra xem thời gian hoạt động mới có nằm trong thời gian của hoạt động hiện tại không
                                        if ((startTimeUTCPlus7.before(activityEndUTCPlus7) && startTimeUTCPlus7.after(activityStartUTCPlus7)) ||
                                            (endTimeUTCPlus7.after(activityStartUTCPlus7) && endTimeUTCPlus7.before(activityEndUTCPlus7)) ||
                                            (startTimeUTCPlus7.before(activityStartUTCPlus7) && endTimeUTCPlus7.after(activityEndUTCPlus7)) ||
                                            (startTimeUTCPlus7.after(activityStartUTCPlus7) && endTimeUTCPlus7.before(activityEndUTCPlus7))){
                                            isValid = false
                                            break
                                        }



                                    }

                                    if (isValid) {
                                        addActivity(activityName, startTimeUTCPlus7, endTimeUTCPlus7)
                                    } else {
                                        Toast.makeText(this, "Thời gian trùng lặp với một hoạt động khác", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Không tải được hoạt động", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Không tải được sự kiện", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Không tải được lịch trình", Toast.LENGTH_SHORT).show()
                }
        }






    // Thêm hoạt động vào Firestore
    private fun addActivity(activityName: String, startTime: Date, endTime: Date) {
        val activityData = hashMapOf(
            "activity_name" to activityName,
            "start_time" to startTime,
            "end_time" to endTime,
            "event_id" to eventId
        )

        db.collection("Schedule")
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    db.collection("Schedule")
                        .document(doc.id)
                        .collection("Activities")
                        .document(activityName)  // Tên hoạt động làm ID cho document
                        .set(activityData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Đã thêm hoạt động", Toast.LENGTH_SHORT).show()
                            loadSchedule()  // Tải lại dữ liệu sau khi thêm thành công
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Thêm hoạt động thất bại", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    // Hàm chuyển đổi thời gian từ chuỗi sang đối tượng Date
    private fun parseDate(dateString: String): Date? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }


}
