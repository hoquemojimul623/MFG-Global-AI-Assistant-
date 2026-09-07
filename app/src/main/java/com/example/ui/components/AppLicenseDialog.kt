package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val LICENSE_CERTIFICATE_ID = "MFG-AI-2026-LIC-MH69"
private const val OWNER_NAME = "MOJIMUL HOQUE"
private const val ORGANIZATION = "MFG EDUCATION"
private const val INSTAGRAM_HANDLE = "@mfgmojimul69"

@Composable
fun AppLicenseDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("চৰ্তাৱলী (Terms)", "মুকলি উৎস (Open Source)", "সুৰক্ষা & স্বত্ব (Legal)")

    val tealColor = Color(0xFF0D9488)
    val goldColor = Color(0xFFD97706)
    val skyColor = Color(0xFF0284C7)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.testTag("app_license_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = tealColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "License",
                                tint = tealColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "এপ অনুজ্ঞাপত্ৰ & আইনী স্বত্ব",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MFG Global AI Official License",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // Official License Certificate Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(tealColor, skyColor, goldColor))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Certificate",
                                    tint = tealColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "বৈধ অনুজ্ঞাপত্ৰ (Verified License)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = tealColor
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = goldColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "2026 EDITION",
                                    color = goldColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "আইডি (Certificate ID):",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = LICENSE_CERTIFICATE_ID,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("license_certificate_id")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "স্বত্বাধিকাৰী আৰু নিয়োজক: $OWNER_NAME (Instagram: $INSTAGRAM_HANDLE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "সংস্থা: $ORGANIZATION",
                            fontSize = 11.sp,
                            color = skyColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // TabRow for navigation between License Sections
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> LicenseTermsTabContent()
                        1 -> OpenSourceTabContent()
                        2 -> SecurityAndCopyrightTabContent()
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("close_license_button")
            ) {
                Text("বুজি পালোঁ (OK)")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(
                        "MFG Global AI License Certificate",
                        """
                        MFG GLOBAL AI SOFTWARE LICENSE
                        Certificate ID: $LICENSE_CERTIFICATE_ID
                        Owner: $OWNER_NAME
                        Organization: $ORGANIZATION
                        Instagram: $INSTAGRAM_HANDLE
                        Copyright (c) 2026 MFG EDUCATION. All Rights Reserved.
                        """.trimIndent()
                    )
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "অনুজ্ঞাপত্ৰ তথ্য কপি কৰা হ'ল!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("copy_license_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("কপি কৰক (Copy)")
            }
        }
    )
}

@Composable
private fun LicenseTermsTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "১. অনুজ্ঞাপত্ৰ প্ৰদান (Grant of License)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "এই এপত অন্তৰ্ভুক্ত সকলো সুবিধা ব্যক্তিগত, শৈক্ষিক আৰু তথ্যমূলক উদ্দেশ্যে ব্যৱহাৰ কৰিবলৈ অন্তিম ব্যৱহাৰকাৰীক অপ্ৰত্যাহাৰ্যভাৱে অনুমতি দিয়া হৈছে।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "২. সীমাবদ্ধতা (Usage Restrictions)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "• MFG EDUCATION বা MOJIMUL HOQUE ৰ লিখিত অনুমতি অবিহনে এই এপৰ উৎস ক'ড বিক্ৰী, পুনৰবিতৰণ বা অননুমোদিত ব্যৱসায়িক লাভৰ বাবে ব্যৱহাৰ কৰা নিষিদ্ধ।\n• এপৰ কোনো সুৰক্ষা ব্যৱস্থা উলংঘা কৰা বা অস্বাভাৱিক ৰূপান্তৰণ কৰা আইনমতে দণ্ডনীয়।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "৩. সেৱা আৰু দায়িত্বমুক্তি (Warranty & Disclaimer)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "এই এপ \"AS IS\" ভিত্তিত প্ৰদান কৰা হৈছে। ব্যৱহাৰকাৰীৰ ডেটাৰ সৰ্বোচ্চ গোপনীয়তা ৰক্ষা কৰাটো নিশ্চিত কৰা হৈছে (Local Encrypted Room Storage)।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OpenSourceTabContent() {
    val libraries = listOf(
        Pair("Android Jetpack & Jetpack Compose", "Apache License 2.0 (Google LLC)"),
        Pair("Kotlin & Kotlinx Coroutines", "Apache License 2.0 (JetBrains s.r.o.)"),
        Pair("AndroidX Room SQLite Database", "Apache License 2.0 (Google LLC)"),
        Pair("Square Retrofit, Moshi & OkHttp", "Apache License 2.0 (Square, Inc.)"),
        Pair("Coil Image Loading Library", "Apache License 2.0 (Coil Contributors)"),
        Pair("Material Design 3 Components", "Apache License 2.0 (Google LLC)")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "মুকলি উৎস লাইব্ৰেৰী শ্ৰদ্ধাঞ্জলি (Third-Party Open Source)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "MFG Global AI এপে বিশ্বমানৰ বিশ্বাসযোগ্য মুকলি উৎস লাইব্ৰেৰীসমূহৰ ওপৰত নিৰ্মাণ কৰা হৈছে:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        libraries.forEach { (name, license) ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text(text = license, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityAndCopyrightTabContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "সুৰক্ষা আৰু স্বত্বাধিকাৰ (Security & Copyright)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0D9488).copy(alpha = 0.08f),
            border = BorderStroke(1.dp, Color(0xFF0D9488).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MFG Fortified Security Shield",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF0D9488)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• SHA-256 Cryptographic Salted Password Hash\n• Anti-Brute-Force Account Lockdown Guard\n• On-Device Room Local Encrypted Vault\n• Zero-Leak Memory Isolation Protection",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "© 2026 MFG EDUCATION. All Rights Reserved.\nস্বত্বাধিকাৰী আৰু নিয়োজক: $OWNER_NAME (Instagram: $INSTAGRAM_HANDLE)\nসংস্থা: $ORGANIZATION\nপঞ্জীভুক্ত অনুজ্ঞাপত্ৰ: $LICENSE_CERTIFICATE_ID",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}
