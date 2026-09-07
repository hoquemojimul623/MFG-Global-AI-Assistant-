package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.splash.SplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.auth.AuthManager
import com.example.data.model.AiMode
import com.example.data.model.SupportedLanguage
import com.example.data.security.PermissionsManager
import com.example.ui.auth.AuthScreen
import com.example.ui.components.ChatBubble
import com.example.ui.components.ChatDrawerContent
import com.example.ui.components.LanguageDropdownSelector
import com.example.ui.components.LanguagePillSelectorRow
import com.example.ui.components.ModeSelectorBar
import com.example.ui.components.MusicStudioDialog
import com.example.ui.components.PhotoEditorDialog
import com.example.ui.components.PromptSuggestions
import com.example.ui.components.SecurityWarningBanner
import com.example.ui.components.ShareConversationDialog
import com.example.ui.components.TypingIndicator
import com.example.ui.components.UpgradeSubscriptionDialog
import com.example.ui.components.VideoStudioDialog
import com.example.ui.components.VoiceAssistantDialog
import com.example.ui.permissions.PermissionsSetupScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GlobalAiViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: GlobalAiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val authManager = remember { AuthManager(context) }
                val permissionsManager = remember { PermissionsManager(context) }

                var showSplash by remember { mutableStateOf(true) }
                var showPermissions by remember {
                    mutableStateOf(!permissionsManager.isPermissionsOnboardingCompleted())
                }
                var isLoggedIn by remember { mutableStateOf(authManager.isLoggedIn()) }

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        val email = authManager.getUserProfile()?.email ?: ""
                        viewModel.setUserEmail(email)
                    } else {
                        viewModel.setUserEmail("")
                    }
                }

                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )
                } else if (showPermissions) {
                    PermissionsSetupScreen(
                        permissionsManager = permissionsManager,
                        onPermissionsComplete = {
                            showPermissions = false
                        }
                    )
                } else if (!isLoggedIn) {
                    AuthScreen(
                        authManager = authManager,
                        onAuthSuccess = {
                            val email = authManager.getUserProfile()?.email ?: ""
                            viewModel.setUserEmail(email)
                            isLoggedIn = true
                        }
                    )
                } else {
                    GlobalAiApp(
                        viewModel = viewModel,
                        authManager = authManager,
                        permissionsManager = permissionsManager,
                        onOpenPermissions = {
                            showPermissions = true
                        },
                        onLogout = {
                            authManager.logout()
                            viewModel.setUserEmail("")
                            isLoggedIn = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalAiApp(
    viewModel: GlobalAiViewModel,
    authManager: AuthManager,
    permissionsManager: PermissionsManager,
    onOpenPermissions: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val attachedMedia by viewModel.attachedMedia.collectAsStateWithLifecycle()
    val autoSpeakEnabled by viewModel.autoSpeakEnabled.collectAsStateWithLifecycle()
    val turboSpeedMode by viewModel.turboSpeedMode.collectAsStateWithLifecycle()
    val selectedAiMode by viewModel.selectedAiMode.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val speakingMessageId by viewModel.ttsManager.speakingMessageId.collectAsStateWithLifecycle()
    val subscriptionStatus by viewModel.subscriptionStatus.collectAsStateWithLifecycle()
    val showUpgradeDialog by viewModel.showUpgradeDialog.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    // Modals & Bottom Sheets
    var showBannerStudio by remember { mutableStateOf(false) }
    var showPhotoStudio by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showVideoStudio by remember { mutableStateOf(false) }
    var showMusicStudio by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var editorInitialUri by remember { mutableStateOf<Uri?>(null) }

    // Scroll to bottom when new messages arrive or when typing indicator appears
    LaunchedEffect(messages.size, isGenerating) {
        val totalCount = messages.size + if (isGenerating) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    // Pickers
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.attachImage(uri)
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.attachAudio(uri)
            viewModel.sendMessage("Transcribe this audio recording accurately and format speech into text (gemini-3.5-transcribe):")
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.attachDocument(uri)
        }
    }

    val cameraPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            viewModel.attachBitmap(bmp, "camera_photo.jpg")
        }
    }

    // Photo Studio Dialog
    if (showPhotoStudio) {
        PhotoEditorDialog(
            initialUri = editorInitialUri,
            onDismiss = {
                showPhotoStudio = false
                editorInitialUri = null
            },
            onAttachToChat = { editedBmp, name ->
                viewModel.attachBitmap(editedBmp, name)
            },
            onAskAiWithImage = { editedBmp, prompt ->
                viewModel.attachBitmap(editedBmp, "edited_photo.jpg")
                viewModel.sendMessage(prompt)
            },
            onAnimateToVideo = { animatedBmp, motionPrompt ->
                viewModel.attachBitmap(animatedBmp, "input_image_to_animate.jpg")
                viewModel.sendMessage("Animate this image into video using model [veo-3.1-fast-generate-preview]: $motionPrompt")
            }
        )
    }

    // Live Voice Assistant Dialog
    if (showVoiceDialog) {
        VoiceAssistantDialog(
            isGenerating = isGenerating,
            lastAiResponse = messages.lastOrNull { it.role == "model" }?.content,
            isSpeaking = speakingMessageId != null,
            onDismiss = { showVoiceDialog = false },
            onVoiceQuery = { query ->
                viewModel.sendMessage(query)
            },
            onStopSpeaking = { viewModel.ttsManager.stop() },
            onTranscribeAudio = {
                audioPickerLauncher.launch("audio/*")
            }
        )
    }

    // Video Script & Outline Studio Dialog
    if (showVideoStudio) {
        VideoStudioDialog(
            onDismiss = { showVideoStudio = false },
            onGenerateVideoScript = { scriptPrompt ->
                viewModel.sendMessage(scriptPrompt)
            }
        )
    }

    // Music Studio Dialog (lyria-3-clip-preview & lyria-3-pro-preview)
    if (showMusicStudio) {
        MusicStudioDialog(
            onDismiss = { showMusicStudio = false },
            onGenerateMusic = { musicPrompt ->
                viewModel.sendMessage(musicPrompt)
            }
        )
    }

    // Photo, Banner & Poster Studio Dialog (ChatGPT AI Image Maker)
    if (showBannerStudio) {
        com.example.ui.components.BannerStudioDialog(
            onDismiss = { showBannerStudio = false },
            onGenerate = { prompt, aspect, style ->
                viewModel.generateBannerOrPhoto(prompt, aspect, style)
            }
        )
    }

    // Upgrade & Unlimited Subscription Dialog
    if (showUpgradeDialog) {
        val userEmail = authManager.getUserProfile()?.email ?: ""
        UpgradeSubscriptionDialog(
            subscriptionManager = viewModel.subscriptionManager,
            userEmail = userEmail,
            currentStatus = subscriptionStatus,
            viewModel = viewModel,
            onDismiss = { viewModel.dismissUpgradeDialog() },
            onSubscriptionSuccess = {
                viewModel.refreshSubscriptionStatus()
            }
        )
    }

    // Share & Export Conversation Dialog
    if (showShareDialog) {
        ShareConversationDialog(
            messages = messages,
            onDismiss = { showShareDialog = false }
        )
    }

    // Attachment Options Bottom Sheet
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Upload & Creative Studio 📁",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // 1. Camera
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            showAttachmentSheet = false
                            cameraPhotoLauncher.launch(null)
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Camera", style = MaterialTheme.typography.labelSmall)
                    }

                    // 2. Gallery
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            showAttachmentSheet = false
                            galleryPickerLauncher.launch("image/*")
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Gallery",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Gallery", style = MaterialTheme.typography.labelSmall)
                    }

                    // 3. Document
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            showAttachmentSheet = false
                            documentPickerLauncher.launch("*/*")
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Document",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Document", style = MaterialTheme.typography.labelSmall)
                    }

                    // 4. Photo Studio AI
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            showAttachmentSheet = false
                            showPhotoStudio = true
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ColorLens,
                                    contentDescription = "Photo Studio AI",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Photo Studio AI", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ChatDrawerContent(
                    sessions = sessions,
                    currentSessionId = currentSessionId,
                    searchQuery = searchQuery,
                    userProfile = authManager.getUserProfile(),
                    subscriptionStatus = subscriptionStatus,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onSessionSelected = { id ->
                        viewModel.selectSession(id)
                        scope.launch { drawerState.close() }
                    },
                    onNewChatClick = {
                        viewModel.startNewChatSession()
                        scope.launch { drawerState.close() }
                    },
                    onDeleteSession = viewModel::deleteSession,
                    onClearAllHistory = viewModel::clearAllHistory,
                    onUpgradeClick = {
                        scope.launch { drawerState.close() }
                        viewModel.openUpgradeDialog()
                    },
                    onOpenPermissions = {
                        scope.launch { drawerState.close() }
                        onOpenPermissions()
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.testTag("app_title_row")
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.mfg_education_logo_1786634760898),
                                    contentDescription = "MFG Education Logo",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "MFG Global AI",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        maxLines = 1,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "বিশ্বজনীন AI সহায়ক",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Chat History Menu"
                                )
                            }
                        },
                        actions = {
                            // Upgrade / Unlimited VIP button
                            IconButton(
                                onClick = { viewModel.openUpgradeDialog() },
                                modifier = Modifier.testTag("upgrade_vip_top_button")
                            ) {
                                Icon(
                                    imageVector = if (subscriptionStatus.isUnlimited) Icons.Default.AutoAwesome else Icons.Default.FlashOn,
                                    contentDescription = "Upgrade Plan",
                                    tint = if (subscriptionStatus.isUnlimited) Color(0xFF10B981) else Color(0xFFEA580C)
                                )
                            }

                            // Share / Export Conversation Button
                            IconButton(
                                onClick = { showShareDialog = true },
                                modifier = Modifier.testTag("share_conversation_top_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share or Export Conversation",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // New Chat
                            IconButton(
                                onClick = { viewModel.startNewChatSession() },
                                modifier = Modifier.testTag("new_chat_top_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Chat Session",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Models Bar: Displayed clearly right below MFG Global AI
                    ModeSelectorBar(
                        selectedMode = selectedAiMode,
                        onModeSelected = viewModel::setAiMode
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                        thickness = 0.5.dp
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Feature Quick Launch Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { scope.launch { drawerState.open() } },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Chat History",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                label = { Text("💬 History (${sessions.size})") },
                                colors = FilterChipDefaults.filterChipColors(),
                                modifier = Modifier.testTag("chat_history_chip")
                            )
                        }
                        item {
                            LanguageDropdownSelector(
                                selectedLanguage = selectedLanguage,
                                onLanguageSelected = viewModel::setLanguage
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { showShareDialog = true },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share or Export Conversation",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                label = { Text("📤 Share / Export") },
                                colors = FilterChipDefaults.filterChipColors(),
                                modifier = Modifier.testTag("share_conversation_chip")
                            )
                        }
                        item {
                            FilterChip(
                                selected = turboSpeedMode,
                                onClick = { viewModel.toggleTurboSpeed() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = "Turbo Speed",
                                        modifier = Modifier.size(16.dp),
                                        tint = if (turboSpeedMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(if (turboSpeedMode) "⚡ বিদ্যুৎ গতি (Turbo)" else "🧠 Pro Mode")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { showVoiceDialog = true },
                                label = { Text("🎙️ Live Voice Q&A") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                        item {
                            FilterChip(
                                selected = true,
                                onClick = { showBannerStudio = true },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Photo & Banner Studio",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                },
                                label = { Text("🖼️ ফটো/বেনাৰ বনাওক (AI Banner)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0284C7),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("open_banner_studio_chip")
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { showPhotoStudio = true },
                                label = { Text("🎨 Photo Studio") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { showVideoStudio = true },
                                label = { Text("🎬 Video Studio") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { showMusicStudio = true },
                                label = { Text("🎵 Music Studio") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { audioPickerLauncher.launch("audio/*") },
                                label = { Text("🎧 Transcribe Audio") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { showAttachmentSheet = true },
                                label = { Text("📄 Upload File") },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                    }

                    // Attached Media Banner if present
                    if (attachedMedia != null) {
                        val media = attachedMedia!!
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (media.type) {
                                        "image" -> Icons.Default.Image
                                        "video" -> Icons.Default.Movie
                                        "audio" -> Icons.Default.Audiotrack
                                        else -> Icons.Default.Description
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = media.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Ready to send with your question",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.clearAttachment() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove attachment",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Main Input Controls Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Attachment Button
                        IconButton(
                            onClick = { showAttachmentSheet = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attach photo or file",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = viewModel::onInputTextChanged,
                            placeholder = {
                                val langHint = when (selectedLanguage) {
                                    SupportedLanguage.AUTO -> "Ask anything (অসমীয়া, English)..."
                                    SupportedLanguage.ASSAMESE -> "অসমীয়াত প্ৰশ্ন সোধক..."
                                    SupportedLanguage.ENGLISH -> "Ask anything in English..."
                                    SupportedLanguage.BENGALI -> "বাংলায় প্রশ্ন জিজ্ঞাসা করুন..."
                                    SupportedLanguage.HINDI -> "हिन्दी में पूछें..."
                                    SupportedLanguage.BODO -> "बड़ो राव आव सों..."
                                }
                                Text(
                                    text = if (attachedMedia != null) "Ask about attached file..." else langHint,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_input_field"),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Mic button for Live Voice Assistant
                        IconButton(
                            onClick = { showVoiceDialog = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Assistant",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        FloatingActionButton(
                            onClick = { viewModel.sendMessage() },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("send_button"),
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                SecurityWarningBanner()

                if (messages.isEmpty() && !isGenerating) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.mfg_education_logo_1786634760898),
                                contentDescription = "MFG Education Logo",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "বিশ্বজনীন AI সহায়ক\nMFG Global AI Assistant",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Photo & File Analysis • Live Voice Q&A • Video & Photo Editing • Assamese & Multi-language",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            PromptSuggestions(
                                onPromptSelected = { prompt ->
                                    viewModel.sendMessage(prompt)
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatBubble(
                                message = msg,
                                isSpeaking = speakingMessageId == msg.id,
                                onSpeakClick = { viewModel.speakMessage(msg.id, msg.content) },
                                onRetryClick = { viewModel.retryMessage(msg.content) }
                            )
                        }

                        if (isGenerating) {
                            item {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
