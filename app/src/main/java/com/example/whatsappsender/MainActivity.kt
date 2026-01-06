package com.example.whatsappsender

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappsender.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var messageStore: MessageStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageStore = MessageStore(this)

        binding.manageButton.setOnClickListener {
            startActivity(Intent(this, ManageMessagesActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshButtons()
    }

    private fun refreshButtons() {
        binding.messagesGrid.removeAllViews()
        val messages = messageStore.getMessages()

        if (messages.isEmpty()) {
            binding.configSummary.text = "No messages defined. content\nClick 'Manage Messages' to start."
            return
        } else {
            binding.configSummary.text = "Select a message to send"
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val buttonSize = (screenWidth / 2) - dpToPx(24) // Adjust for margins/padding

        messages.forEach { message ->
            val button = Button(this).apply {
                text = if (message.isGroup) "${message.label}\n(Group)" else "${message.label}\n(${message.phoneNumber})"
                gravity = Gravity.CENTER
                textSize = 16f
                isAllCaps = false
                
                layoutParams = GridLayout.LayoutParams().apply {
                    width = buttonSize
                    height = buttonSize
                    setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
                }
                
                setOnClickListener {
                    sendWhatsappMessage(message)
                }
            }
            binding.messagesGrid.addView(button)
        }
    }

    private fun sendWhatsappMessage(message: Message) {
        if (message.message.isBlank()) {
            Toast.makeText(this, R.string.missing_config_data, Toast.LENGTH_SHORT).show()
            return
        }

        val intent = if (message.isGroup) {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message.message)
                setPackage("com.whatsapp")
            }
        } else {
            if (message.phoneNumber.isBlank()) {
                Toast.makeText(this, R.string.missing_config_data, Toast.LENGTH_SHORT).show()
                return
            }
            val uri = Uri.parse("https://api.whatsapp.com/send").buildUpon()
                .appendQueryParameter("phone", message.phoneNumber)
                .appendQueryParameter("text", message.message)
                .build()
            Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.whatsapp")
            }
        }

        try {
            startActivity(intent)
        } catch (error: ActivityNotFoundException) {
            Toast.makeText(this, R.string.whatsapp_missing, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
