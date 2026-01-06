package com.example.whatsappsender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappsender.databinding.ItemMessageBinding

class MessagesAdapter(
    private var messages: List<Message>,
    private val onDeleteClick: (Message) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.messageLabel.text = message.label
        holder.binding.messagePhone.text = if (message.isGroup) "Group Message (Manual Select)" else message.phoneNumber
        holder.binding.deleteButton.setOnClickListener { onDeleteClick(message) }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
