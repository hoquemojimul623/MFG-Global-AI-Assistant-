package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class VideoTemplate(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val defaultPrompt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoStudioDialog(
    onDismiss: () -> Unit,
    onGenerateVideoScript: (String) -> Unit
) {
    var topicText by remember { mutableStateOf("") }
    var aspectRatio by remember { mutableStateOf("16:9") } // 16:9 or 9:16
    val selectedModel = "veo-3.1-fast-generate-preview"

    val templates = listOf(
        VideoTemplate(
            title = "Educational Lecture & Lesson",
            description = "বক্তৃতা, বিজ্ঞান, অঙ্ক আৰু পাঠৰ বাবে দৃশ্যপট সহ সম্পূৰ্ণ Video Script",
            icon = Icons.Default.School,
            defaultPrompt = "Create a complete, engaging 3-minute educational video script with scene-by-scene visual descriptions, timestamps, voiceover in Assamese and English for the topic: "
        ),
        VideoTemplate(
            title = "Reels / Shorts / TikTok Script",
            description = "৩০ চেকেণ্ডৰ আকৰ্ষণীয় Viral Short Video Hook, Script & Captions",
            icon = Icons.Default.Videocam,
            defaultPrompt = "Generate a highly viral 45-second YouTube Short / Reel video script with a strong 3-second hook, visual cues, captions, and call to action on the topic: "
        ),
        VideoTemplate(
            title = "Science & Tech Explainer",
            description = "জটিল বৈজ্ঞানিক বিষয় সহজ এনিমেশ্যনৰ যোগেদি বুজাবলৈ ভিডিঅ' ৰূপৰেখা",
            icon = Icons.Default.Science,
            defaultPrompt = "Write an entertaining 3D animation explainer video script detailing step-by-step how things work for: "
        ),
        VideoTemplate(
            title = "History & Assam Heritage Story",
            description = "অসমৰ ইতিহাস, সংস্কৃতি আৰু মহৎ ব্যক্তিৰ জীৱনভিত্তিক কাহিনী",
            icon = Icons.Default.Movie,
            defaultPrompt = "Write a captivating documentary-style video script with dramatic narration and visual storyboard about: "
        )
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
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Video Creation & Script Studio 🎬",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AI Storyboard • Reels • Educational Video Notes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Generate Any Video Content with AI 🎥",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Enter your video idea or choose an educational template below. MFG AI will design the entire script, visual instructions, and audio voiceover!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilterChip(
                                        selected = aspectRatio == "16:9",
                                        onClick = { aspectRatio = "16:9" },
                                        label = { Text("🖥️ 16:9 (Landscape / YouTube)") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                    FilterChip(
                                        selected = aspectRatio == "9:16",
                                        onClick = { aspectRatio = "9:16" },
                                        label = { Text("📱 9:16 (Reel / Short)") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = topicText,
                                    onValueChange = { topicText = it },
                                    label = { Text("Video Topic / Prompt") },
                                    placeholder = { Text("e.g. Solar System in Assamese, Coding in Kotlin, Photosynthesis") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (topicText.isNotBlank()) {
                                            val fullPrompt = "Generate video content using model [$selectedModel] with aspect ratio $aspectRatio for prompt: $topicText"
                                            onGenerateVideoScript(fullPrompt)
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("generate_video_submit_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    enabled = topicText.isNotBlank()
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Video ($selectedModel) ✨")
                                }

                            }
                        }
                    }

                    item {
                        Text(
                            text = "Or Choose a Popular Video Template:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(templates) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val query = if (topicText.isNotBlank()) {
                                        "${item.defaultPrompt} $topicText"
                                    } else {
                                        "${item.defaultPrompt} (Provide an exemplary popular topic with full script)"
                                    }
                                    onGenerateVideoScript(query)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Select",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
