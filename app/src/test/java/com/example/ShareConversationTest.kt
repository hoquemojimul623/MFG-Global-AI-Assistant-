package com.example

import com.example.data.db.ChatMessage
import com.example.ui.components.formatConversationForExport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareConversationTest {

    @Test
    fun testEmptyConversationExport() {
        val result = formatConversationForExport(emptyList())
        assertEquals("", result)
    }

    @Test
    fun testConversationExportWithMessages() {
        val messages = listOf(
            ChatMessage(
                id = 1,
                sessionId = "session_1",
                role = "user",
                content = "What is the capital of Assam?",
                timestamp = 1700000000000L
            ),
            ChatMessage(
                id = 2,
                sessionId = "session_1",
                role = "model",
                content = "The capital of Assam is Dispur.",
                timestamp = 1700000005000L
            )
        )

        val result = formatConversationForExport(messages, includeTimestamps = true)
        assertTrue(result.contains("MFG Global AI - বাৰ্তালাপ সংৰক্ষণ"))
        assertTrue(result.contains("মুঠ বাৰ্তা: 2"))
        assertTrue(result.contains("👤 You"))
        assertTrue(result.contains("What is the capital of Assam?"))
        assertTrue(result.contains("✨ MFG Global AI"))
        assertTrue(result.contains("The capital of Assam is Dispur."))
        assertTrue(result.contains("MFG MOJIMUL HOQUE"))
    }

    @Test
    fun testConversationExportWithAttachment() {
        val messages = listOf(
            ChatMessage(
                id = 1,
                sessionId = "session_1",
                role = "user",
                content = "Describe this photo",
                attachmentName = "landscape.jpg",
                attachmentType = "image",
                timestamp = 1700000000000L
            )
        )

        val result = formatConversationForExport(messages, includeTimestamps = false)
        assertTrue(result.contains("landscape.jpg"))
        assertTrue(result.contains("সংলগ্ন ফাইল"))
        assertTrue(result.contains("Describe this photo"))
    }
}
