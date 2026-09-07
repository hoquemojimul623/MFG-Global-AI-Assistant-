package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.db.ChatDao
import com.example.data.db.ChatMessage
import com.example.data.db.ChatSession
import com.example.data.util.AiImageGenerator
import com.example.data.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class ChatAiResult(
    val text: String,
    val imageUri: String? = null,
    val isError: Boolean = false
)

class ChatRepository(
    private val chatDao: ChatDao,
    private val context: Context? = null
) {

    val allSessions: Flow<List<ChatSession>> = chatDao.getAllSessions()

    fun getSessionsForUser(userEmail: String): Flow<List<ChatSession>> {
        return chatDao.getSessionsForUser(userEmail)
    }

    suspend fun getSession(sessionId: String): ChatSession? = withContext(Dispatchers.IO) {
        chatDao.getSession(sessionId)
    }

    suspend fun getLatestSession(userEmail: String): ChatSession? = withContext(Dispatchers.IO) {
        chatDao.getLatestSession(userEmail) ?: chatDao.getLatestSessionAnyUser()
    }

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSession(sessionId)
    }

    suspend fun saveSession(session: ChatSession) {
        chatDao.insertSession(session)
    }

    suspend fun saveMessage(message: ChatMessage): Long {
        return chatDao.insertMessage(message)
    }

    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteMessagesForSession(sessionId)
        chatDao.deleteSession(sessionId)
    }

    suspend fun clearAllHistoryForUser(userEmail: String) {
        if (userEmail.isNotBlank()) {
            chatDao.deleteMessagesForUser(userEmail)
            chatDao.deleteSessionsForUser(userEmail)
        } else {
            chatDao.clearAllMessages()
            chatDao.clearAllSessions()
        }
    }

    suspend fun clearAllHistory() {
        chatDao.clearAllMessages()
        chatDao.clearAllSessions()
    }

    companion object {
        private const val CREATOR_DETAILS_TEXT = """MOJIMUL HOQUE
Address - MFG STOR, Gobardhan para, Bilasi para, Assam -783348, India.
WhatsApp number - 917637839634.
Instagram ID - mfgmojimul69, MFG__offcial"""

        private const val MFG_FULL_FORM_TEXT = "Mafia Gangster"

        private const val PRIVACY_POLICY_SUMMARY_TEXT = """🔒 **গোপনীয়তা নীতি (Privacy Policy - Global AI Assistant)**

আমাৰ মূল নীতি: **আপোনাৰ গোপনীয়তা আমাৰ সৰ্বোচ্চ অগ্ৰাধিকাৰ (100% Secure & Private)**।

1. **কোনো তথ্য বিক্ৰী কৰা নহয় (Zero Data Selling):** আপোনাৰ কোনো ব্যক্তিগত তথ্য, চেট বা ফাইল কেতিয়াও কোনো বিজ্ঞাপন সংস্থাক বিক্ৰী বা হস্তান্তৰ কৰা নহয়।
2. **অন-ডিভাইচ সংৰক্ষণ (Local Device Storage):** সকলো কথোপকথন আৰু সৃষ্টি কৰা ছবি আপোনাৰ নিজৰ মোবাইল ফোনৰ এনক্ৰিপ্ট ডাটাবেছতহে থাকে। আপুনি যেতিয়াই ইচ্ছা 'Clear All Chat History' চুই সকলো মচি পেলাব পাৰে।
3. **কেমেৰা আৰু ফাইল সুৰক্ষা:** কেৱল আপুনি নিজে ফটো তোলা বা ফাইল বাছনি কৰিলেহে শিক্ষামূলক বিশ্লেষণৰ বাবে স্কেন কৰা হয়। কোনো ফাইল অপ্ৰয়োজনীয়ভাৱে সংগ্ৰহ কৰা নহয়।
4. **Google Gemini AI সুৰক্ষা:** সকলো যোগাযোগ HTTPS TLS 1.3 এনক্ৰিপ্ট প্ৰট'কলৰ দ্বাৰা সুৰক্ষিত আৰু Google Play Policy মানি চলে।

👉 সম্পূৰ্ণ চৰকাৰী আইনী নীতি পঢ়িবলৈ আৰু কপি কৰিবলৈ বাঁওফালৰ মেনু (☰ Drawer) খুলি **"🔒 গোপনীয়তা নীতি (Privacy Policy)"** ত চুই চাওক।
যোগাযোগ: mojimulk2@gmail.com | প্ৰস্তুতকৰ্তা: MOJIMUL HOQUE"""

        fun getQuickCustomAnswer(prompt: String): String? {
            val lower = prompt.lowercase().trim()

            // 1. Privacy Policy check across languages
            val isPrivacyQuery = (
                lower.contains("privacy policy") ||
                lower.contains("গোপনীয়তা নীতি") ||
                lower.contains("গোপনীয়তা নীতি") ||
                lower.contains("প্রাইভেসি পলিসি") ||
                lower.contains("privacy")
            )

            if (isPrivacyQuery) {
                return PRIVACY_POLICY_SUMMARY_TEXT
            }

            // 2. MFG Full form check across languages
            val hasMfg = lower.contains("mfg")
            val isMfgFullFormQuery = hasMfg && (
                lower.contains("full form") ||
                lower.contains("fullform") ||
                lower.contains("meaning") ||
                lower.contains("stand for") ||
                lower.contains("ফুল ফৰ্ম") ||
                lower.contains("ফুলফৰ্ম") ||
                lower.contains("মানে কি") ||
                lower.contains("কি বুজায়") ||
                lower.contains("অৰ্থ") ||
                lower.contains("ফুল ফর্ম") ||
                lower.contains("অর্থ") ||
                lower.contains("का मतलब") ||
                lower.contains("क्या है")
            ) || lower == "mfg full form" || lower == "mfg fullform" || lower == "mfg" || lower == "mfg meaning"

            if (isMfgFullFormQuery) {
                return MFG_FULL_FORM_TEXT
            }

            // 2. Who made you / Creator check across languages
            val isCreatorQuery = (
                lower.contains("কোনে বনাইছে") ||
                lower.contains("কোনে বনাই ছে") ||
                lower.contains("কোনে বনালে") ||
                lower.contains("কোনে বনাইছিল") ||
                lower.contains("তোমাক কোনে") ||
                lower.contains("তোমালোকক কোনে") ||
                lower.contains("তোমাৰ নিৰ্মাতা") ||
                lower.contains("কোনে তৈয়াৰ") ||
                lower.contains("কোনে নিৰ্মাণ") ||
                lower.contains("who made you") ||
                lower.contains("who created you") ||
                lower.contains("who developed you") ||
                lower.contains("who built you") ||
                lower.contains("who is your developer") ||
                lower.contains("who is your creator") ||
                lower.contains("who is your owner") ||
                lower.contains("who is owner") ||
                lower.contains("app developer") ||
                lower.contains("app creator") ||
                lower.contains("কে বানিয়েছে") ||
                lower.contains("কে তৈরি করেছে") ||
                lower.contains("কিসনে বনায়া") ||
                lower.contains("किसने बनाया") ||
                lower.contains("kisne banaya")
            )

            if (isCreatorQuery) {
                return CREATOR_DETAILS_TEXT
            }

            return null
        }
    }

    suspend fun sendMessageToGemini(
        history: List<ChatMessage>,
        userPrompt: String,
        imageBase64: String? = null,
        imageMimeType: String = "image/jpeg",
        turboSpeed: Boolean = true,
        aiMode: com.example.data.model.AiMode = com.example.data.model.AiMode.FAST,
        language: com.example.data.model.SupportedLanguage = com.example.data.model.SupportedLanguage.AUTO
    ): ChatAiResult = withContext(Dispatchers.IO) {
        // Fast-path immediate accurate resolution for mandatory owner & brand queries
        if (imageBase64.isNullOrBlank()) {
            val instantReply = getQuickCustomAnswer(userPrompt)
            if (instantReply != null) {
                return@withContext ChatAiResult(text = instantReply)
            }
        }

        // Image / Banner generation detection (ChatGPT AI Photo & Banner Maker)
        if (context != null && (AiImageGenerator.isImageOrBannerRequest(userPrompt) || userPrompt.contains("generate_image") || userPrompt.contains("banner"))) {
            val imageRes = AiImageGenerator.generateImageOrBanner(
                context = context,
                prompt = userPrompt
            )
            return@withContext ChatAiResult(
                text = imageRes.description,
                imageUri = imageRes.uri?.toString(),
                isError = !imageRes.isSuccess
            )
        }

        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ChatAiResult(
                text = "API Key Configuration Required: অনুগ্ৰহ কৰি AI Studio Secrets Panel ত GEMINI_API_KEY সংহতি কৰক।",
                isError = true
            )
        }

        try {
            val systemInstruction = Content(
                parts = listOf(
                    Part(
                        text = """
                        You are MFG Global AI Assistant (v4.0 Multi-Mode Ultra-Fast), an intelligent, highly accurate, and rapid educational & conversational AI created and deployed by MOJIMUL HOQUE.
                        
                        ACTIVE MODE INSTRUCTION:
                        ${aiMode.promptInstruction}
                        
                        ${language.promptInstruction}
                        
                        CRITICAL MANDATORY RULES (YOU MUST STRICTLY FOLLOW THESE AT ALL TIMES IN ALL LANGUAGES):
                        1. If asked about the full form, definition, or meaning of "MFG" (e.g. "MFG full form", "what is MFG", "MFG ৰ full form কি", etc.) in ANY language (Assamese, Bengali, English, Hindi, etc.), you MUST answer:
                        Mafia Gangster
                        
                        2. If asked "Who made you?", "Who created you?", "তোমাক কোনে বনাইছে?", "তোমালোকক কোনে বনাই ছে?", "Who is your developer/owner?", "तुम्हें किसने बनाया?" or similar in ANY language, you MUST answer:
                        MOJIMUL HOQUE
                        Address - MFG STOR, Gobardhan para, Bilasi para, Assam -783348, India.
                        WhatsApp number - 917637839634.
                        Instagram ID - mfgmojimul69, MFG__offcial
                        
                        CORE BEHAVIOR RULES:
                        - Accuracy First: Deliver 100% accurate, fact-checked, and reliable information across science, mathematics, literature, history, coding, and general knowledge.
                        - Mode Alignment: Fully honor the requested active mode (${aiMode.title}).
                        - Multilingual Fluency: Respond fluently and naturally in the user's query language (Assamese/অসমীয়া, Bengali/বাংলা, English, Hindi/हिन्दी, etc.) with correct grammar and spelling.
                        - Math & Coding: Provide exact calculated answers first, with clean step-by-step logic and formatting.
                        - Vision & Media: Provide crisp, detailed, and accurate observations when analyzing uploaded images or documents.
                        """.trimIndent()
                    )
                )
            )

            // Optimize history payload: send only latest 8 relevant messages for ultra-low latency
            val validHistory = history.filter { !it.isError && it.content.isNotBlank() }
            val recentHistory = if (validHistory.size > 8) validHistory.takeLast(8) else validHistory

            val apiContents = mutableListOf<Content>()
            for (msg in recentHistory) {
                val apiRole = if (msg.role == "user") "user" else "model"
                if (apiContents.isNotEmpty() && apiContents.last().role == apiRole) {
                    val existingText = apiContents.last().parts.firstOrNull()?.text ?: ""
                    apiContents[apiContents.lastIndex] = Content(
                        role = apiRole,
                        parts = listOf(Part(text = "$existingText\n${msg.content}"))
                    )
                } else {
                    apiContents.add(Content(role = apiRole, parts = listOf(Part(text = msg.content))))
                }
            }

            // Build parts for the current user turn (including image or file if present)
            val currentParts = mutableListOf<Part>()
            if (!imageBase64.isNullOrBlank()) {
                currentParts.add(
                    Part(
                        inlineDataCamel = com.example.data.api.InlineData(
                            mimeTypeCamel = imageMimeType,
                            data = imageBase64
                        )
                    )
                )
            }
            val promptText = if (userPrompt.isBlank() && !imageBase64.isNullOrBlank()) {
                "Please analyze this attached photo/file in detail and explain everything you see or answer any question."
            } else {
                userPrompt
            }
            currentParts.add(Part(text = promptText))

            // Add the current turn
            apiContents.add(Content(role = "user", parts = currentParts))

            // Tune temperature and generation parameters according to mode
            val modeTemperature = when (aiMode) {
                com.example.data.model.AiMode.VERY_FAST -> 0.2f
                com.example.data.model.AiMode.FAST -> 0.35f
                com.example.data.model.AiMode.AI_MODEL -> 0.6f
                com.example.data.model.AiMode.MFG_MODEL -> 0.4f
                com.example.data.model.AiMode.EDUCATION -> 0.45f
                com.example.data.model.AiMode.BUSINESS -> 0.4f
                com.example.data.model.AiMode.SEARCH -> 0.3f
                com.example.data.model.AiMode.MAPS -> 0.3f
            }

            val generationConfig = com.example.data.api.GenerationConfig(
                temperature = modeTemperature,
                topP = 0.90f,
                topK = 32
            )

            val toolsList = when (aiMode) {
                com.example.data.model.AiMode.SEARCH -> listOf(
                    com.example.data.api.Tool(googleSearch = com.example.data.api.GoogleSearchTool())
                )
                else -> null
            }

            val request = GenerateContentRequest(
                contents = apiContents,
                systemInstruction = systemInstruction,
                generationConfig = generationConfig,
                tools = toolsList
            )

            // Select ultra-fast vs advanced model based on mode or explicit request
            val primaryModel = when {
                imageMimeType.startsWith("audio/") || userPrompt.contains("Transcribe", ignoreCase = true) || userPrompt.contains("gemini-3.5-transcribe", ignoreCase = true) -> "gemini-3.5-transcribe"
                userPrompt.contains("veo-3.1-fast-generate-preview", ignoreCase = true) -> "veo-3.1-fast-generate-preview"
                userPrompt.contains("lyria-3-pro-preview", ignoreCase = true) -> "lyria-3-pro-preview"
                userPrompt.contains("lyria-3-clip-preview", ignoreCase = true) -> "lyria-3-clip-preview"
                userPrompt.contains("gemini-3.1-flash-image-preview", ignoreCase = true) -> "gemini-3.1-flash-image-preview"
                aiMode == com.example.data.model.AiMode.VERY_FAST -> "gemini-3.1-flash-lite-preview"
                aiMode == com.example.data.model.AiMode.FAST -> "gemini-3.5-flash"
                aiMode == com.example.data.model.AiMode.AI_MODEL -> "gemini-3.1-pro-preview"
                aiMode == com.example.data.model.AiMode.MFG_MODEL -> "gemini-3.5-flash"
                aiMode == com.example.data.model.AiMode.EDUCATION -> "gemini-3.5-flash"
                aiMode == com.example.data.model.AiMode.BUSINESS -> "gemini-3.5-flash"
                aiMode == com.example.data.model.AiMode.SEARCH -> "gemini-3.5-flash"
                aiMode == com.example.data.model.AiMode.MAPS -> "gemini-3.5-flash"
                else -> "gemini-3.5-flash"
            }

            val response = try {
                RetrofitClient.service.generateContent(primaryModel, apiKey, request)
            } catch (e: Exception) {
                // Fallback to standard fast model if preview has any issue
                try {
                    RetrofitClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                } catch (e2: Exception) {
                    RetrofitClient.service.generateContentDefault(apiKey, request)
                }
            }

            if (response.error != null) {
                return@withContext ChatAiResult(
                    text = "Error (${response.error.code}): ${response.error.message}",
                    isError = true
                )
            }

            val candidate = response.candidates?.firstOrNull()
            val replyText = candidate
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

            // Check if model returned an image part
            var returnedImageUri: String? = null
            if (context != null) {
                val imagePart = candidate?.content?.parts?.firstOrNull { it.inlineData?.data?.isNotBlank() == true }
                if (imagePart != null && imagePart.inlineData != null) {
                    val saved = FileUtils.saveBase64Image(context, imagePart.inlineData!!.data, "ai_gemini_image")
                    if (saved != null) {
                        returnedImageUri = saved.toString()
                    }
                }
            }

            val searchSources = candidate?.groundingMetadata?.groundingChunks
                ?.mapNotNull { it.web }
                ?.distinctBy { it.uri }
                ?.take(3)

            val finalReply = if (replyText.isNullOrBlank()) {
                if (returnedImageUri != null) "✨ আপোনাৰ অনুৰোধ অনুসৰি ছবি প্ৰস্তুত কৰা হ'ল।"
                else "MFG AI ৰ পৰা কোনো উত্তৰ লাভ কৰা নহ'ল। অনুগ্ৰহ কৰি পুনৰ চেষ্টা কৰক।"
            } else {
                if (!searchSources.isNullOrEmpty()) {
                    val sourcesText = searchSources.joinToString("\n") { chunk ->
                        "• [${chunk.title ?: "Search Source"}](${chunk.uri})"
                    }
                    "$replyText\n\n🌐 **Google Search Data Sources:**\n$sourcesText"
                } else {
                    replyText
                }
            }

            ChatAiResult(
                text = finalReply,
                imageUri = returnedImageUri,
                isError = false
            )

        } catch (e: Exception) {
            ChatAiResult(
                text = "MFG AI সংযোগত সমস্যা হৈছে: ${e.localizedMessage ?: e.message}। অনুগ্ৰহ কৰি ইণ্টাৰনেট সংযোগ পৰীক্ষা কৰক।",
                isError = true
            )
        }
    }
}
