package com.example.data.model

enum class SupportedLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flagEmoji: String,
    val speechLocaleTag: String,
    val promptInstruction: String
) {
    AUTO(
        code = "auto",
        nativeName = "Auto",
        englishName = "Automatic",
        flagEmoji = "🌐",
        speechLocaleTag = "",
        promptInstruction = "LANGUAGE DIRECTIVE: Automatically detect the user's language and respond naturally in that exact language (Assamese, English, Bengali, Hindi, etc.)."
    ),
    ASSAMESE(
        code = "as",
        nativeName = "অসমীয়া",
        englishName = "Assamese",
        flagEmoji = "🇮🇳",
        speechLocaleTag = "as-IN",
        promptInstruction = "LANGUAGE DIRECTIVE (CRITICAL): Always respond strictly in proper, high-quality, and fluent Assamese (অসমীয়া ভাষা) with correct grammar and script. Even if the user asks in English or another language, translate and explain your full response in natural Assamese."
    ),
    ENGLISH(
        code = "en",
        nativeName = "English",
        englishName = "English",
        flagEmoji = "🇬🇧",
        speechLocaleTag = "en-US",
        promptInstruction = "LANGUAGE DIRECTIVE (CRITICAL): Always respond strictly in clear, articulate, and grammatically correct English. Provide crisp explanations and well-structured answers in English."
    ),
    BENGALI(
        code = "bn",
        nativeName = "বাংলা",
        englishName = "Bengali",
        flagEmoji = "🇮🇳",
        speechLocaleTag = "bn-IN",
        promptInstruction = "LANGUAGE DIRECTIVE (CRITICAL): Always respond strictly in standard, elegant, and grammatically correct Bengali (বাংলা ভাষা) using proper Bengali script and vocabulary."
    ),
    HINDI(
        code = "hi",
        nativeName = "हिन्दी",
        englishName = "Hindi",
        flagEmoji = "🇮🇳",
        speechLocaleTag = "hi-IN",
        promptInstruction = "LANGUAGE DIRECTIVE (CRITICAL): Always respond strictly in natural, polite, and fluent Hindi (हिन्दी भाषा) using Devanagari script."
    ),
    BODO(
        code = "brx",
        nativeName = "बड़ो",
        englishName = "Bodo",
        flagEmoji = "🇮🇳",
        speechLocaleTag = "hi-IN",
        promptInstruction = "LANGUAGE DIRECTIVE: Respond in Bodo language (बड़ो राव) using Devanagari script or clean easy explanation when requested."
    );

    companion object {
        fun fromCode(code: String?): SupportedLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: AUTO
        }
    }
}
