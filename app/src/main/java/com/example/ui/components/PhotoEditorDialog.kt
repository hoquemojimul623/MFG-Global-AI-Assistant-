package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class FilterPreset(val title: String) {
    ORIGINAL("Normal"),
    SEPIA("Sepia"),
    BW("B&W"),
    CYBERPUNK("Neon Cyber"),
    VINTAGE("Vintage"),
    COOL_BLUE("Cool Blue"),
    WARM_GOLD("Warm Gold"),
    NEGATIVE("Invert")
}

data class DrawLine(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditorDialog(
    initialUri: Uri? = null,
    onDismiss: () -> Unit,
    onAttachToChat: (Bitmap, String) -> Unit,
    onAskAiWithImage: (Bitmap, String) -> Unit,
    onAnimateToVideo: ((Bitmap, String) -> Unit)? = null
) {
    val context = LocalContext.current

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Filters, 1: Adjust, 2: Draw, 3: Text

    // Adjustments
    var brightness by remember { mutableFloatStateOf(0f) } // -100 to +100
    var contrast by remember { mutableFloatStateOf(1f) }   // 0.5 to 2.0
    var saturation by remember { mutableFloatStateOf(1f) } // 0 to 2.0
    var selectedFilter by remember { mutableStateOf(FilterPreset.ORIGINAL) }

    // Drawing state
    val drawLines = remember { mutableStateListOf<DrawLine>() }
    var selectedBrushColor by remember { mutableStateOf(Color.Red) }
    var brushStrokeWidth by remember { mutableFloatStateOf(10f) }

    // Text Overlay state
    var overlayText by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color.Yellow) }
    var textPosition by remember { mutableStateOf(Offset(100f, 100f)) }

    // Prompt for AI analysis
    var aiPromptText by remember { mutableStateOf("Explain this image in detail and highlight key insights.") }

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                    drawLines.clear()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            selectedBitmap = bmp
            drawLines.clear()
        }
    }

    // Load initialUri if present
    LaunchedEffect(initialUri) {
        if (initialUri != null && selectedBitmap == null) {
            try {
                val inputStream = context.contentResolver.openInputStream(initialUri)
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bmp != null) {
                    selectedBitmap = bmp
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Helper to generate the final edited Bitmap
    fun generateEditedBitmap(): Bitmap? {
        val base = selectedBitmap ?: return null
        val mutableBmp = Bitmap.createBitmap(base.width, base.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBmp)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val cm = ColorMatrix()

        // 1. Saturation
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(saturation)
        cm.postConcat(satMatrix)

        // 2. Preset filters
        val filterMatrix = ColorMatrix()
        when (selectedFilter) {
            FilterPreset.SEPIA -> {
                filterMatrix.set(
                    floatArrayOf(
                        0.393f, 0.769f, 0.189f, 0f, 0f,
                        0.349f, 0.686f, 0.168f, 0f, 0f,
                        0.272f, 0.534f, 0.131f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            FilterPreset.BW -> {
                filterMatrix.setSaturation(0f)
            }
            FilterPreset.CYBERPUNK -> {
                filterMatrix.set(
                    floatArrayOf(
                        1.4f, 0f, 0.4f, 0f, 20f,
                        0f, 1.2f, 0.8f, 0f, -10f,
                        0.5f, 0f, 1.5f, 0f, 40f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            FilterPreset.VINTAGE -> {
                filterMatrix.set(
                    floatArrayOf(
                        0.9f, 0f, 0f, 0f, 30f,
                        0f, 0.8f, 0f, 0f, 15f,
                        0f, 0f, 0.6f, 0f, -20f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            FilterPreset.COOL_BLUE -> {
                filterMatrix.set(
                    floatArrayOf(
                        0.8f, 0f, 0f, 0f, -10f,
                        0f, 0.9f, 0f, 0f, 0f,
                        0f, 0f, 1.3f, 0f, 30f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            FilterPreset.WARM_GOLD -> {
                filterMatrix.set(
                    floatArrayOf(
                        1.2f, 0f, 0f, 0f, 30f,
                        0f, 1.1f, 0f, 0f, 20f,
                        0f, 0f, 0.8f, 0f, -20f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            FilterPreset.NEGATIVE -> {
                filterMatrix.set(
                    floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            FilterPreset.ORIGINAL -> {}
        }
        cm.postConcat(filterMatrix)

        // 3. Brightness & Contrast
        val scale = contrast
        val translate = brightness + (1f - scale) * 128f
        val bcMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        cm.postConcat(bcMatrix)

        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(base, 0f, 0f, paint)

        // Draw Annotations
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        for (line in drawLines) {
            linePaint.color = line.color.toArgb()
            linePaint.strokeWidth = line.strokeWidth
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, linePaint)
        }

        // Draw Text Overlay
        if (overlayText.isNotBlank()) {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = textColor.toArgb()
                textSize = 48f
                isFakeBoldText = true
                setShadowLayer(6f, 2f, 2f, android.graphics.Color.BLACK)
            }
            canvas.drawText(overlayText, textPosition.x, textPosition.y, textPaint)
        }

        return mutableBmp
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
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Photo & Media Studio 🎨",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Filters • Draw • Annotate • AI Vision",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close Photo Studio"
                            )
                        }
                    },
                    actions = {
                        if (selectedBitmap != null) {
                            IconButton(
                                onClick = {
                                    val finalBmp = generateEditedBitmap()
                                    if (finalBmp != null) {
                                        onAttachToChat(finalBmp, "edited_photo.jpg")
                                        onDismiss()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Apply & Attach",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Main Content
                if (selectedBitmap == null) {
                    // Empty State: Prompt to select or capture photo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(28.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "AI Photo & Media Editor",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "ফটো আপলোড কৰক, ফিল্টাৰ লগাওক, আকি দিয়ক আৰু AI ক প্ৰশ্ন সোধক!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { galleryLauncher.launch("image/*") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gallery")
                                    }

                                    FilledTonalButton(
                                        onClick = { cameraLauncher.launch(null) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Camera")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Photo Studio Canvas and Tool Panels
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Drawing / Photo Canvas Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .background(Color.Black)
                                .clip(RoundedCornerShape(0.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            var lastTouchPoint by remember { mutableStateOf<Offset?>(null) }

                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(activeTab) {
                                        if (activeTab == 2) { // Freehand Drawing mode
                                            detectDragGestures(
                                                onDragStart = { offset ->
                                                    lastTouchPoint = offset
                                                },
                                                onDrag = { change, _ ->
                                                    change.consume()
                                                    val current = change.position
                                                    val prev = lastTouchPoint ?: current
                                                    drawLines.add(
                                                        DrawLine(
                                                            start = prev,
                                                            end = current,
                                                            color = selectedBrushColor,
                                                            strokeWidth = brushStrokeWidth
                                                        )
                                                    )
                                                    lastTouchPoint = current
                                                },
                                                onDragEnd = {
                                                    lastTouchPoint = null
                                                }
                                            )
                                        } else if (activeTab == 3) { // Drag text position
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                textPosition += dragAmount
                                            }
                                        }
                                    }
                            ) {
                                val edited = generateEditedBitmap()
                                if (edited != null) {
                                    val imageBmp = edited.asImageBitmap()
                                    drawImage(
                                        image = imageBmp,
                                        dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt())
                                    )
                                }
                            }
                        }

                        // Editor Mode Tabs
                        TabRow(
                            selectedTabIndex = activeTab,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Tab(
                                selected = activeTab == 0,
                                onClick = { activeTab = 0 },
                                text = { Text("Filters", fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                            Tab(
                                selected = activeTab == 1,
                                onClick = { activeTab = 1 },
                                text = { Text("Tune", fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                            Tab(
                                selected = activeTab == 2,
                                onClick = { activeTab = 2 },
                                text = { Text("Draw", fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                            Tab(
                                selected = activeTab == 3,
                                onClick = { activeTab = 3 },
                                text = { Text("Text/AI", fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                        }

                        // Tab Content Body
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            when (activeTab) {
                                0 -> {
                                    // Preset Filters
                                    Text(
                                        text = "Select Color Style",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(FilterPreset.values()) { preset ->
                                            FilterChip(
                                                selected = selectedFilter == preset,
                                                onClick = { selectedFilter = preset },
                                                label = { Text(preset.title) },
                                                colors = FilterChipDefaults.filterChipColors()
                                            )
                                        }
                                    }
                                }
                                1 -> {
                                    // Tuning Sliders
                                    Text(text = "Brightness: ${brightness.toInt()}", style = MaterialTheme.typography.bodySmall)
                                    Slider(
                                        value = brightness,
                                        onValueChange = { brightness = it },
                                        valueRange = -100f..100f
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(text = "Contrast: ${(contrast * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                    Slider(
                                        value = contrast,
                                        onValueChange = { contrast = it },
                                        valueRange = 0.5f..2.0f
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(text = "Saturation: ${(saturation * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                    Slider(
                                        value = saturation,
                                        onValueChange = { saturation = it },
                                        valueRange = 0f..2.0f
                                    )
                                }
                                2 -> {
                                    // Drawing Tools
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Brush Color & Size",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    if (drawLines.isNotEmpty()) drawLines.removeAt(drawLines.lastIndex)
                                                }
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                                            }
                                            IconButton(onClick = { drawLines.clear() }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear Canvas")
                                            }
                                        }
                                    }

                                    // Color palette
                                    val palette = listOf(
                                        Color.Red, Color.Yellow, Color.Green, Color.Cyan,
                                        Color.Blue, Color.Magenta, Color.White, Color.Black
                                    )

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                    ) {
                                        items(palette) { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                                    .border(
                                                        width = if (selectedBrushColor == color) 3.dp else 1.dp,
                                                        color = if (selectedBrushColor == color) MaterialTheme.colorScheme.primary else Color.Gray,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { selectedBrushColor = color }
                                            )
                                        }
                                    }

                                    Text(text = "Brush Size: ${brushStrokeWidth.toInt()}px", style = MaterialTheme.typography.bodySmall)
                                    Slider(
                                        value = brushStrokeWidth,
                                        onValueChange = { brushStrokeWidth = it },
                                        valueRange = 4f..30f
                                    )
                                }
                                3 -> {
                                    // Text Overlay & Direct AI Question
                                    Text(
                                        text = "Add Caption or Question on Photo",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = overlayText,
                                        onValueChange = { overlayText = it },
                                        label = { Text("Caption on Image") },
                                        placeholder = { Text("e.g., Note, Math Step, Diagram") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Ask MFG AI about this Photo 🤖",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    OutlinedTextField(
                                        value = aiPromptText,
                                        onValueChange = { aiPromptText = it },
                                        label = { Text("Question for MFG AI") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            val finalBmp = generateEditedBitmap()
                                            if (finalBmp != null) {
                                                onAskAiWithImage(finalBmp, aiPromptText)
                                                onDismiss()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Ask AI with this Edited Photo 🚀")
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            val finalBmp = generateEditedBitmap()
                                            if (finalBmp != null) {
                                                val videoPrompt = if (aiPromptText.isNotBlank()) aiPromptText else "Animate this photo into an engaging motion video"
                                                if (onAnimateToVideo != null) {
                                                    onAnimateToVideo(finalBmp, videoPrompt)
                                                } else {
                                                    onAskAiWithImage(finalBmp, "Animate image into video (veo-3.1-fast-generate-preview): $videoPrompt")
                                                }
                                                onDismiss()
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("animate_image_into_video_button"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                        )
                                    ) {
                                        Icon(Icons.Default.Movie, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Animate into Video (Veo AI) 🎬")
                                    }

                                }
                            }
                        }

                        // Bottom Actions Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Image", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val finalBmp = generateEditedBitmap()
                                    if (finalBmp != null) {
                                        onAttachToChat(finalBmp, "edited_photo.jpg")
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Use in Chat", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
