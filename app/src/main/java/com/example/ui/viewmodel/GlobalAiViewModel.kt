package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.db.AppDatabase
import com.example.data.db.ChatMessage
import com.example.data.db.ChatSession
import com.example.data.model.AiMode
import com.example.data.model.SupportedLanguage
import com.example.data.repository.ChatRepository
import com.example.data.subscription.SubscriptionManager
import com.example.data.subscription.SubscriptionPlan
import com.example.data.subscription.UserSubscriptionStatus
import com.example.data.util.FileUtils
import com.example.ui.tts.TtsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class AttachedMedia(
    val uri: Uri? = null,
    val type: String, // "image", "file", "video", "drawing"
    val name: String,
    val mimeType: String,
    val base64Data: String? = null,
    val extractedText: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalAiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatRepository
    val ttsManager: TtsManager = TtsManager(application)
    private val authManager = AuthManager(application)

    private val _currentSessionId = MutableStateFlow<String>("")
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String>(
        authManager.getUserProfile()?.email ?: ""
    )
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessions: StateFlow<List<ChatSession>>

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMessages: StateFlow<List<ChatMessage>>

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _attachedMedia = MutableStateFlow<AttachedMedia?>(null)
    val attachedMedia: StateFlow<AttachedMedia?> = _attachedMedia.asStateFlow()

    private val _autoSpeakEnabled = MutableStateFlow(false)
    val autoSpeakEnabled: StateFlow<Boolean> = _autoSpeakEnabled.asStateFlow()

    private val _turboSpeedMode = MutableStateFlow(true)
    val turboSpeedMode: StateFlow<Boolean> = _turboSpeedMode.asStateFlow()

    private val prefs = application.getSharedPreferences("global_ai_preferences", Context.MODE_PRIVATE)

    val subscriptionManager = SubscriptionManager(application)

    private val _subscriptionStatus = MutableStateFlow(
        subscriptionManager.getUserStatus(_currentUserEmail.value)
    )
    val subscriptionStatus: StateFlow<UserSubscriptionStatus> = _subscriptionStatus.asStateFlow()

    private val _showUpgradeDialog = MutableStateFlow(false)
    val showUpgradeDialog: StateFlow<Boolean> = _showUpgradeDialog.asStateFlow()

    private val _selectedAiMode = MutableStateFlow(
        AiMode.fromId(prefs.getString("saved_ai_mode", AiMode.FAST.id))
    )
    val selectedAiMode: StateFlow<AiMode> = _selectedAiMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(
        SupportedLanguage.fromCode(prefs.getString("saved_ai_language", SupportedLanguage.AUTO.code))
    )
    val selectedLanguage: StateFlow<SupportedLanguage> = _selectedLanguage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ChatRepository(database.chatDao(), application.applicationContext)

        sessions = _currentUserEmail.flatMapLatest { email ->
            if (email.isBlank()) {
                repository.allSessions
            } else {
                repository.getSessionsForUser(email)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        currentMessages = _currentSessionId.flatMapLatest { sessionId ->
            if (sessionId.isBlank()) {
                flowOf(emptyList())
            } else {
                repository.getMessagesForSession(sessionId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Restore active past conversation from Room database or initialize
        restoreOrInitializeSession(_currentUserEmail.value)
    }

    private fun restoreOrInitializeSession(targetEmail: String) {
        viewModelScope.launch {
            val savedSessionId = prefs.getString("last_active_session_id", null)
            if (!savedSessionId.isNullOrBlank()) {
                val existing = repository.getSession(savedSessionId)
                if (existing != null) {
                    _currentSessionId.value = existing.sessionId
                    return@launch
                }
            }

            // Fallback to most recent session in Room database
            val latest = repository.getLatestSession(targetEmail)
            if (latest != null) {
                _currentSessionId.value = latest.sessionId
                prefs.edit().putString("last_active_session_id", latest.sessionId).apply()
            } else {
                // If no sessions exist in the database, start a fresh session
                startNewChatSession()
            }
        }
    }

    fun setUserEmail(email: String) {
        if (_currentUserEmail.value != email) {
            _currentUserEmail.value = email
            _attachedMedia.value = null
            _inputText.value = ""
            _subscriptionStatus.value = subscriptionManager.getUserStatus(email)
            restoreOrInitializeSession(email)
        }
    }

    fun openUpgradeDialog() {
        _showUpgradeDialog.value = true
    }

    fun dismissUpgradeDialog() {
        _showUpgradeDialog.value = false
    }

    fun refreshSubscriptionStatus() {
        _subscriptionStatus.value = subscriptionManager.getUserStatus(_currentUserEmail.value)
    }

    /**
     * Regex-based validation function that verifies whether the provided UTR (Unique Transaction Reference)
     * number strictly follows the standard 12-digit numeric format typically used in Indian UPI transactions (NPCI standard).
     *
     * @param utr The UTR / UPI Ref string entered by the user
     * @return True if the string strictly consists of exactly 12 numeric digits (0-9), false otherwise
     */
    fun validateUtrNumber(utr: String): Boolean {
        val cleanUtr = utr.trim()
        val utrRegex = Regex("^[0-9]{12}$")
        return cleanUtr.matches(utrRegex)
    }

    /**
     * Validates and verifies the UTR number using regex-based formatting check, master activation codes,
     * and NPCI banking date/time rules before enabling VIP upgrade status.
     *
     * @param rawInput The user's input UTR or passcode
     * @param plan The selected SubscriptionPlan
     * @return Pair containing boolean indicating validity and localized status message
     */
    fun validateAndVerifyUtr(
        rawInput: String,
        plan: SubscriptionPlan
    ): Pair<Boolean, String> {
        val input = rawInput.trim()
        if (input.isBlank()) {
            return Pair(false, "অনুগ্ৰহ কৰি পেমেণ্টৰ ১২ সংখ্যাৰ শুদ্ধ UTR / Ref নম্বৰ দিয়ক")
        }

        // 1. Check if it is a valid Master Code or Admin Key
        val codeResult = subscriptionManager.validateAndActivateCode(_currentUserEmail.value, input)
        if (codeResult.first) {
            return Pair(true, "✅ VIP ছিক্ৰেট ক'ড পৰীক্ষিত হ'ল! এতিয়া তলৰ '👑 MFG VIP' বুটামত টিপক।")
        }

        // 2. Strict Regex validation: Must follow standard 12-digit UPI format
        if (!validateUtrNumber(input)) {
            return Pair(
                false,
                "⚠️ অশুদ্ধ ফৰ্মেট! UPI UTR নম্বৰ ঠিক ১২ টা সংখ্যাৰ (12-digit numeric) হ'ব লাগিব (উদাহৰণ: 423589123456)। কোনো আখৰ বা বিশেষ চিহ্ন মান্য নহ'ব।"
            )
        }

        // 3. Delegate to SubscriptionManager for full NPCI checksum & bank verification
        return subscriptionManager.verifyUtrForOwnerUpi(_currentUserEmail.value, input, plan)
    }

    /**
     * Activates VIP upgrade status for the user once the 12-digit UTR format is validated.
     *
     * @param utr The validated 12-digit UTR or transaction identifier
     * @param plan The target subscription plan
     * @return True if VIP upgrade status was enabled successfully, false otherwise
     */
    fun upgradeToVipWithUtr(utr: String, plan: SubscriptionPlan): Boolean {
        val cleanUtr = utr.trim()
        val isValidUtrFormat = validateUtrNumber(cleanUtr)
        val isCodeValid = subscriptionManager.validateAndActivateCode(_currentUserEmail.value, cleanUtr).first

        if (!isValidUtrFormat && !isCodeValid) {
            return false
        }

        val userEmail = _currentUserEmail.value
        val txnId = if (isValidUtrFormat) "UTR_$cleanUtr" else "CODE_$cleanUtr"
        subscriptionManager.activateSubscription(userEmail, plan, txnId)
        if (isValidUtrFormat) {
            subscriptionManager.markUtrUsed(cleanUtr, userEmail)
        }
        refreshSubscriptionStatus()
        return true
    }

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleAutoSpeak() {
        _autoSpeakEnabled.value = !_autoSpeakEnabled.value
    }

    fun toggleTurboSpeed() {
        _turboSpeedMode.value = !_turboSpeedMode.value
    }

    fun setAiMode(mode: AiMode) {
        _selectedAiMode.value = mode
        prefs.edit().putString("saved_ai_mode", mode.id).apply()
    }

    fun setLanguage(language: SupportedLanguage) {
        _selectedLanguage.value = language
        prefs.edit().putString("saved_ai_language", language.code).apply()
        if (language.speechLocaleTag.isNotBlank()) {
            ttsManager.setLanguage(language.speechLocaleTag)
        }
    }

    fun attachImage(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val name = FileUtils.getFileName(context, uri)
            val mimeType = FileUtils.getMimeType(context, uri)
            val base64 = withContext(Dispatchers.IO) {
                FileUtils.uriToBase64(context, uri)
            }
            _attachedMedia.value = AttachedMedia(
                uri = uri,
                type = "image",
                name = name,
                mimeType = if (mimeType.startsWith("image/")) mimeType else "image/jpeg",
                base64Data = base64
            )
        }
    }

    fun attachBitmap(bitmap: Bitmap, name: String = "edited_photo.jpg") {
        viewModelScope.launch {
            val base64 = withContext(Dispatchers.IO) {
                FileUtils.bitmapToBase64(bitmap)
            }
            _attachedMedia.value = AttachedMedia(
                uri = null,
                type = "image",
                name = name,
                mimeType = "image/jpeg",
                base64Data = base64
            )
        }
    }

    fun attachDocument(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val name = FileUtils.getFileName(context, uri)
            val mimeType = FileUtils.getMimeType(context, uri)
            val extractedText = withContext(Dispatchers.IO) {
                FileUtils.readTextFromUri(context, uri)
            }
            _attachedMedia.value = AttachedMedia(
                uri = uri,
                type = "file",
                name = name,
                mimeType = mimeType,
                extractedText = extractedText
            )
        }
    }

    fun attachAudio(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val name = FileUtils.getFileName(context, uri)
            val mimeType = FileUtils.getMimeType(context, uri).let {
                if (it.startsWith("audio/")) it else "audio/mp3"
            }
            val base64 = withContext(Dispatchers.IO) {
                FileUtils.uriToBase64(context, uri)
            }
            _attachedMedia.value = AttachedMedia(
                uri = uri,
                type = "audio",
                name = name,
                mimeType = mimeType,
                base64Data = base64
            )
        }
    }

    fun clearAttachment() {
        _attachedMedia.value = null
    }

    fun startNewChatSession() {
        val newId = UUID.randomUUID().toString()
        _currentSessionId.value = newId
        _inputText.value = ""
        _attachedMedia.value = null
        prefs.edit().putString("last_active_session_id", newId).apply()
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
        _attachedMedia.value = null
        ttsManager.stop()
        prefs.edit().putString("last_active_session_id", sessionId).apply()
    }

    fun sendMessage(promptText: String = _inputText.value) {
        val prompt = promptText.trim()
        val currentAttachment = _attachedMedia.value
        if ((prompt.isBlank() && currentAttachment == null) || _isGenerating.value) return

        val userEmail = _currentUserEmail.value
        // Check daily free quota or subscription status
        if (!subscriptionManager.canSendMessage(userEmail)) {
            viewModelScope.launch {
                var activeSessionId = _currentSessionId.value
                if (activeSessionId.isBlank()) {
                    activeSessionId = UUID.randomUUID().toString()
                    _currentSessionId.value = activeSessionId
                }
                val limitMessage = ChatMessage(
                    sessionId = activeSessionId,
                    role = "model",
                    content = "⚠️ আপোনাৰ আজিৰ বিনামূলীয়া ১০ টা প্ৰশ্নৰ সীমা (Daily Free Limit) শেষ হৈছে।\n\nসীমাহীনভাৱে ব্যৱহাৰ কৰিবলৈ অনুগ্ৰহ কৰি তলৰ বুটামত ক্লিক কৰি Unlimited Plan-লৈ উন্নীত কৰক (Upgrade to Unlimited)।",
                    timestamp = System.currentTimeMillis(),
                    isError = true
                )
                repository.saveMessage(limitMessage)
                _showUpgradeDialog.value = true
                refreshSubscriptionStatus()
            }
            return
        }

        viewModelScope.launch {
            // Consume 1 quota count and update state
            subscriptionManager.consumeMessage(userEmail)
            refreshSubscriptionStatus()

            var activeSessionId = _currentSessionId.value
            if (activeSessionId.isBlank()) {
                activeSessionId = UUID.randomUUID().toString()
                _currentSessionId.value = activeSessionId
            }
            prefs.edit().putString("last_active_session_id", activeSessionId).apply()

            val displayPrompt = if (prompt.isNotBlank()) {
                prompt
            } else if (currentAttachment != null) {
                "Attached ${currentAttachment.type}: ${currentAttachment.name}"
            } else {
                "Question"
            }

            // Retrieve existing session or create a new permanent one
            val existingSession = repository.getSession(activeSessionId)
            val sessionTitle = existingSession?.title ?: if (displayPrompt.length > 32) "${displayPrompt.take(32)}..." else displayPrompt
            val session = existingSession?.copy(
                userEmail = _currentUserEmail.value,
                lastUpdated = System.currentTimeMillis(),
                summary = displayPrompt
            ) ?: ChatSession(
                sessionId = activeSessionId,
                userEmail = _currentUserEmail.value,
                title = sessionTitle,
                lastUpdated = System.currentTimeMillis(),
                summary = displayPrompt
            )
            repository.saveSession(session)

            // Prepare prompt with document context if document attached
            val finalPrompt = if (currentAttachment?.extractedText != null) {
                """
                Uploaded Document: ${currentAttachment.name}
                Document Content:
                ${currentAttachment.extractedText}
                
                User Request: $prompt
                """.trimIndent()
            } else {
                prompt
            }

            // Save user message in DB
            val userMessage = ChatMessage(
                sessionId = activeSessionId,
                role = "user",
                content = if (prompt.isNotBlank()) prompt else "Please analyze this attached ${currentAttachment?.type ?: "file"}",
                timestamp = System.currentTimeMillis(),
                attachmentUri = currentAttachment?.uri?.toString(),
                attachmentType = currentAttachment?.type,
                attachmentName = currentAttachment?.name
            )
            repository.saveMessage(userMessage)

            // Clear input text and attachment
            _inputText.value = ""
            _attachedMedia.value = null
            _isGenerating.value = true

            // Send to Gemini API with optional base64 image, mode prompt instructions, and speed optimization
            val history = currentMessages.value
            val aiResult = repository.sendMessageToGemini(
                history = history,
                userPrompt = finalPrompt,
                imageBase64 = currentAttachment?.base64Data,
                imageMimeType = currentAttachment?.mimeType ?: "image/jpeg",
                turboSpeed = _turboSpeedMode.value,
                aiMode = _selectedAiMode.value,
                language = _selectedLanguage.value
            )

            val isErr = aiResult.isError || aiResult.text.startsWith("Error") || aiResult.text.startsWith("API Key Configuration Required")

            val aiMessage = ChatMessage(
                sessionId = activeSessionId,
                role = "model",
                content = aiResult.text,
                timestamp = System.currentTimeMillis(),
                isError = isErr,
                attachmentUri = aiResult.imageUri,
                attachmentType = if (aiResult.imageUri != null) "image" else null,
                attachmentName = if (aiResult.imageUri != null) "AI Generated Photo / Banner" else null
            )
            val msgId = repository.saveMessage(aiMessage)

            // Update session timestamp
            repository.saveSession(
                session.copy(
                    lastUpdated = System.currentTimeMillis(),
                    summary = if (aiResult.text.length > 50) "${aiResult.text.take(50)}..." else aiResult.text
                )
            )

            _isGenerating.value = false

            // If Auto-Speech is enabled and no error, speak out the answer
            if (_autoSpeakEnabled.value && !isErr && aiResult.text.isNotBlank()) {
                ttsManager.speak(msgId, aiResult.text)
            }
        }
    }

    fun generateBannerOrPhoto(prompt: String, aspectRatio: String, style: String) {
        val promptWithMetadata = "[banner] $prompt (Aspect: $aspectRatio, Style: $style)"
        sendMessage(promptWithMetadata)
    }

    fun retryMessage(failedPrompt: String) {
        sendMessage(failedPrompt)
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = repository.getLatestSession(_currentUserEmail.value)
                if (remaining != null) {
                    _currentSessionId.value = remaining.sessionId
                    prefs.edit().putString("last_active_session_id", remaining.sessionId).apply()
                } else {
                    startNewChatSession()
                }
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistoryForUser(_currentUserEmail.value)
            prefs.edit().remove("last_active_session_id").apply()
            startNewChatSession()
        }
    }

    fun speakMessage(messageId: Long, text: String) {
        ttsManager.speak(messageId, text)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
