package com.example.eventmanagementapp.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagementapp.databinding.InviteEmailItemBinding

class InviteAdapter(
    private val context: Context,
    private val guestList: MutableList<String>,
    private val onInvite: (String) -> Unit
) : RecyclerView.Adapter<InviteAdapter.EmailViewHolder>() {

    inner class EmailViewHolder(val binding: InviteEmailItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = InviteEmailItemBinding.inflate(inflater, parent, false)
        return EmailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = guestList[position]
        val binding = holder.binding

        // Đặt nội dung EditText ban đầu
        binding.etEmail.setText(email)

        // Lắng nghe thay đổi nội dung
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                guestList[position] = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Gửi lời mời khi nhấn nút
        binding.btnInvite.setOnClickListener {
            val emailInput = binding.etEmail.text.toString().trim()
            if (emailInput.isNotEmpty()) {
                onInvite(emailInput)
            }
        }
    }

    override fun getItemCount(): Int = guestList.size
}
