package com.example.data.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {

    fun getFileName(context: Context, uri: Uri): String {
        var name = "attachment"
        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = it.getString(nameIndex) ?: name
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return name
    }

    fun getMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: when {
            uri.toString().endsWith(".png", true) -> "image/png"
            uri.toString().endsWith(".jpg", true) || uri.toString().endsWith(".jpeg", true) -> "image/jpeg"
            uri.toString().endsWith(".webp", true) -> "image/webp"
            uri.toString().endsWith(".pdf", true) -> "application/pdf"
            uri.toString().endsWith(".txt", true) -> "text/plain"
            uri.toString().endsWith(".mp4", true) -> "video/mp4"
            else -> "application/octet-stream"
        }
    }

    fun uriToBase64(context: Context, uri: Uri, maxSize: Int = 1024): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Scale down if too large to prevent memory overhead & API payload limits
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > maxSize || height > maxSize) {
                val maxDim = maxOf(width, height)
                maxSize.toFloat() / maxDim
            } else {
                1f
            }

            val resizedBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                    true
                )
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun readTextFromUri(context: Context, uri: Uri, maxChars: Int = 15000): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
            val reader = inputStream.bufferedReader()
            val text = reader.readText()
            inputStream.close()
            if (text.length > maxChars) {
                text.substring(0, maxChars) + "\n\n...[Document truncated due to length]..."
            } else {
                text
            }
        } catch (e: Exception) {
            "Error reading document: ${e.localizedMessage}"
        }
    }

    fun saveBase64Image(context: Context, base64Data: String, prefix: String = "ai_image"): Uri? {
        return try {
            val bytes = Base64.decode(base64Data, Base64.DEFAULT)
            val dir = File(context.filesDir, "ai_creations")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "${prefix}_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { it.write(bytes) }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, prefix: String = "ai_banner"): Uri? {
        return try {
            val dir = File(context.filesDir, "ai_creations")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveImageToGallery(context: Context, sourceUri: Uri, title: String = "AI Creation"): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return false
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (bitmap == null) return false

            val filename = "${title.replace("[^a-zA-Z0-9]".toRegex(), "_")}_${System.currentTimeMillis()}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/GlobalAI")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val uri = context.contentResolver.insert(collection, contentValues) ?: return false
            val outputStream = context.contentResolver.openOutputStream(uri) ?: return false
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            outputStream.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
