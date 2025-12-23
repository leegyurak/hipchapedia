package com.hipchapedia.domain.interfaces

import com.hipchapedia.domain.entities.Genre

/**
 * 가사 저장소 인터페이스
 */
interface LyricsRepositoryInterface {
    /**
     * 가사 해시로 기존 가사를 조회합니다.
     *
     * @param lyricsHash 가사 해시
     * @return (가사 ID, 곡 제목, 장르) 또는 null
     */
    suspend fun getByHash(lyricsHash: String): Triple<Long, String, Genre>?

    /**
     * 가사 ID로 분석 결과를 조회합니다.
     *
     * @param lyricsId 가사 ID
     * @return 분석 결과 또는 null
     */
    suspend fun getAnalysisResultByLyricsId(lyricsId: Long): String?

    /**
     * 새로운 가사를 저장합니다.
     *
     * @param title 곡 제목
     * @param lyricsHash 가사 해시
     * @param originalLyrics 원본 가사 내용
     * @param genre 장르
     * @return 저장된 가사 ID
     */
    suspend fun save(
        title: String,
        lyricsHash: String,
        originalLyrics: String,
        genre: Genre,
    ): Long

    /**
     * 분석 결과를 저장합니다.
     *
     * @param lyricsId 가사 ID
     * @param analysisResult 분석 결과
     */
    suspend fun saveAnalysisResult(
        lyricsId: Long,
        analysisResult: String,
    )
}
