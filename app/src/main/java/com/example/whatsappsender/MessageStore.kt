package com.example.whatsappsender

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MessageStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("whatsapp_sender_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val messagesKey = "saved_messages"

    fun getMessages(): List<Message> {
        val json = prefs.getString(messagesKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Message>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addMessage(message: Message) {
        val currentFailed = getMessages().toMutableList()
        currentFailed.add(message)
        saveMessages(currentFailed)
    }

    fun removeMessage(messageId: String) {
        val currentList = getMessages().toMutableList()
        currentList.removeIf { it.id == messageId }
        saveMessages(currentList)
    }
    
    fun updateMessage(message: Message) {
        val currentList = getMessages().toMutableList()
        val index = currentList.indexOfFirst { it.id == message.id }
        if (index != -1) {
            currentList[index] = message
            saveMessages(currentList)
        }
    }

    private fun saveMessages(messages: List<Message>) {
        val json = gson.toJson(messages)
        prefs.edit().putString(messagesKey, json).apply()
    }
}
