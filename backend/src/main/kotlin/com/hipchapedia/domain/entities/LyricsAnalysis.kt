package com.hipchapedia.domain.entities

import java.time.LocalDateTime

/**
 * 가사 분석 도메인 엔티티
 */
data class LyricsAnalysis(
    val title: String,
    val lyrics: String,
    val genre: Genre,
    val analysisResult: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val id: Long? = null,
) {
    init {
        require(title.isNotBlank()) { "곡 제목은 필수입니다." }
        require(lyrics.isNotBlank()) { "가사는 필수입니다." }
    }

    fun withAnalysisResult(result: String): LyricsAnalysis = copy(analysisResult = result)

    fun withId(newId: Long): LyricsAnalysis = copy(id = newId)
}
