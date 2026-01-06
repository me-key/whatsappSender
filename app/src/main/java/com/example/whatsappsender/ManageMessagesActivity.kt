package com.example.whatsappsender

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappsender.databinding.ActivityManageMessagesBinding

class ManageMessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageMessagesBinding
    private lateinit var messageStore: MessageStore
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageStore = MessageStore(this)
        
        adapter = MessagesAdapter(messageStore.getMessages()) { message ->
            messageStore.removeMessage(message.id)
            refreshList()
        }

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.messagesRecyclerView.adapter = adapter

        binding.addMessageFab.setOnClickListener {
            showAddMessageDialog()
        }
    }

    private fun refreshList() {
        adapter.updateMessages(messageStore.getMessages())
    }

    private fun showAddMessageDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_message, null)
        val labelInput = dialogView.findViewById<EditText>(R.id.labelInput)
        val phoneInput = dialogView.findViewById<EditText>(R.id.phoneInput)
        val messageInput = dialogView.findViewById<EditText>(R.id.messageInput)
        val isGroupCheckbox = dialogView.findViewById<android.widget.CheckBox>(R.id.isGroupCheckbox)

        isGroupCheckbox.setOnCheckedChangeListener { _, isChecked ->
            phoneInput.isEnabled = !isChecked
            if (isChecked) {
                phoneInput.text.clear()
                phoneInput.hint = "N/A for Group"
            } else {
                phoneInput.hint = "Phone Number (e.g. +1...)"
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Add Message")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val label = labelInput.text.toString()
                val phone = phoneInput.text.toString()
                val message = messageInput.text.toString()
                val isGroup = isGroupCheckbox.isChecked

                if (label.isNotBlank() && message.isNotBlank() && (isGroup || phone.isNotBlank())) {
                    val newMessage = Message(
                        label = label,
                        phoneNumber = if (isGroup) "" else phone,
                        message = message,
                        isGroup = isGroup
                    )
                    messageStore.addMessage(newMessage)
                    refreshList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
