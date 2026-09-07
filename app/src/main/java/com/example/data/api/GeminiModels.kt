package com.example.data.api

import com.squareup.moshi.Json

data class GenerateContentRequest(
    val contents: List<Content>,
    @property:Json(name = "systemInstruction") val systemInstruction: Content? = null,
    @property:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null
)

data class Tool(
    @property:Json(name = "googleSearch") val googleSearch: GoogleSearchTool? = null,
    @property:Json(name = "googleSearchRetrieval") val googleSearchRetrieval: GoogleSearchRetrieval? = null
)

class GoogleSearchTool

data class GoogleSearchRetrieval(
    val dynamicRetrievalConfig: DynamicRetrievalConfig? = null
)

data class DynamicRetrievalConfig(
    val mode: String = "MODE_DYNAMIC",
    val dynamicThreshold: Float = 0.3f
)

data class Content(
    val role: String? = null,
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    @property:Json(name = "inlineData") val inlineDataCamel: InlineData? = null,
    @property:Json(name = "inline_data") val inlineDataSnake: InlineData? = null
) {
    val inlineData: InlineData?
        get() = inlineDataCamel ?: inlineDataSnake
}

data class InlineData(
    @property:Json(name = "mimeType") val mimeTypeCamel: String? = null,
    @property:Json(name = "mime_type") val mimeTypeSnake: String? = null,
    val data: String = ""
) {
    val mimeType: String
        get() = mimeTypeCamel ?: mimeTypeSnake ?: "image/jpeg"
}

data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val responseModalities: List<String>? = null,
    val imageConfig: ImageConfig? = null,
    val speechConfig: SpeechConfig? = null,
    val thinkingConfig: ThinkingConfig? = null
)

data class ImageConfig(
    val aspectRatio: String? = "1:1",
    val imageSize: String? = "1K"
)

data class SpeechConfig(
    val voiceConfig: VoiceConfig? = null
)

data class VoiceConfig(
    val prebuiltVoiceConfig: PrebuiltVoiceConfig? = null
)

data class PrebuiltVoiceConfig(
    val voiceName: String = "Kore"
)

data class ThinkingConfig(
    val thinkingLevel: String = "low"
)

data class GenerateContentResponse(
    val candidates: List<Candidate>? = null,
    val error: ApiError? = null
)

data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null,
    val groundingMetadata: GroundingMetadata? = null
)

data class GroundingMetadata(
    val searchEntryPoint: SearchEntryPoint? = null,
    val webSearchQueries: List<String>? = null,
    val groundingChunks: List<GroundingChunk>? = null
)

data class SearchEntryPoint(
    val renderedContent: String? = null
)

data class GroundingChunk(
    val web: WebChunk? = null
)

data class WebChunk(
    val uri: String? = null,
    val title: String? = null
)

data class ApiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

