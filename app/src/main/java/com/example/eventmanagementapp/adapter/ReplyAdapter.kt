package com.example.eventmanagementapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.R
import com.example.eventmanagementapp.SessionManager
import com.example.eventmanagementapp.model.Reply
import com.google.firebase.firestore.FirebaseFirestore

class ReplyAdapter(
    private val replies: List<Reply>
) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReplyLine: TextView = itemView.findViewById(R.id.tv_reply_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reply_item, parent, false)
        return ReplyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        val context = holder.itemView.context

        if (reply.replied_by == SessionManager.currentUserId) {
            holder.tvReplyLine.text = "Bạn: ${reply.reply_text}"
        } else {
            // Query để lấy tên user khác
            val db = FirebaseFirestore.getInstance()
            db.collection("Users")
                .whereEqualTo("id", reply.replied_by)
                .get()
                .addOnSuccessListener { result ->
                    val name = if (!result.isEmpty) {
                        result.documents[0].getString("username") ?: "Ẩn danh"
                    } else {
                        "Ẩn danh"
                    }
                    holder.tvReplyLine.text = "$name: ${reply.reply_text}"
                }
                .addOnFailureListener {
                    holder.tvReplyLine.text = "Ẩn danh: ${reply.reply_text}"
                }
        }
    }

    override fun getItemCount(): Int = replies.size
}
