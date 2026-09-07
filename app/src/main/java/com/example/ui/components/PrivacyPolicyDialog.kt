package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val PRIVACY_CONTACT_EMAIL = "mojimulk2@gmail.com"
private const val APP_DEVELOPER = "MOJIMUL HOQUE (MFG Education)"
private const val POLICY_VERSION = "2026.1 (Effective September 2026)"

@Composable
fun PrivacyPolicyDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("সাৰাংশ (Overview)", "অনুমতি (Permissions)", "AI সুৰক্ষা (AI Safety)", "Google Play Policy")

    val securityBlue = Color(0xFF0284C7)
    val emeraldGreen = Color(0xFF059669)

    val fullPolicyText = remember {
        """
        =====================================================
        GLOBAL AI ASSISTANT - PRIVACY POLICY (গোপনীয়তা নীতি)
        =====================================================
        App Name: Global AI Assistant (MFG Global AI)
        Developer & Owner: $APP_DEVELOPER
        Contact Email: $PRIVACY_CONTACT_EMAIL
        Policy Version: $POLICY_VERSION

        1. INTRODUCTION & CORE PRINCIPLE:
        At Global AI Assistant, we believe privacy is a fundamental human right. We are committed to protecting your personal information and being transparent about our practices. We do not sell, rent, or trade your personal data with any third party, broker, or advertising network.

        2. DATA COLLECTION AND USAGE:
        • Text Messages & Prompts: Transmitted via encrypted HTTPS TLS 1.3 to Google Gemini API servers strictly to generate AI responses. They are stored locally on your device in a private, encrypted database.
        • Camera & Photos: Used solely when you explicitly take a photo or select an image for AI analysis, OCR, or banner generation. Never uploaded or shared without your direct command.
        • Audio & Microphone: Used strictly in real-time for speech-to-text queries and voice interaction. No audio is recorded or stored permanently on external servers.
        • Documents: Accessed only when you explicitly pick a document via the system storage picker for educational analysis.
        • Device & Security Integrity: MFG Anti-Hack Guard checks on-device security signals (e.g., root integrity) locally to protect your data. No device IDs or telemetry are sent to advertisers.

        3. DATA RETENTION AND DELETION:
        All chat history and generated media are stored exclusively on your device. You can clear and delete all chat data at any time with a single tap in the app drawer ("Clear All Chat History"). Uninstalling the app permanently removes all local data.

        4. THIRD-PARTY AI SERVICES:
        We utilize Google Gemini API infrastructure. Communications adhere to Google Cloud's Enterprise Data Security and Privacy standards. Your private user content is NOT used to train public foundation models without consent.

        5. CHILDREN'S PRIVACY (COPPA COMPLIANCE):
        This is an educational, safe AI assistant suitable for all age groups. We do not knowingly collect personal identifiable information from children under 13 years of age.

        6. CONTACT US:
        If you have questions, feedback, or requests regarding this Privacy Policy, please contact the developer directly at $PRIVACY_CONTACT_EMAIL.
        """.trimIndent()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.testTag("privacy_policy_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = securityBlue.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = "Privacy Shield",
                                tint = securityBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "গোপনীয়তা নীতি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Privacy Policy • Google Play Verified",
                            style = MaterialTheme.typography.labelSmall,
                            color = securityBlue,
                            fontWeight = FontWeight.SemiBold
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
                // Verified Security Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = emeraldGreen.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, emeraldGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Badge",
                            tint = emeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "100% নিৰাপদ • তথ্য বিক্ৰী বা অনৈতিক অনুসৰণ নিষিদ্ধ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = emeraldGreen
                        )
                    }
                }

                // Tab Row for easy reading
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
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

                // Scrollable Body Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> PrivacyOverviewTab()
                        1 -> PrivacyPermissionsTab()
                        2 -> PrivacyAiSafetyTab()
                        3 -> PrivacyLegalTextTab(fullPolicyText)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                // Bottom Quick Contact & Copy Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "মালিক & যোগাযোগ:",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = PRIVACY_CONTACT_EMAIL,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = securityBlue
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Global AI Privacy Policy", fullPolicyText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "✅ সম্পূৰ্ণ গোপনীয়তা নীতি কপি কৰা হ'ল!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Policy",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি কৰক", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = securityBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("মই বুজি পালোঁ (Accept & Close)", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun PrivacyOverviewTab() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PolicyFeatureCard(
            icon = Icons.Default.Lock,
            iconTint = Color(0xFF0284C7),
            title = "ব্যক্তিগত তথ্য সুৰক্ষিত (Zero Data Selling)",
            description = "আপোনাৰ কোনো ব্যক্তিগত তথ্য, চেট বা ফাইল কেতিয়াও কোনো বিজ্ঞাপন কোম্পানী বা তৃতীয় পক্ষক বিক্ৰী বা প্ৰদান কৰা নহয়। এয়া আমাৰ স্থায়ী প্ৰতিশ্ৰুতি।"
        )

        PolicyFeatureCard(
            icon = Icons.Default.Security,
            iconTint = Color(0xFF059669),
            title = "অন-ডিভাইচ মেমৰী (Local Safe Storage)",
            description = "আপোনাৰ সকলো কথোপকথন আৰু সৃষ্টি কৰা ছবি/বেনাৰ আপোনাৰ নিজৰ মোবাইল ফোনৰ এনক্ৰিপ্ট ডাটাবেছতহে সাঁচি ৰখা হয়। আপুনি বিচৰা সময়ত সকলো মচি পেলাব পাৰে।"
        )

        PolicyFeatureCard(
            icon = Icons.Default.VisibilityOff,
            iconTint = Color(0xFF7C3AED),
            title = "কোনো অপ্ৰয়োজনীয় অনুসৰণ নাই (No Ad Trackers)",
            description = "এই এপত কোনো ধৰণৰ গোপনে তথ্য সংগ্ৰহ কৰা বিজ্ঞাপন ট্ৰেকাৰ বা এনালাইটিক্স ব্যৱহাৰ কৰা হোৱা নাই। এপটো ১০০% শৈক্ষিক আৰু নিকা।"
        )

        PolicyFeatureCard(
            icon = Icons.Default.Policy,
            iconTint = Color(0xFFEA580C),
            title = "শিশু আৰু পৰিয়ালৰ বাবে সুৰক্ষিত (Family Safe)",
            description = "সকলো বয়সৰ শিক্ষার্থী আৰু সাধাৰণ মানুহৰ জ্ঞান বৃদ্ধিৰ বাবে এয়া এক সুৰক্ষিত শৈক্ষিক মঞ্চ। অনুপযুক্ত বা ক্ষতিকাৰক সমল প্ৰতিৰোধ কৰা হয়।"
        )
    }
}

@Composable
private fun PrivacyPermissionsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PolicyPermissionItem(
            title = "📷 কেমেৰা আৰু ফটো (Camera & Gallery)",
            subtitle = "কেৱল আপোনাৰ অনুৰোধতহে ব্যৱহাৰ হয়",
            body = "যেতিয়া আপুনি নিজে ফটো তুলি কিবা প্ৰশ্ন সমাধান কৰিব খোজে বা বেনাৰ বনাব খোজে, তেতিয়াহে কেমেৰা আৰু গেলেৰীৰ নিৰ্দিষ্ট ফটোখন ব্যৱহাৰ কৰা হয়। কোনো ফটো গোপনভাৱে সংগ্ৰহ কৰা নহয়।"
        )

        PolicyPermissionItem(
            title = "🎙️ মাইক্ৰ'ফোন আৰু মাত (Microphone & Audio)",
            subtitle = "পোনপটীয়া ভইচ চেটৰ বাবে",
            body = "কথা কৈ প্ৰশ্ন সুধিবলৈ বা কথাৰ পৰা লেখালৈ (Speech-to-Text) ৰূপান্তৰ কৰিবলৈহে মাইক্ৰ'ফোন ব্যৱহাৰ হয়। আপোনাৰ কথোপকথনৰ কোনো অডিঅ' ৰেকৰ্ডিং বাহিৰৰ চাৰ্ভাৰত স্থায়ীভাৱে সাঁচি থোৱা নহয়।"
        )

        PolicyPermissionItem(
            title = "📄 ডকুমেণ্ট আৰু ফাইল (Document & Storage)",
            subtitle = "PDF আৰু শিক্ষাৰ ফাইল বিশ্লেষণ",
            body = "যেতিয়া আপুনি ডকুমেণ্ট বিশ্লেষণ বা সাৰাংশ বিচাৰি ফাইল বাছি লয়, কেৱল সেই ফাইলটোহে পঢ়া হয়। ফোনৰ অন্য ব্যক্তিগত ফাইল বা ফোল্ডাৰ চোৱাৰ কোনো অনুমতি এপত নাই।"
        )

        PolicyPermissionItem(
            title = "🌐 ইণ্টাৰনেট সংযোগ (Internet Permission)",
            subtitle = "Google Gemini AI ইঞ্জিনৰ সৈতে যোগাযোগ",
            body = "AI উত্তৰসমূহ অনতিপলমে প্ৰস্তুত কৰাৰ বাবে সুৰক্ষিত HTTPS TLS 1.3 এনক্ৰিপ্ট প্ৰট'কলৰ জৰিয়তে যোগাযোগ সম্পন্ন হয়।"
        )
    }
}

@Composable
private fun PrivacyAiSafetyTab() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "🤖 Google Gemini AI & Model Safety",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• আপোনাৰ ব্যক্তিগত প্ৰমপ্ট বা ইনপুটসমূহ কোনো ৰাজহুৱা AI মডেলক প্ৰশিক্ষণ (Training) দিয়াৰ বাবে ব্যৱহাৰ কৰা নহয়।\n• সকলো যোগাযোগ এনক্ৰিপ্টেড আৰু সুৰক্ষিত।\n• সৃষ্টি কৰা ফটো আৰু বেনাৰসমূহ ব্যৱহাৰকাৰীৰ নিজা সৃষ্টি আৰু ব্যৱহাৰকাৰীৰ সম্পূৰ্ণ স্বত্বাধিকাৰ থাকে।",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "🛡️ আপোনাৰ নিয়ন্ত্ৰণ আৰু অধিকাৰ (Your Rights)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF059669)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. সকলো মচি পেলাব পাৰে: এপৰ 'Clear All Chat History' চুই তৎক্ষণাত সকলো ডিলিট কৰিব পাৰে।\n2. অনুমতি বন্ধ কৰিব পাৰে: ফোনৰ Settings ৰ পৰা যিকোনো সময়ত Camera, Mic অনুমতি বন্ধ কৰিব পাৰে।\n3. এক্সপোৰ্ট কৰিব পাৰে: নিজৰ কথোপকথন Text হিচাপে শ্বেয়াৰ বা সংৰক্ষণ কৰিব পাৰে।",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PrivacyLegalTextTab(fullText: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "Official Legal Policy Text (Google Play Developer Compliance):",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = fullText,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PolicyFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun PolicyPermissionItem(
    title: String,
    subtitle: String,
    body: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = body, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        }
    }
}
