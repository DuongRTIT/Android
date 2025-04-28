package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.SessionManager
import com.example.eventmanagementapp.model.Feedback
import com.example.eventmanagementapp.model.Reply
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class FeedbackAdapter(
    private val feedbackList: List<Feedback>
) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val tvFeedbackText: TextView = item.findViewById(R.id.tv_feedback_text)
        val tvGuestName: TextView = item.findViewById(R.id.tv_guest_name)
        val tvDate: TextView = item.findViewById(R.id.tv_feedback_date)
        val ratingBar: RatingBar = item.findViewById(R.id.ratingBar)
        val imgAvatar: ImageView = item.findViewById(R.id.img_guest_avatar)

        val tvReplyLabel: TextView = item.findViewById(R.id.tv_reply_label)
        val etReply: EditText = item.findViewById(R.id.et_reply)
        val btnSendReply: Button = item.findViewById(R.id.btn_send_reply)
        val recyclerReplies: RecyclerView = item.findViewById(R.id.recycler_replies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.feedback_item, parent, false)
        return FeedbackViewHolder(v)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, pos: Int) {
        val fb = feedbackList[pos]

        holder.tvFeedbackText.text = fb.feedback_text
        holder.ratingBar.rating = fb.rating?.toFloat() ?: 0f
        holder.tvDate.text = fb.created_at?.let { formatDate(it) } ?: "Không rõ"

        if (fb.guest_id != null) loadGuestInfo(fb.guest_id, holder)
        else holder.tvGuestName.text = "Ẩn danh"

        holder.tvReplyLabel.visibility = View.VISIBLE
        holder.etReply.visibility = View.GONE
        holder.btnSendReply.visibility = View.GONE

        loadReplies(holder, fb.docId)

        holder.tvReplyLabel.setOnClickListener {
            holder.tvReplyLabel.visibility = View.GONE
            holder.etReply.visibility = View.VISIBLE
            holder.btnSendReply.visibility = View.VISIBLE
        }

        holder.btnSendReply.setOnClickListener {
            val replyText = holder.etReply.text.toString().trim()
            if (replyText.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fid = fb.docId ?: return@setOnClickListener

            val db = FirebaseFirestore.getInstance()
            val replyCollection = db.collection("Feedbacks").document(fid).collection("Replies")

            replyCollection.get()
                .addOnSuccessListener { snapshot ->
                    val nextNumber = (snapshot.size() + 1)  // lấy tổng số replies hiện tại + 1
                    val newDocId = "Reply$nextNumber"

                    replyCollection.document(newDocId)
                        .set(
                            mapOf(
                                "reply_text" to replyText,
                                "replied_at" to Timestamp.now(),
                                "replied_by" to SessionManager.currentUserId
                            )
                        )
                        .addOnSuccessListener {
                            holder.etReply.text.clear()
                            notifyItemChanged(pos)
                        }
                        .addOnFailureListener {
                            Toast.makeText(holder.itemView.context, "Gửi thất bại", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Không thể tạo reply mới", Toast.LENGTH_SHORT).show()
                }

        }
    }

    override fun getItemCount(): Int = feedbackList.size

    private fun formatDate(timestamp: Timestamp): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(timestamp.toDate())
    }

    private fun loadGuestInfo(guestId: Int, holder: FeedbackViewHolder) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Guests")
            .whereEqualTo("id", guestId)
            .get()
            .addOnSuccessListener { snap ->
                if (!snap.isEmpty) {
                    val guest = snap.documents[0]
                    holder.tvGuestName.text = guest.getString("name") ?: "Ẩn danh"
                    guest.getString("avatar")?.let { avatarUrl ->
                        Glide.with(holder.itemView.context)
                            .load(avatarUrl)
                            .into(holder.imgAvatar)
                    }
                }
            }
    }

    private fun loadReplies(holder: FeedbackViewHolder, feedbackDocId: String?) {
        if (feedbackDocId == null) return
        val db = FirebaseFirestore.getInstance()
        db.collection("Feedbacks")
            .document(feedbackDocId)
            .collection("Replies")
            .orderBy("replied_at", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snap ->
                val replyList = snap.documents.map { doc ->
                    Reply(
                        id = doc.id,
                        reply_text = doc.getString("reply_text") ?: "",
                        replied_at = doc.getTimestamp("replied_at"),
                        replied_by = doc.getLong("replied_by")?.toInt()
                    )
                }
                holder.recyclerReplies.layoutManager = LinearLayoutManager(holder.itemView.context)
                holder.recyclerReplies.adapter = ReplyAdapter(replyList)
            }
    }
}
