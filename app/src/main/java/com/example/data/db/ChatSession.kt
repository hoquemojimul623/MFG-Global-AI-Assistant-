package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey
    val sessionId: String,
    val userEmail: String = "",
    val title: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val summary: String = ""
)
