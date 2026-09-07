package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.Locale

data class VoiceLang(val code: String, val name: String, val tag: String)

@Composable
fun VoiceAssistantDialog(
    isGenerating: Boolean,
    lastAiResponse: String?,
    isSpeaking: Boolean,
    onDismiss: () -> Unit,
    onVoiceQuery: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onTranscribeAudio: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val languages = listOf(
        VoiceLang("as-IN", "অসমীয়া", "as"),
        VoiceLang("en-US", "English", "en"),
        VoiceLang("bn-IN", "বাংলা", "bn"),
        VoiceLang("hi-IN", "हिन्दी", "hi")
    )
    var selectedLang by remember { mutableStateOf(languages[0]) }
    var recognizedSpokenText by remember { mutableStateOf("") }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                recognizedSpokenText = spokenText
                onVoiceQuery(spokenText)
            }
        }
    }

    fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLang.code)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now (${selectedLang.name})...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
        }
    }

    // Glowing animations
    val infiniteTransition = rememberInfiniteTransition(label = "voice_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking || isGenerating) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Voice AI Assistant 🎙️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC)
                        )
                        Text(
                            text = "gemini-3.1-flash-live-preview • Voice Chat & Audio",
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    IconButton(
                        onClick = {
                            onStopSpeaking()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Voice Assistant",
                            tint = Color.White
                        )
                    }
                }

                // Language Selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(languages) { lang ->
                        FilterChip(
                            selected = selectedLang == lang,
                            onClick = { selectedLang = lang },
                            label = { Text(lang.name, color = if (selectedLang == lang) Color.Black else Color.White) }
                        )
                    }
                }

                // Central AI Sphere with Pulsing Wave
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(240.dp)
                ) {
                    // Outer Ring
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = if (isSpeaking) {
                                        listOf(Color(0xFF10B981).copy(alpha = 0.6f), Color(0xFF059669).copy(alpha = 0.2f), Color.Transparent)
                                    } else if (isGenerating) {
                                        listOf(Color(0xFF8B5CF6).copy(alpha = 0.6f), Color(0xFF6366F1).copy(alpha = 0.2f), Color.Transparent)
                                    } else {
                                        listOf(Color(0xFF0284C7).copy(alpha = 0.5f), Color(0xFF0369A1).copy(alpha = 0.15f), Color.Transparent)
                                    }
                                )
                            )
                    )

                    // Inner Orb
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = if (isSpeaking) {
                                        listOf(Color(0xFF10B981), Color(0xFF047857))
                                    } else if (isGenerating) {
                                        listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))
                                    } else {
                                        listOf(Color(0xFF0284C7), Color(0xFF2563EB))
                                    }
                                )
                            )
                            .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .clickable { startListening() }
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                // Dynamic Status & Transcript Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E293B)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when {
                                isGenerating -> "Thinking & processing your question..."
                                isSpeaking -> "Speaking answer aloud 🔊..."
                                recognizedSpokenText.isNotBlank() -> "You asked: \"$recognizedSpokenText\""
                                else -> "Tap the Mic or button below and ask anything!"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE2E8F0),
                            textAlign = TextAlign.Center
                        )

                        if (!lastAiResponse.isNullOrBlank() && !isGenerating) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = lastAiResponse.take(200) + if (lastAiResponse.length > 200) "..." else "",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Controls Row
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isSpeaking) {
                        Surface(
                            color = Color(0xFFEF4444),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.clickable { onStopSpeaking() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeOff, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Stop Speaking", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF0284C7),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.clickable { startListening() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tap to Speak (${selectedLang.name})", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (onTranscribeAudio != null) {
                                Surface(
                                    color = Color(0xFF475569),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.clickable {
                                        onDismiss()
                                        onTranscribeAudio()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Transcribe Audio File", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
