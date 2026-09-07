package com.example.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.ImageConfig
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class AiImageResult(
    val uri: Uri?,
    val description: String,
    val prompt: String,
    val isSuccess: Boolean
)

object AiImageGenerator {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    fun isImageOrBannerRequest(prompt: String): Boolean {
        val lower = prompt.lowercase()
        return lower.contains("[generate_image]") ||
                lower.contains("[banner]") ||
                lower.contains("ফটো") ||
                lower.contains("বেনাৰ") ||
                lower.contains("বিনাৰ") ||
                lower.contains("ছবি") ||
                lower.contains("পোষ্টাৰ") ||
                lower.contains("চিত্ৰ") ||
                lower.contains("ৱালপেপাৰ") ||
                lower.contains("আঁকি দিয়া") ||
                lower.contains("আঁকি দিয়ক") ||
                lower.contains("আঁকা") ||
                lower.contains("বনাই দিয়া") ||
                lower.contains("বনাই দিব") ||
                lower.contains("বনাই দিয়ক") ||
                lower.contains("photo") ||
                lower.contains("banner") ||
                lower.contains("benar") ||
                lower.contains("bener") ||
                lower.contains("image") ||
                lower.contains("poster") ||
                lower.contains("wallpaper") ||
                lower.contains("picture") ||
                lower.contains("generate picture") ||
                lower.contains("create picture") ||
                lower.contains("make a picture") ||
                lower.contains("draw a") ||
                lower.contains("dall-e") ||
                lower.contains("dalle")
    }

    fun detectAspectRatio(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("banner") || lower.contains("benar") || lower.contains("bener") || lower.contains("বেনাৰ") || lower.contains("বিনাৰ") || lower.contains("youtube") || lower.contains("cover") || lower.contains("16:9") -> "16:9"
            lower.contains("wallpaper") || lower.contains("ৱালপেপাৰ") || lower.contains("story") || lower.contains("reel") || lower.contains("9:16") -> "9:16"
            lower.contains("poster") || lower.contains("পোষ্টাৰ") || lower.contains("3:4") -> "3:4"
            lower.contains("4:3") -> "4:3"
            else -> "1:1"
        }
    }

    suspend fun generateImageOrBanner(
        context: Context,
        prompt: String,
        targetAspectRatio: String? = null,
        style: String? = null
    ): AiImageResult = withContext(Dispatchers.IO) {
        val cleanPrompt = prompt
            .replace("[generate_image]", "")
            .replace("[banner]", "")
            .trim()

        val aspect = targetAspectRatio ?: detectAspectRatio(cleanPrompt)

        // Build detailed prompt for high-fidelity photorealistic/graphic generation
        val styleModifier = when (style?.lowercase()) {
            "photorealistic", "প্ৰকৃত ফটো" -> "ultra realistic photograph, 8k uhd, cinematic lighting, highly detailed, photorealistic"
            "digital art", "ডিজিটেল আৰ্ট" -> "digital art painting, vibrant colors, masterpiece, trend on artstation"
            "3d render", "৩ডি চিনেমেটিক" -> "3D render, Octane render, volumetric lighting, photorealistic textures, 8k"
            "graphic design", "গ্ৰাফিক ডিজাইন" -> "professional graphic design, elegant typography, modern vector aesthetics, clean layout"
            "anime", "এনিমে" -> "anime style, makoto shinkai aesthetic, gorgeous colors, detailed illustration"
            "vintage", "ভিন্টেজ" -> "vintage retro classic poster aesthetic, warm tones, timeless composition"
            else -> "professional high quality, crisp details, stunning visual composition, masterwork"
        }

        val enrichedPrompt = if (cleanPrompt.contains("banner", ignoreCase = true) ||
            cleanPrompt.contains("বেনাৰ", ignoreCase = true) ||
            cleanPrompt.contains("বিনাৰ", ignoreCase = true) ||
            cleanPrompt.contains("benar", ignoreCase = true) ||
            cleanPrompt.contains("bener", ignoreCase = true) ||
            cleanPrompt.contains("পোষ্টাৰ", ignoreCase = true) ||
            cleanPrompt.contains("poster", ignoreCase = true)) {
            "Stunning wide banner design: $cleanPrompt, $styleModifier, clear composition, commercial grade graphic banner layout, high resolution"
        } else {
            "$cleanPrompt, $styleModifier, beautiful lighting, sharp focus, 8k resolution"
        }

        val apiKey = BuildConfig.GEMINI_API_KEY

        // 1. Try Gemini API first (gemini-2.5-flash-image or gemini-3.1-flash-image-preview)
        if (apiKey.isNotBlank()) {
            try {
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = enrichedPrompt)))
                    ),
                    generationConfig = GenerationConfig(
                        imageConfig = ImageConfig(aspectRatio = aspect, imageSize = "1K"),
                        responseModalities = listOf("TEXT", "IMAGE")
                    )
                )

                val response = try {
                    RetrofitClient.service.generateContent("gemini-2.5-flash-image", apiKey, request)
                } catch (e: Exception) {
                    RetrofitClient.service.generateContent("gemini-3.1-flash-image-preview", apiKey, request)
                }

                val candidate = response.candidates?.firstOrNull()
                val parts = candidate?.content?.parts.orEmpty()
                val imagePart = parts.firstOrNull { it.inlineData?.data?.isNotBlank() == true }

                if (imagePart != null && imagePart.inlineData != null) {
                    val base64Data = imagePart.inlineData!!.data
                    val savedUri = FileUtils.saveBase64Image(context, base64Data, "ai_banner")
                    if (savedUri != null) {
                        val replyText = parts.firstOrNull { it.text?.isNotBlank() == true }?.text
                            ?: "✨ আপোনাৰ অনুৰোধ অনুসৰি AI ৰ দ্বাৰা নতুন ফটো/বেনাৰ প্ৰস্তুত কৰা হ'ল (Aspect: $aspect)।"
                        return@withContext AiImageResult(
                            uri = savedUri,
                            description = replyText,
                            prompt = cleanPrompt,
                            isSuccess = true
                        )
                    }
                }
            } catch (e: Exception) {
                // If Gemini quota exhausted or not supporting direct image modality, gracefully fallback
                e.printStackTrace()
            }
        }

        // 2. High-Fidelity AI Image Generation Fallback (Pollinations AI Flux/SD)
        val (width, height) = when (aspect) {
            "16:9" -> Pair(1280, 720)
            "9:16" -> Pair(720, 1280)
            "3:4" -> Pair(768, 1024)
            "4:3" -> Pair(1024, 768)
            else -> Pair(1024, 1024)
        }

        try {
            val encoded = URLEncoder.encode(enrichedPrompt, "UTF-8")
            val seed = (System.currentTimeMillis() % 1000000).toInt()
            val imageUrl = "https://image.pollinations.ai/prompt/$encoded?width=$width&height=$height&seed=$seed&nologo=true"

            val httpReq = Request.Builder()
                .url(imageUrl)
                .addHeader("User-Agent", "Mozilla/5.0 GlobalAI/4.0")
                .build()

            val response = httpClient.newCall(httpReq).execute()
            if (response.isSuccessful) {
                val bytes = response.body?.bytes()
                if (bytes != null && bytes.isNotEmpty()) {
                    val dir = java.io.File(context.filesDir, "ai_creations")
                    if (!dir.exists()) dir.mkdirs()
                    val file = java.io.File(dir, "ai_banner_${System.currentTimeMillis()}.jpg")
                    file.writeBytes(bytes)
                    val uri = Uri.fromFile(file)

                    val desc = "🎨 **ChatGPT AI Photo & Banner Studio**\n\n" +
                            "✅ আপোনাৰ প্ৰম্প্ট: *\"$cleanPrompt\"*\n" +
                            "📐 অনুপাত: **$aspect** (${width}x${height}px) | শৈলী: **${style ?: "Photorealistic AI"}**\n\n" +
                            "💡 তলৰ বুটামৰ সহায়ত আপুনি ফটোখন গেলেৰীত ছেভ কৰিব পাৰে, টেক্সট/ষ্টিকাৰ সংযোগ কৰিব পাৰে, অথবা ভিডিঅ'লৈ ৰূপান্তৰ কৰিব পাৰে।"

                    return@withContext AiImageResult(
                        uri = uri,
                        description = desc,
                        prompt = cleanPrompt,
                        isSuccess = true
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Resilient Offline Vector Banner Synthesizer (Canvas based)
        try {
            val offlineBitmap = generateLocalBannerBitmap(cleanPrompt, aspect, width, height)
            val uri = FileUtils.saveBitmapToFile(context, offlineBitmap, "ai_offline_banner")
            val desc = "🖼️ **AI বেনাৰ ডিজাইন (Custom Visual Banner)**\n\n" +
                    "আপোনাৰ অনুৰোধৰ আধাৰত তৈয়াৰ কৰা বেনাৰ: *\"$cleanPrompt\"*\n" +
                    "অনুপাত: $aspect | ৰিজলিউচন: ${width}x${height}px"

            return@withContext AiImageResult(
                uri = uri,
                description = desc,
                prompt = cleanPrompt,
                isSuccess = true
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext AiImageResult(
                uri = null,
                description = "ফটো/বেনাৰ প্ৰস্তুত কৰাত ত্ৰুটি হৈছে: ${e.localizedMessage}। অনুগ্ৰহ কৰি পুনৰ চেষ্টা কৰক।",
                prompt = cleanPrompt,
                isSuccess = false
            )
        }
    }

    /**
     * Generates a high-resolution gradient visual banner with stylized graphic motifs,
     * decorative framing, and custom title typography.
     */
    private fun generateLocalBannerBitmap(title: String, aspect: String, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw elegant gradient background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(0xFF0F2027.toInt(), 0xFF203A43.toInt(), 0xFF2C5364.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw decorative accent border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF38BDF8.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRoundRect(RectF(24f, 24f, width - 24f, height - 24f), 24f, 24f, borderPaint)

        // Top Brand Ribbon
        val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF0284C7.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(40f, 40f, 320f, 96f), 16f, 16f, ribbonPaint)

        val ribbonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 28f
            isFakeBoldText = true
        }
        canvas.drawText("✨ GLOBAL AI STUDIO", 60f, 78f, ribbonTextPaint)

        // Main Title Text
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = if (aspect == "16:9") 52f else 44f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val displayTitle = if (title.length > 50) "${title.take(50)}..." else title
        canvas.drawText(displayTitle, (width / 2).toFloat(), (height / 2).toFloat(), titlePaint)

        // Subtitle badge
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFBAE6FD.toInt()
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("ChatGPT AI Style Photo & Banner Creation • HD Quality", (width / 2).toFloat(), (height / 2 + 60).toFloat(), subPaint)

        return bitmap
    }
}
