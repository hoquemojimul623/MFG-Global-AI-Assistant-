package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.db.ChatDao
import com.example.data.db.ChatMessage
import com.example.data.db.ChatSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomChatHistoryTest {

    private lateinit var db: AppDatabase
    private lateinit var chatDao: ChatDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        chatDao = db.chatDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveChatSessionAndMessages() = runBlocking {
        val sessionId = "session_123"
        val userEmail = "mojimulk2@gmail.com"

        val session = ChatSession(
            sessionId = sessionId,
            userEmail = userEmail,
            title = "What is MFG?",
            lastUpdated = 1000L,
            summary = "Mafia Gangster"
        )
        chatDao.insertSession(session)

        val userMessage = ChatMessage(
            sessionId = sessionId,
            role = "user",
            content = "What is MFG full form?",
            timestamp = 1001L
        )
        val aiMessage = ChatMessage(
            sessionId = sessionId,
            role = "model",
            content = "Mafia Gangster",
            timestamp = 1002L
        )

        chatDao.insertMessage(userMessage)
        chatDao.insertMessage(aiMessage)

        // Verify session retrieval
        val retrievedSession = chatDao.getSession(sessionId)
        assertNotNull(retrievedSession)
        assertEquals("What is MFG?", retrievedSession?.title)

        // Verify latest session retrieval
        val latestSession = chatDao.getLatestSession(userEmail)
        assertNotNull(latestSession)
        assertEquals(sessionId, latestSession?.sessionId)

        // Verify messages retrieval preserves order
        val messages = chatDao.getMessagesForSession(sessionId).first()
        assertEquals(2, messages.size)
        assertEquals("user", messages[0].role)
        assertEquals("What is MFG full form?", messages[0].content)
        assertEquals("model", messages[1].role)
        assertEquals("Mafia Gangster", messages[1].content)
    }

    @Test
    fun deleteSessionRemovesSessionAndMessages() = runBlocking {
        val sessionId = "session_to_delete"
        chatDao.insertSession(ChatSession(sessionId = sessionId, title = "To Delete"))
        chatDao.insertMessage(ChatMessage(sessionId = sessionId, role = "user", content = "Hello"))

        chatDao.deleteMessagesForSession(sessionId)
        chatDao.deleteSession(sessionId)

        val session = chatDao.getSession(sessionId)
        assertEquals(null, session)

        val messages = chatDao.getMessagesForSession(sessionId).first()
        assertEquals(0, messages.size)
    }
}
