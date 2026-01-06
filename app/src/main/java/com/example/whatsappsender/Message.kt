package com.example.whatsappsender

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val phoneNumber: String,
    val message: String
)
