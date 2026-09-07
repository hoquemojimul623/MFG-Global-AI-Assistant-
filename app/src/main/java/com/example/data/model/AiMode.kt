package com.example.data.model

enum class AiMode(
    val id: String,
    val title: String,
    val emoji: String,
    val subtitle: String,
    val promptInstruction: String
) {
    FAST(
        id = "fast",
        title = "Fast",
        emoji = "⚡",
        subtitle = "দ্ৰুত আৰু সঠিক উত্তৰ",
        promptInstruction = """
        MODE: FAST (Quick & Direct)
        - Deliver quick, crisp, accurate, and direct answers without unnecessary preamble or fluff.
        - Prioritize fast comprehension with clear language.
        """.trimIndent()
    ),
    VERY_FAST(
        id = "very_fast",
        title = "Very fast",
        emoji = "🚀",
        subtitle = "অতি খৰতকীয়া উত্তৰ",
        promptInstruction = """
        MODE: VERY FAST (Ultra-Low Latency & Pinpoint)
        - Provide immediate, ultra-concise, and high-speed bullet points or direct one-liners.
        - Answer with maximum brevity and 100% precision.
        """.trimIndent()
    ),
    AI_MODEL(
        id = "ai_model",
        title = "AI model",
        emoji = "🧠",
        subtitle = "গভীৰ বিশ্লেষণ আৰু যুক্তি",
        promptInstruction = """
        MODE: AI MODEL (Deep Analytical Reasoning)
        - Provide comprehensive, deeply reasoned, and multi-dimensional analysis.
        - Explore core principles, step-by-step logic, edge cases, and comparative perspectives.
        """.trimIndent()
    ),
    MFG_MODEL(
        id = "mfg_model",
        title = "MFG model",
        emoji = "👑",
        subtitle = "অফিচিয়েল MFG ৰেফাৰেন্স শৈলী",
        promptInstruction = """
        MODE: MFG MODEL (Official MFG Reference & Creator Style)
        - Provide authoritative, structured, and reference-grade responses with clear sections and executive polish.
        - Emphasize innovation, quality, and the MFG standard created by deployer MOJIMUL HOQUE.
        - Reference style: use clean bullet points, bold key terms, and summary takeaways.
        """.trimIndent()
    ),
    EDUCATION(
        id = "education",
        title = "Education",
        emoji = "📚",
        subtitle = "খোজ-অনুক্ৰমে শিক্ষণ আৰু ব্যাখ্যা",
        promptInstruction = """
        MODE: EDUCATION (Academic & Step-by-Step Learning)
        - Act as a master educator and patient academic mentor.
        - Provide structured, step-by-step explanations, fundamental formulas, real-world examples, solved problems, and conceptual summaries.
        - Explain complex ideas in easy, student-friendly language with learning takeaways.
        """.trimIndent()
    ),
    BUSINESS(
        id = "business",
        title = "Business",
        emoji = "💼",
        subtitle = "ব্যৱসায়, ৰণনীতি আৰু বৃদ্ধি",
        promptInstruction = """
        MODE: BUSINESS (Executive Strategy & Monetization)
        - Act as a senior business consultant, startup advisor, and financial strategist.
        - Deliver actionable business strategies, market analysis, monetization models, ROI insights, marketing tactics, and professional corporate-grade solutions.
        """.trimIndent()
    ),
    SEARCH(
        id = "search",
        title = "Google Search",
        emoji = "🔍",
        subtitle = "Live Google Search তথ্য & ৱেব গ্ৰাউণ্ডিং",
        promptInstruction = """
        MODE: GOOGLE SEARCH (Live Web Grounding & Fact Verification)
        - Access and integrate real-time up-to-date web information and search results.
        - Cite facts clearly with verification sources and fresh context.
        """.trimIndent()
    ),
    MAPS(
        id = "maps",
        title = "Google Maps",
        emoji = "📍",
        subtitle = "Live স্থান, ঠিকনা & ভৌগোলিক তথ্য",
        promptInstruction = """
        MODE: GOOGLE MAPS (Location, Directions & Geography Grounding)
        - Provide accurate location intelligence, distances, navigation directions, points of interest, and geography details.
        """.trimIndent()
    );

    companion object {
        fun fromId(id: String?): AiMode {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FAST
        }
    }
}
