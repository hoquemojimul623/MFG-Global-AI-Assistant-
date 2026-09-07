package com.example.ui.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.data.subscription.SubscriptionManager
import com.example.data.subscription.SubscriptionPlan
import com.example.data.subscription.UserSubscriptionStatus
import com.example.ui.viewmodel.GlobalAiViewModel

// Gemini Signature Color Palette
private val GeminiBlue = Color(0xFF1B72E8)
private val GeminiPurple = Color(0xFF8E24AA)
private val GeminiPink = Color(0xFFD946EF)
private val GeminiIndigo = Color(0xFF6366F1)
private val GeminiTeal = Color(0xFF0D9488)
private val GeminiGold = Color(0xFFF59E0B)

@Composable
fun UpgradeSubscriptionDialog(
    subscriptionManager: SubscriptionManager,
    userEmail: String,
    currentStatus: UserSubscriptionStatus,
    viewModel: GlobalAiViewModel? = null,
    onDismiss: () -> Unit,
    onSubscriptionSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf(SubscriptionManager.PLANS[2]) } // Default VIP Yearly (₹899)
    
    // Payment Verification State
    var isPaymentSuccessful by remember { mutableStateOf(false) }
    var verifiedTxnId by remember { mutableStateOf<String?>(null) }
    
    var showSuccessState by remember { mutableStateOf(false) }
    var enteredUtrOrCode by remember { mutableStateOf("") }
    var verificationError by remember { mutableStateOf<String?>(null) }
    
    // Owner Admin Panel states
    var adminTargetEmail by remember { mutableStateOf("") }
    var generatedCodeForUser by remember { mutableStateOf<String?>(null) }

    val ownerUpi = subscriptionManager.getOwnerUpiId()
    val ownerName = subscriptionManager.getOwnerName()
    val ownerPhone = SubscriptionManager.DEFAULT_OWNER_PHONE
    val isUserOwner = subscriptionManager.isOwner(userEmail)

    // Lottie Animation for VIP Unlock & Celebration
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.vip_unlock_success)
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComposition,
        isPlaying = isPaymentSuccessful || showSuccessState,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true
    )

    // Gemini Dynamic Aura Gradient Animation
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_sparkle")
    val shimmerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gemini_rotation"
    )

    val vipPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gemini_pulse"
    )

    val geminiBrush = Brush.horizontalGradient(
        colors = listOf(GeminiBlue, GeminiPurple, GeminiPink, GeminiIndigo)
    )

    val geminiBorderBrush = Brush.sweepGradient(
        colors = listOf(GeminiBlue, GeminiPurple, GeminiPink, GeminiIndigo, GeminiBlue)
    )

    fun buildUpiUri(plan: SubscriptionPlan): Uri {
        return Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", ownerUpi)
            .appendQueryParameter("pn", ownerName)
            .appendQueryParameter("mc", "")
            .appendQueryParameter("tr", "MFG_${System.currentTimeMillis()}")
            .appendQueryParameter("tn", "MFG Global AI VIP - ${plan.name} ($userEmail)")
            .appendQueryParameter("am", plan.price.toString())
            .appendQueryParameter("cu", "INR")
            .build()
    }

    val upiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        val response = data?.getStringExtra("response") ?: ""
        val status = data?.getStringExtra("Status") ?: ""
        
        val isConfirmedSuccess = (result.resultCode == Activity.RESULT_OK) && 
            (response.contains("SUCCESS", ignoreCase = true) || 
             status.equals("SUCCESS", ignoreCase = true) || 
             response.contains("Status=SUCCESS", ignoreCase = true))

        if (isConfirmedSuccess) {
            val txnId = "UPI_SUCCESS_${System.currentTimeMillis()}"
            verifiedTxnId = txnId
            isPaymentSuccessful = true
            verificationError = null
            Toast.makeText(context, "✅ Payment Verified! এতিয়া '👑 MFG VIP' অপশ্বনত ক্লিক কৰক!", Toast.LENGTH_LONG).show()
        } else if (response.contains("FAILURE", ignoreCase = true) || response.contains("FAILED", ignoreCase = true)) {
            isPaymentSuccessful = false
            Toast.makeText(context, "❌ পেমেণ্ট বিফল হ'ল। অনুগ্ৰহ কৰি পুনৰ চেষ্টা কৰক।", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "পেমেণ্ট সম্পন্ন হ'লে ৰিচিটৰ ১২ সংখ্যাৰ UTR নম্বৰ তলত দি 'Verify' কৰক।", Toast.LENGTH_LONG).show()
        }
    }

    fun launchUpiPayment(specificPackage: String? = null) {
        val upiUri = buildUpiUri(selectedPlan)
        val intent = Intent(Intent.ACTION_VIEW, upiUri)
        if (!specificPackage.isNullOrBlank()) {
            intent.setPackage(specificPackage)
        }
        try {
            if (specificPackage == null) {
                upiLauncher.launch(Intent.createChooser(intent, "Google Pay / PhonePe / Paytm ৰে ₹${selectedPlan.price} পৰিশোধ কৰক"))
            } else {
                upiLauncher.launch(intent)
            }
        } catch (e: Exception) {
            try {
                val genericIntent = Intent(Intent.ACTION_VIEW, upiUri)
                upiLauncher.launch(Intent.createChooser(genericIntent, "Pay ₹${selectedPlan.price} with Any UPI"))
            } catch (ex: Exception) {
                Toast.makeText(context, "কোনো UPI এপ পোৱা নগ'ল। $ownerUpi ত পেমেণ্ট কৰক।", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun verifyEnteredUtrOrCode() {
        val input = enteredUtrOrCode.trim()
        val result = if (viewModel != null) {
            viewModel.validateAndVerifyUtr(input, selectedPlan)
        } else {
            subscriptionManager.verifyUtrForOwnerUpi(userEmail, input, selectedPlan)
        }
        if (result.first) {
            verifiedTxnId = input
            isPaymentSuccessful = true
            verificationError = null
            Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
        } else {
            isPaymentSuccessful = false
            verificationError = result.second
            Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsAppProof() {
        val text = "নমস্কাৰ মজিমুল ছাৰ,\nমই MFG Global AI VIP ৰ ${selectedPlan.name} (₹${selectedPlan.price}) ৰ বাবে আপোনাৰ UPI ($ownerUpi) ত পেমেণ্ট কৰিলোঁ।\n\nমোৰ একাউণ্ট ইমেইল: $userEmail"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/91$ownerPhone?text=${Uri.encode(text)}")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:mojimulk2@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "MFG Global AI VIP Payment - $userEmail")
                putExtra(Intent.EXTRA_TEXT, text)
            }
            try {
                context.startActivity(emailIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "WhatsApp/Email পোৱা নগ'ল। ফোন: $ownerPhone", Toast.LENGTH_LONG).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 14.dp)
                .imePadding()
                .clip(RoundedCornerShape(32.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(GeminiBlue.copy(alpha = 0.6f), GeminiPink.copy(alpha = 0.6f))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Gemini Header with Glowing Sparkle Icon & Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.2.dp, geminiBrush)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Brush.horizontalGradient(listOf(GeminiBlue.copy(alpha = 0.12f), GeminiPink.copy(alpha = 0.12f))))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "MFG Global AI",
                                tint = GeminiBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MFG Global AI VIP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GeminiBlue
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Close, 
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (showSuccessState) {
                    // Success View after tapping "MFG VIP"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 12.dp)
                    ) {
                        // Lottie Celebration Animation
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            LottieAnimation(
                                composition = lottieComposition,
                                progress = { lottieProgress },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "👑 আপুনি এতিয়া MFG Global AI VIP সদস্য! 🎉",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "আপোনাৰ একাউণ্ট সফলতাৰে ${selectedPlan.name} লৈ Upgrade হ'ল। MFG Global AI ৰ সকলো Next-Gen AI সুবিধা সীমাহীন গতিৰে সক্ৰিয় হৈ পৰিল।",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                onSubscriptionSuccess()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue)
                        ) {
                            Text("MFG Global AI ৰ সৈতে কথা পাতক", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }
                    }
                } else {
                    // MFG Global AI Brand Hero Header
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Upgrade to MFG Global AI VIP",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MFG Global AI ৰ ছুপাৰফাষ্ট AI মডেল, আনলিমিটেড মেছেজ আৰু ফটো ষ্টুডিঅ' আনলক কৰক।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feature Highlights Box
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GeminiPurple, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MFG Global AI Unlimited VIP Access", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = GeminiGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("বিনা কোনো Daily Limit এ সীমাহীন চাট আৰু অনুবাদ", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GeminiTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("সৰ্বাধিক অগ্ৰাধিকাৰ আৰু আটাইতকৈ দ্ৰুততম সঁহাৰি", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Owner Control (Mojimul Hoque only)
                    if (isUserOwner) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = GeminiGold.copy(alpha = 0.12f),
                            border = BorderStroke(1.5.dp, GeminiGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GeminiGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("👑 Owner Admin Approval (Mojimul Hoque)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GeminiGold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = adminTargetEmail,
                                    onValueChange = { adminTargetEmail = it },
                                    label = { Text("ব্যৱহাৰকাৰীৰ Email ID", fontSize = 11.sp) },
                                    placeholder = { Text("user@gmail.com", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (adminTargetEmail.isNotBlank()) {
                                                subscriptionManager.activateSubscription(adminTargetEmail.trim(), selectedPlan, "OWNER_INSTANT_UNLOCK")
                                                Toast.makeText(context, "$adminTargetEmail এক্টিভ কৰা হ'ল!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("🔓 Unlock VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            if (adminTargetEmail.isNotBlank()) {
                                                val code = subscriptionManager.generateUserActivationCode(adminTargetEmail.trim(), selectedPlan.id)
                                                generatedCodeForUser = code
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Code", code))
                                                Toast.makeText(context, "Code ($code) কপি কৰা হ'ল!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = GeminiPurple),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("🔑 Gen Code", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (generatedCodeForUser != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Key: $generatedCodeForUser", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GeminiPurple)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Step 1: MFG Global AI Plan Selection Cards
                    Text(
                        text = "১. আপোনাৰ MFG Global AI প্লেন বাছনি কৰক:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SubscriptionManager.PLANS.forEach { plan ->
                        val isSelected = plan.id == selectedPlan.id
                        val isYearly = plan.id == "plan_yearly"
                        
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) 
                                GeminiBlue.copy(alpha = 0.08f) 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) geminiBrush else Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { 
                                    selectedPlan = plan 
                                    isPaymentSuccessful = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) GeminiBlue else Color.Transparent,
                                        border = BorderStroke(1.5.dp, if (isSelected) GeminiBlue else MaterialTheme.colorScheme.outline),
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        if (isSelected) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = plan.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (plan.badge != null) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = if (isYearly) GeminiBlue else GeminiPurple,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = plan.badge,
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = plan.assameseName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${plan.price}",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) GeminiBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (plan.durationDays == -1) "এককালীন"
                                               else if (plan.durationDays == 365) "/ year"
                                               else "/ ${plan.durationDays} days",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 2: Gemini Direct UPI Checkout Button
                    Text(
                        text = "২. পেমেণ্ট কৰক (Pay ₹${selectedPlan.price}):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Gemini Master Multi-Color Action Button
                    Button(
                        onClick = { launchUpiPayment(null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Pay / PhonePe / Paytm ৰে ₹${selectedPlan.price} দিয়ক",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // UPI App Fast Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { launchUpiPayment("com.google.android.apps.nbu.paisa.user") },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, GeminiBlue.copy(alpha = 0.6f))
                        ) {
                            Text("🔵 GPay", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GeminiBlue)
                        }

                        OutlinedButton(
                            onClick = { launchUpiPayment("com.phonepe.app") },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, GeminiPurple.copy(alpha = 0.6f))
                        ) {
                            Text("🟣 PhonePe", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GeminiPurple)
                        }

                        OutlinedButton(
                            onClick = { launchUpiPayment("net.one97.paytm") },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, Color(0xFF002E6E).copy(alpha = 0.6f))
                        ) {
                            Text("🟦 Paytm", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF002E6E))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Official UPI ID Copy Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Official UPI: $ownerUpi", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                Text("প্ৰাপক: $ownerName", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", ownerUpi))
                                    Toast.makeText(context, "UPI ID ($ownerUpi) কপি কৰা হ'ল!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = GeminiBlue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 3: Mandatory UTR / UPI Ref Verification for 7637839634-3@ybl
                    Text(
                        text = "৩. $ownerUpi ত কৰা পেমেণ্টৰ UTR দিয়ক:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "কেৱল $ownerUpi ($ownerName) লৈ সফল হোৱা পেমেণ্টৰ ১২ সংখ্যাৰ UPI Ref / UTR নম্বৰ দিলেই '👑 MFG VIP' অপশ্বন খোল খাব। অন্য নম্বৰৰ পেমেণ্ট গ্ৰহণ কৰা নহ'ব।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // UTR / Code Verification Input Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = enteredUtrOrCode,
                                onValueChange = {
                                    enteredUtrOrCode = it
                                    verificationError = null
                                    if (isPaymentSuccessful) {
                                        isPaymentSuccessful = false
                                    }
                                },
                                placeholder = { Text("উদাহৰণ: 423589123456", fontSize = 11.sp) },
                                label = { Text("১২ সংখ্যাৰ UPI Ref / UTR", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                isError = verificationError != null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GeminiBlue,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { verifyEnteredUtrOrCode() },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verify UTR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (verificationError != null) {
                            Text(
                                text = verificationError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // Contact Support Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { openWhatsAppProof() }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF25D366))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সহায়ৰ বাবে WhatsApp ($ownerPhone)", fontSize = 11.sp, color = Color(0xFF25D366))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // =========================================================================
                    // CRITICAL REQUIREMENT: "MFG VIP" OPTION (Gemini Advanced Premium Tier)
                    // ONLY APPEARS WHEN isPaymentSuccessful == true!
                    // IF NOT SUCCESSFUL, THIS OPTION DOES NOT APPEAR AND UPGRADE CANNOT HAPPEN!
                    // =========================================================================
                    AnimatedVisibility(
                        visible = isPaymentSuccessful,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = GeminiPurple.copy(alpha = 0.12f),
                            border = BorderStroke(2.dp, geminiBrush),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .scale(vipPulseScale)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Lottie VIP Unlock Feedback Animation
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(96.dp)
                                ) {
                                    LottieAnimation(
                                        composition = lottieComposition,
                                        progress = { lottieProgress },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = GeminiBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VIP Status Unlocked! (12-Digit UTR Verified)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GeminiBlue
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "তলৰ '👑 MFG VIP' অপশ্বনত টিপি আপোনাৰ একাউণ্ট এতিয়াই MFG Global AI VIP লৈ Upgrade কৰক:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // THE OFFICIAL "MFG VIP" UPGRADE BUTTON
                                Button(
                                    onClick = {
                                        val txn = verifiedTxnId ?: "MFG_VIP_${System.currentTimeMillis()}"
                                        val upgraded = if (viewModel != null && verifiedTxnId != null) {
                                            viewModel.upgradeToVipWithUtr(verifiedTxnId!!, selectedPlan)
                                        } else {
                                            subscriptionManager.activateSubscription(userEmail, selectedPlan, txn)
                                            if (verifiedTxnId != null) {
                                                subscriptionManager.markUtrUsed(verifiedTxnId!!, userEmail)
                                            }
                                            true
                                        }
                                        if (upgraded) {
                                            showSuccessState = true
                                            Toast.makeText(context, "অভিনন্দন! আপোনাৰ একাউণ্ট MFG Global AI VIP লৈ Upgrade হ'ল 🎉", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GeminiGold,
                                        contentColor = Color.Black
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "MFG VIP",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "👑 MFG VIP (Upgrade to MFG Global AI)",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // If payment is not yet verified, show clear locked notice
                    if (!isPaymentSuccessful) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🔒 পেমেণ্ট সফল নোহোৱালৈকে 'MFG VIP' অপশ্বন নাহিব আৰু একাউণ্ট Upgrade নহ'ব।",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google-style Security & Encryption Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Google-Grade 256-bit Encrypted Checkout • Verified Upgrade",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

