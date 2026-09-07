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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class MusicGenreTemplate(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val prompt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicStudioDialog(
    onDismiss: () -> Unit,
    onGenerateMusic: (String) -> Unit
) {
    var musicPrompt by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("lyria-3-clip-preview") } // lyria-3-clip-preview or lyria-3-pro-preview

    val genreTemplates = listOf(
        MusicGenreTemplate(
            title = "Bihu & Traditional Folk Fusion",
            description = "অসমৰ ঢোল, পেঁপা, গগনা আৰু বাঁহীৰ সুৰত সজোৱা লোকসংগীত",
            icon = Icons.Default.MusicNote,
            prompt = "Generate a vibrant Assamese Bihu traditional folk track featuring Dhol, Pepa, and flute melody with energetic festival rhythm."
        ),
        MusicGenreTemplate(
            title = "Chill Lofi Study Beats",
            description = "পঢ়া-শুনা আৰু ধ্যানৰ বাবে শান্ত শিথিল ল'ফাই পিয়ানো সংগীত",
            icon = Icons.Default.Headphones,
            prompt = "Compose a calming Lo-Fi chillhop soundtrack with soft electric piano chords, mellow vinyl crackle, and soothing study vibes."
        ),
        MusicGenreTemplate(
            title = "Cinematic Epic Orchestral",
            description = "ভিডিঅ' আৰু চলচ্চিত্ৰৰ বাবে আকৰ্ষণীয় বৰ্ধিত বাদ্যযন্ত্ৰী সংগীত",
            icon = Icons.Default.GraphicEq,
            prompt = "Compose a 30-second dramatic cinematic orchestral score with rising brass, deep cello lines, and epic percussion."
        ),
        MusicGenreTemplate(
            title = "Acoustic Guitar & Flute Ballad",
            description = "হৃদয়স্পৰ্শী একোষ্টিক গীটাৰ আৰু বাঁহীৰ মধুৰ সুৰ",
            icon = Icons.Default.LibraryMusic,
            prompt = "Create an emotional acoustic fingerstyle guitar and gentle bamboo flute acoustic melody with warm ambient reverb."
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
                                text = "Music & Sound Studio 🎵",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lyria AI Music • Bihu Fusion • Lofi Beats",
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
                                    text = "Generate Original Music with AI 🎼",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Create instrumental audio clips, Bihu folk rhythms, or cinematic background scores using Google Lyria AI models.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilterChip(
                                        selected = selectedModel == "lyria-3-clip-preview",
                                        onClick = { selectedModel = "lyria-3-clip-preview" },
                                        label = { Text("⚡ lyria-3-clip (Short)") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                    FilterChip(
                                        selected = selectedModel == "lyria-3-pro-preview",
                                        onClick = { selectedModel = "lyria-3-pro-preview" },
                                        label = { Text("👑 lyria-3-pro (Full)") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = musicPrompt,
                                    onValueChange = { musicPrompt = it },
                                    label = { Text("Music Concept or Instruments") },
                                    placeholder = { Text("e.g. Assamese Dhol rhythm with modern upbeat synthesizer") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (musicPrompt.isNotBlank()) {
                                            val fullQuery = "Generate a musical track using model [$selectedModel]: $musicPrompt"
                                            onGenerateMusic(fullQuery)
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("generate_music_submit_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    enabled = musicPrompt.isNotBlank()
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Music Now ✨")
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Or Choose a Music Preset:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(genreTemplates) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val fullQuery = if (musicPrompt.isNotBlank()) {
                                        "Generate a musical track using model [$selectedModel] based on ${item.title}: $musicPrompt"
                                    } else {
                                        "Generate a musical track using model [$selectedModel]: ${item.prompt}"
                                    }
                                    onGenerateMusic(fullQuery)
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
