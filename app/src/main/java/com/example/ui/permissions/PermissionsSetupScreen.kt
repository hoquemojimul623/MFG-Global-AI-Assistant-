package com.example.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.security.PermissionsManager

@Composable
fun PermissionsSetupScreen(
    permissionsManager: PermissionsManager,
    onPermissionsComplete: () -> Unit
) {
    val context = LocalContext.current

    var isMicGranted by remember { mutableStateOf(permissionsManager.isRecordAudioGranted()) }
    var isCameraGranted by remember { mutableStateOf(permissionsManager.isCameraGranted()) }
    var isNotificationGranted by remember { mutableStateOf(permissionsManager.isNotificationGranted()) }
    var isBatteryOptimized by remember { mutableStateOf(permissionsManager.isBatteryOptimizationIgnored()) }

    // Update states on resume or changes
    fun refreshStates() {
        isMicGranted = permissionsManager.isRecordAudioGranted()
        isCameraGranted = permissionsManager.isCameraGranted()
        isNotificationGranted = permissionsManager.isNotificationGranted()
        isBatteryOptimized = permissionsManager.isBatteryOptimizationIgnored()
    }

    // Multiple permissions launcher
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        isMicGranted = results[Manifest.permission.RECORD_AUDIO] ?: permissionsManager.isRecordAudioGranted()
        isCameraGranted = results[Manifest.permission.CAMERA] ?: permissionsManager.isCameraGranted()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isNotificationGranted = results[Manifest.permission.POST_NOTIFICATIONS] ?: permissionsManager.isNotificationGranted()
        }
        refreshStates()
    }

    // Single permission launchers
    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isMicGranted = granted
        refreshStates()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isCameraGranted = granted
        refreshStates()
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isNotificationGranted = granted
        refreshStates()
    }

    val batteryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        refreshStates()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B132B),
                        Color(0xFF1C2541),
                        Color(0xFF0B132B)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("permissions_setup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF0284C7).copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Shield",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "প্ৰয়োজনীয় অনুমতিসমূহ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "App Setup & Permission Verification",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B).copy(alpha = 0.85f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "MFG AI Assistant ৰ Voice Q&A, ফটো বিশ্লেষণ আৰু তীব্ৰ গতি নিশ্চিত কৰিবলৈ তলৰ সুবিধাসমূহ সক্ৰিয় কৰক।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permission Items List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Voice & Microphone Permission
                item {
                    PermissionCard(
                        icon = Icons.Default.Mic,
                        iconColor = Color(0xFF06B6D4),
                        title = "ভইচ প্ৰশ্ন আৰু মাইক্ৰ'ফোন (Voice Q&A)",
                        description = "মুখৰে কথা কৈ প্ৰশ্ন সুধিবলৈ আৰু তাৎক্ষণিক ভইচ উত্তৰ লাভ কৰিবলৈ।",
                        isGranted = isMicGranted,
                        onGrantClick = {
                            micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    )
                }

                // 2. Camera Permission
                item {
                    PermissionCard(
                        icon = Icons.Default.CameraAlt,
                        iconColor = Color(0xFFA855F7),
                        title = "কেমেৰা আৰু ফটো বিশ্লেষণ (Camera & Vision AI)",
                        description = "অঙ্ক, নথি আৰু বস্তুৰ ফটো তুলি AI ক বিশ্লেষণ কৰিবলৈ।",
                        isGranted = isCameraGranted,
                        onGrantClick = {
                            cameraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }

                // 3. Battery Optimization Exemption
                item {
                    PermissionCard(
                        icon = Icons.Default.BatteryChargingFull,
                        iconColor = Color(0xFF10B981),
                        title = "তীব্ৰ গতি আৰু বেটাৰী অনুকূলকৰণ (Fast Response)",
                        description = "বেক্টগ্ৰাউণ্ডত AI প্ৰচেছিং বিলম্ব নোহোৱাকৈ ৰাখিবলৈ বেটাৰী ৰেষ্ট্ৰিকশ্বন আতঁৰাওক।",
                        isGranted = isBatteryOptimized,
                        onGrantClick = {
                            try {
                                val intent = permissionsManager.createBatteryOptimizationIntent()
                                batteryLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Settings খোলক", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                // 4. Notifications Permission (Android 13+)
                item {
                    PermissionCard(
                        icon = Icons.Default.NotificationsActive,
                        iconColor = Color(0xFFF59E0B),
                        title = "জাননী আৰু AI এলৰ্ট (AI Notifications)",
                        description = "দীঘলীয়া প্ৰশ্নৰ উত্তৰ সম্পূৰ্ণ হোৱাৰ জাননী আৰু নিৰাপত্তা সতৰ্কবাৰ্তা।",
                        isGranted = isNotificationGranted,
                        onGrantClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                isNotificationGranted = true
                            }
                        }
                    )
                }

                // 5. Anti-Hack Screen & Privacy Status
                item {
                    PermissionCard(
                        icon = Icons.Default.Shield,
                        iconColor = Color(0xFF38BDF8),
                        title = "🔒 নিৰাপদ স্ক্ৰীণ আৰু এণ্টি-হেকিং (Privacy Guard)",
                        description = "SHA-256 এনক্ৰিপশ্বন, স্ক্ৰীণ সুৰক্ষা আৰু সক্ৰিয় প্ৰাইভেচি শ্বিল্ড।",
                        isGranted = true,
                        isAutoSecured = true,
                        onGrantClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Grant All Button (if any permission is missing)
                if (!isMicGranted || !isCameraGranted || !isNotificationGranted) {
                    Button(
                        onClick = {
                            val permsToRequest = mutableListOf<String>()
                            if (!isMicGranted) permsToRequest.add(Manifest.permission.RECORD_AUDIO)
                            if (!isCameraGranted) permsToRequest.add(Manifest.permission.CAMERA)
                            if (!isNotificationGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            if (permsToRequest.isNotEmpty()) {
                                multiplePermissionsLauncher.launch(permsToRequest.toTypedArray())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("grant_all_permissions_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সকলো অনুমতি একেলগে দিয়ক (Grant All)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Continue Button
                Button(
                    onClick = {
                        permissionsManager.setPermissionsOnboardingCompleted(true)
                        onPermissionsComplete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMicGranted && isCameraGranted) Color(0xFF10B981) else Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("continue_to_app_button")
                ) {
                    Text(
                        text = "এপ আৰম্ভ কৰক (Continue to App)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    isGranted: Boolean,
    isAutoSecured: Boolean = false,
    onGrantClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827).copy(alpha = 0.9f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isGranted) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action / Status Badge
            if (isGranted) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAutoSecured) "Active" else "সক্ৰিয়",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onGrantClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF38BDF8)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7)),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "অনুমতি দিয়ক",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
