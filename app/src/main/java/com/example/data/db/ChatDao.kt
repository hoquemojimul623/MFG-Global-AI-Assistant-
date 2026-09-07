package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_sessions ORDER BY lastUpdated DESC")
    fun getAllSessions(): Flow<List<ChatSession>>

    @Query("SELECT * FROM chat_sessions WHERE (:userEmail = '' OR userEmail = :userEmail) ORDER BY lastUpdated DESC")
    fun getSessionsForUser(userEmail: String): Flow<List<ChatSession>>

    @Query("SELECT * FROM chat_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSession(sessionId: String): ChatSession?

    @Query("SELECT * FROM chat_sessions WHERE (:userEmail = '' OR userEmail = :userEmail) ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestSession(userEmail: String): ChatSession?

    @Query("SELECT * FROM chat_sessions ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestSessionAnyUser(): ChatSession?

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: String)

    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM chat_messages WHERE sessionId IN (SELECT sessionId FROM chat_sessions WHERE userEmail = :userEmail)")
    suspend fun deleteMessagesForUser(userEmail: String)

    @Query("DELETE FROM chat_sessions WHERE userEmail = :userEmail")
    suspend fun deleteSessionsForUser(userEmail: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()
}
