package com.example.ui.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _speakingMessageId = MutableStateFlow<Long?>(null)
    val speakingMessageId: StateFlow<Long?> = _speakingMessageId.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.getDefault()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    _speakingMessageId.value = null
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _speakingMessageId.value = null
                }
            })
        }
    }

    fun setLanguage(localeTag: String) {
        if (!isInitialized || localeTag.isBlank()) return
        try {
            val locale = Locale.forLanguageTag(localeTag)
            tts?.language = locale
        } catch (_: Exception) {}
    }

    fun speak(messageId: Long, text: String) {
        if (!isInitialized) return

        if (_speakingMessageId.value == messageId) {
            stop()
            return
        }

        stop()
        _speakingMessageId.value = messageId
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, messageId.toString())
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _speakingMessageId.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
