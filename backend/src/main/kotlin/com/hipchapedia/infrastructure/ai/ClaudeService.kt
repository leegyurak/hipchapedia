package com.hipchapedia.infrastructure.ai

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.AIServiceInterface
import com.hipchapedia.infrastructure.config.AnthropicConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

/**
 * Claude API를 사용한 AI 서비스 구현
 */
@Service
class ClaudeService(
    private val anthropicConfig: AnthropicConfig,
) : AIServiceInterface {
    private val client =
        OkHttpClient
            .Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    private val objectMapper = jacksonObjectMapper()

    /**
     * 장르에 맞는 시스템 프롬프트를 로드합니다.
     */
    private fun loadSystemPrompt(genre: Genre): String {
        val promptFileName =
            when (genre) {
                Genre.HIPHOP -> "hiphop_lyrics_analysis_prompt.md"
                Genre.RNB -> "rnb_lyrics_analysis_prompt.md"
                Genre.KPOP -> "kpop_lyrics_analysis_prompt.md"
                Genre.JPOP -> "jpop_lyrics_analysis_prompt.md"
                Genre.BAND -> "band_lyrics_analysis_prompt.md"
            }

        val resource = ClassPathResource("prompts/$promptFileName")
        return resource.inputStream.bufferedReader().use { it.readText() }
    }

    /**
     * 장르에 맞는 사용자 메시지를 생성합니다.
     */
    private fun createUserMessage(
        title: String,
        lyrics: String,
        genre: Genre,
    ): String {
        val genreDescription =
            when (genre) {
                Genre.HIPHOP -> "힙합"
                Genre.RNB -> "R&B"
                Genre.KPOP -> "K-POP"
                Genre.JPOP -> "J-POP"
                Genre.BAND -> "밴드"
            }

        return """
            곡 제목: $title

            가사:
            $lyrics

            위 $genreDescription 곡의 가사를 분석해주세요.
            """.trimIndent()
    }

    override suspend fun analyzeLyrics(
        title: String,
        lyrics: String,
        genre: Genre,
    ): String =
        withContext(Dispatchers.IO) {
            val systemPrompt = loadSystemPrompt(genre)
            val userMessage = createUserMessage(title, lyrics, genre)

            val requestBody =
                objectMapper
                    .createObjectNode()
                    .apply {
                        put("model", anthropicConfig.model)
                        put("max_tokens", 8192)
                        put("system", systemPrompt)
                        putArray("messages").apply {
                            addObject().apply {
                                put("role", "user")
                                put("content", userMessage)
                            }
                        }
                    }.toString()

            val request =
                Request
                    .Builder()
                    .url("https://api.anthropic.com/v1/messages")
                    .addHeader("x-api-key", anthropicConfig.apiKey)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw RuntimeException("Claude API 호출 실패: ${response.code} ${response.message}")
            }

            val responseBody = response.body?.string() ?: throw RuntimeException("응답 본문이 없습니다")
            val jsonResponse = objectMapper.readTree(responseBody)

            extractTextFromResponse(jsonResponse)
        }

    private fun extractTextFromResponse(response: JsonNode): String {
        val content = response.get("content")
        if (content == null || !content.isArray || content.isEmpty) {
            throw IllegalStateException("응답에 content가 없습니다")
        }

        val firstContent = content[0]
        val type = firstContent.get("type")?.asText()

        if (type != "text") {
            throw IllegalStateException("예상치 못한 content type: $type")
        }

        return firstContent.get("text")?.asText()
            ?: throw IllegalStateException("text 필드가 없습니다")
    }
}
