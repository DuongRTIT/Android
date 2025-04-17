package com.example.eventmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.eventmanagementapp.ui.theme.EventManagementAppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import android.util.Log


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventManagementAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        // Khởi tạo Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Liệt kê các sự kiện
        listEvents()
    }
    private lateinit var db: FirebaseFirestore
    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Liệt kê các sự kiện
        listEvents()
    }
    */
    private fun listEvents() {
        // Lấy tất cả sự kiện từ Firestore
        db.collection("Events")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Nếu truy vấn thành công, duyệt qua các tài liệu sự kiện
                    val result: QuerySnapshot? = task.result
                    result?.forEach { document ->
                        // Lấy các trường dữ liệu từ tài liệu sự kiện
                        val eventName = document.getString("event_name")
                        val eventDate = document.getString("start_time") // Thời gian bắt đầu
                        val eventDescription = document.getString("description")
                        val eventPrice = document.getLong("Price")
                        val eventStartTime = document.getString("start_time")
                        val eventEndTime = document.getString("end_time")
                        val userId = document.getLong("user_id")
                        val venueId = document.getLong("venue_id")

                        // Hiển thị thông tin sự kiện trong Logcat
                        Log.d("Firestore", "Event Name: $eventName, Description: $eventDescription, " +
                                "Price: $eventPrice, Start Time: $eventStartTime, End Time: $eventEndTime, " +
                                "User ID: $userId, Venue ID: $venueId")
                    }
                } else {
                    // Nếu có lỗi trong quá trình truy vấn, hiển thị lỗi
                    Log.w("Firestore", "Error getting documents.", task.exception)
                }
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventManagementAppTheme {
        Greeting("Android")
    }
}


