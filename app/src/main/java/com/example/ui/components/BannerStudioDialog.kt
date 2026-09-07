package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class BannerPreset(
    val title: String,
    val category: String,
    val prompt: String,
    val aspectRatio: String,
    val style: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BannerStudioDialog(
    onDismiss: () -> Unit,
    onGenerate: (prompt: String, aspectRatio: String, style: String) -> Unit
) {
    var promptText by remember { mutableStateOf("") }
    var selectedAspect by remember { mutableStateOf("16:9") }
    var selectedStyle by remember { mutableStateOf("Photorealistic") }

    val styles = listOf(
        "Photorealistic" to "প্ৰকৃত ফটো",
        "Digital Art" to "ডিজিটেল আৰ্ট",
        "3D Render" to "৩ডি চিনেমেটিক",
        "Graphic Design" to "গ্ৰাফিক ডিজাইন",
        "Anime" to "এনিমে",
        "Vintage" to "ভিন্টেজ আৰ্ট"
    )

    val aspectRatios = listOf(
        "16:9" to "YouTube / Banner (16:9)",
        "1:1" to "Square Photo (1:1)",
        "9:16" to "Phone / Story (9:16)",
        "3:4" to "Poster (3:4)",
        "4:3" to "Landscape (4:3)"
    )

    val presets = listOf(
        BannerPreset(
            title = "ৰঙালী বিহু উৎসৱ বেনাৰ 🌾",
            category = "অসমীয়া কৃষ্টি",
            prompt = "Traditional Assamese Rongali Bihu festival celebration banner, vibrant colors, cultural motifs with Dhol, Pepa, Japi, Kopou Phool orchid flowers, tea garden background, high quality celebration banner",
            aspectRatio = "16:9",
            style = "Graphic Design",
            icon = Icons.Default.Celebration
        ),
        BannerPreset(
            title = "YouTube Cover / Channel Banner 📺",
            category = "ছ'চিয়েল মিডিয়া",
            prompt = "Ultra modern YouTube channel cover banner for tech, education and AI, glowing neon cyberpunk grid, futuristic aesthetic, sleek digital graphic design layout",
            aspectRatio = "16:9",
            style = "Digital Art",
            icon = Icons.Default.VideoLibrary
        ),
        BannerPreset(
            title = "দোকান / ব্যৱসায়িক অফাৰ বেনাৰ 🛍️",
            category = "ব্যৱসায়িক",
            prompt = "Grand opening and special mega discount commercial promotional banner for retail store, luxury navy blue and gold festive ribbons, modern commercial advertisement graphic banner",
            aspectRatio = "16:9",
            style = "Graphic Design",
            icon = Icons.Default.Storefront
        ),
        BannerPreset(
            title = "কাজিৰঙা ৰয়েল বেংগল বাঘৰ ফটো 🐅",
            category = "বন্যপ্ৰাণী ফটো",
            prompt = "Majestic Royal Bengal Tiger roaming through mist in Kaziranga National Park Assam, golden morning sunrise, 8k National Geographic documentary wildlife photograph",
            aspectRatio = "1:1",
            style = "Photorealistic",
            icon = Icons.Default.PhotoCamera
        ),
        BannerPreset(
            title = "অসমৰ চাহ বাগানৰ সূৰ্য্যোদয় ☕",
            category = "প্ৰাকৃতিক দৃশ্য",
            prompt = "Panoramic lush green rolling Assam tea garden at dawn, cinematic golden sunrise, mountain mist, tea workers in traditional Japi conical hats, breathtaking nature landscape photography",
            aspectRatio = "16:9",
            style = "Photorealistic",
            icon = Icons.Default.Wallpaper
        ),
        BannerPreset(
            title = "ভৱিষ্যতৰ এআই ৰবট ডিজিটেল আৰ্ট 🤖",
            category = "বিজ্ঞান আৰু কল্পবিজ্ঞান",
            prompt = "Futuristic humanoid AI robot with translucent glass chassis and glowing cyan neural core sitting at a hologram computer, neon reflections, Octane render 3D art",
            aspectRatio = "1:1",
            style = "3D Render",
            icon = Icons.Default.AutoAwesome
        )
    )

    fun enhancePrompt() {
        val current = promptText.trim()
        if (current.isBlank()) {
            promptText = "Stunning scenic landscape of Assam hills with serene river, golden sunset lighting, 8k resolution masterwork"
            return
        }
        val prefix = when {
            selectedAspect == "16:9" -> "Stunning high-impact graphic banner design: "
            selectedAspect == "3:4" -> "Professional artistic poster layout: "
            else -> "Award-winning high-resolution artwork: "
        }
        promptText = "$prefix$current, $selectedStyle style, volumetric lighting, immaculate details, 8k ultra-sharp composition"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ChatGPT AI Photo & Banner Studio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "ফটো, বেনাৰ, পোষ্টাৰ আৰু এআই আৰ্ট মেকাৰ (DALL-E & Gemini)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0C243B)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0284C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "ChatGPT AI ৰ দৰে ফটো আৰু বেনাৰ বনাওক",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "YouTube বেনাৰ, দোকানৰ অফাৰ, বিহুৰ বেনাৰ, ৱালপেপাৰ বা HD ফটো সৃষ্টি কৰক।",
                                        color = Color(0xFFBAE6FD),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Prompt Input Section
                    item {
                        Text(
                            text = "১. আপোনাৰ ফটো বা বেনাৰৰ বিৱৰণ (Prompt):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("banner_prompt_input"),
                            minLines = 3,
                            maxLines = 6,
                            placeholder = {
                                Text("যেনে: 'ৰঙালী বিহুৰ উপলক্ষে শুভেচ্ছা জনাই এখন ধুনীয়া বেনাৰ' বা 'YouTube channel cover banner' বা 'Sunset photo in Assam tea garden'...")
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { enhancePrompt() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "✨ ChatGPT Prompt Enhancer",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Aspect Ratio Selector
                    item {
                        Text(
                            text = "২. অনুপাত (Aspect Ratio):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            aspectRatios.forEach { (ratio, label) ->
                                FilterChip(
                                    selected = selectedAspect == ratio,
                                    onClick = { selectedAspect = ratio },
                                    label = { Text(label, fontSize = 12.sp) },
                                    leadingIcon = if (selectedAspect == ratio) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else {
                                        { Icon(Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Style Selector
                    item {
                        Text(
                            text = "৩. শৈলী (Visual Art Style):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            styles.forEach { (eng, asName) ->
                                FilterChip(
                                    selected = selectedStyle == eng,
                                    onClick = { selectedStyle = eng },
                                    label = { Text("$asName ($eng)", fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // Inspiration Presets
                    item {
                        Text(
                            text = "💡 জনপ্ৰিয় বেনাৰ আৰু ফটো টেমপ্লেট (One-Tap Presets):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(presets) { preset ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    promptText = preset.prompt
                                    selectedAspect = preset.aspectRatio
                                    selectedStyle = preset.style
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        preset.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${preset.category} • ${preset.aspectRatio} • ${preset.style}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "ব্যৱহাৰ",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Bottom Action Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("বাতিল (Cancel)")
                        }

                        Button(
                            onClick = {
                                if (promptText.isNotBlank()) {
                                    onGenerate(promptText, selectedAspect, selectedStyle)
                                    onDismiss()
                                }
                            },
                            enabled = promptText.isNotBlank(),
                            modifier = Modifier
                                .weight(2f)
                                .testTag("generate_banner_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0284C7),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ফটো / বেনাৰ সৃষ্টি কৰক 🚀", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
