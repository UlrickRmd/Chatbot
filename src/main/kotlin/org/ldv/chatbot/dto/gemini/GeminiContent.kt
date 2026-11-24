package org.ldv.chatbot.dto.gemini

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart> = emptyList()
)
