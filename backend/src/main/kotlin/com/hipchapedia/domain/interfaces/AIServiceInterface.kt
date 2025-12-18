package com.hipchapedia.domain.interfaces

/**
 * AI 분석 서비스 인터페이스
 */
interface AIServiceInterface {
    /**
     * 가사를 분석합니다.
     *
     * @param title 곡 제목
     * @param lyrics 가사 내용
     * @return 분석 결과 (Markdown 형식)
     */
    suspend fun analyzeLyrics(
        title: String,
        lyrics: String,
    ): String
}
