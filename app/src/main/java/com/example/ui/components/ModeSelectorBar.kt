package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMode

@Composable
fun ModeSelectorBar(
    selectedMode: AiMode,
    onModeSelected: (AiMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto scroll to make the selected mode visible
    LaunchedEffect(selectedMode) {
        val index = AiMode.entries.indexOf(selectedMode)
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    val orangeBrand = Color(0xFFEA580C)
    val cardBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 4.dp)
            .testTag("mode_selector_bar")
    ) {
        // Model Selection Chips Row
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(AiMode.entries, key = { it.id }) { mode ->
                val isSelected = mode == selectedMode

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) orangeBrand else borderColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onModeSelected(mode) }
                        .testTag("mode_chip_${mode.id}"),
                    color = if (isSelected) {
                        orangeBrand.copy(alpha = 0.15f)
                    } else {
                        cardBg
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Icon
                        Text(
                            text = mode.emoji,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )

                        // Title
                        Text(
                            text = mode.title,
                            color = if (isSelected) orangeBrand else MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false
                        )

                        // Assamese short badge
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) orangeBrand else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = getShortBadgeForMode(mode),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active Model",
                                tint = orangeBrand,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Info Button to explain all models in full detail
            item {
                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("model_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Model Descriptions & Details",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Active Model Explanation Strip
        AnimatedContent(
            targetState = selectedMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "modelExplanationAnimation"
        ) { targetMode ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 4.dp)
                    .clickable { showInfoDialog = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${targetMode.emoji} ${targetMode.title}:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = orangeBrand,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = targetMode.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 11.5.sp
                        )
                    }

                    Text(
                        text = "বিৱৰণ ℹ️",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }

    // Comprehensive Dialog detailing all 6 AI models
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🤖 AI Models গাইড & বিৱৰণ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "প্ৰত্যেকটি Model ৰ বিশেষ ক্ষমতা তলত উল্লেখ কৰা হ'ল। আপোনাৰ প্ৰয়োজন অনুসৰি উপযুক্ত Model বাছনি কৰক:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AiMode.entries.forEach { mode ->
                        val isSelected = mode == selectedMode
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    orangeBrand.copy(alpha = 0.12f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, orangeBrand) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onModeSelected(mode)
                                    showInfoDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = mode.emoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = mode.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) orangeBrand else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = orangeBrand
                                        ) {
                                            Text(
                                                text = "নিৰ্বাচিত (Active)",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mode.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = getDetailedDescription(mode),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("বুজি পালোঁ (OK)")
                }
            }
        )
    }
}

private fun getShortBadgeForMode(mode: AiMode): String {
    return when (mode) {
        AiMode.FAST -> "দ্ৰুত"
        AiMode.VERY_FAST -> "খৰতকীয়া"
        AiMode.AI_MODEL -> "গভীৰ যুক্তি"
        AiMode.MFG_MODEL -> "MFG VIP"
        AiMode.EDUCATION -> "শিক্ষা"
        AiMode.BUSINESS -> "ব্যৱসায়"
        AiMode.SEARCH -> "Live Search"
        AiMode.MAPS -> "Google Maps"
    }
}

private fun getDetailedDescription(mode: AiMode): String {
    return when (mode) {
        AiMode.FAST -> "দ্ৰুত, সংক্ষিপ্ত আৰু সঠিক পোনপটীয়া উত্তৰ প্ৰদান কৰে। সাধাৰণ প্ৰশ্নৰ বাবে উপযুক্ত।"
        AiMode.VERY_FAST -> "অতি খৰতকীয়া, কম সময়ত পোনপটীয়া বুলেত পইণ্টত উত্তৰ দিয়ে।"
        AiMode.AI_MODEL -> "জটিল প্ৰশ্ন, গভীৰ বিশ্লেষণ, যুক্তি আৰু খোজ-অনুক্ৰমিক ব্যাখ্যাৰ বাবে আদৰ্শ।"
        AiMode.MFG_MODEL -> "MFG Education ৰ অফিচিয়েল মান্য শৈলী আৰু MOJIMUL HOQUE ৰ নিৰ্দেশিত প্ৰিমিয়াম ৰেফাৰেন্স।"
        AiMode.EDUCATION -> "ছাত্ৰ-ছাত্ৰী আৰু শিক্ষাবিদৰ বাবে উপযুক্ত। গণিত, বিজ্ঞান, ব্যাকৰণ আৰু স্পষ্ট শিক্ষা সহায়ক।"
        AiMode.BUSINESS -> "ব্যৱসায় কৌশল, ষ্টাৰ্টআপ পৰিকল্পনা, বিত্তীয় বৃদ্ধি আৰু কৰ্পৰেট পৰামৰ্শ।"
        AiMode.SEARCH -> "Google Search ৰ জৰিয়তে বাস্তৱ সময়ৰ লাইভ ৱেব তথ্য, শেহতীয়া খবৰ আৰু তথ্যসূত্ৰ গ্ৰাউণ্ডিং।"
        AiMode.MAPS -> "Google Maps স্থান, ঠিকনা, ভৌগোলিক দূৰত্ব, নেভিগেচন আৰু স্থানীয় তথ্য।"
    }
}
