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
     * @param artist 아티스트명 (nullable)
     * @return 저장된 가사 ID
     */
    suspend fun save(
        title: String,
        lyricsHash: String,
        originalLyrics: String,
        genre: Genre,
        artist: String? = null,
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

    /**
     * 가사 목록을 cursor pagination으로 조회합니다.
     *
     * @param cursor 마지막 가사 ID (null이면 처음부터)
     * @param limit 조회할 개수
     * @param genre 필터링할 장르 (nullable)
     * @param artist 필터링할 아티스트 (nullable)
     * @return (가사 ID, 곡 제목, 아티스트, 장르, 가사) 리스트
     */
    suspend fun getLyricsList(
        cursor: Long?,
        limit: Int,
        genre: Genre?,
        artist: String?,
    ): List<LyricsData>

    /**
     * ID로 가사와 분석 결과를 조회합니다.
     *
     * @param id 가사 ID
     * @return (곡 제목, 가사, 장르, 분석 결과) 또는 null
     */
    suspend fun getLyricsById(id: Long): LyricsWithAnalysis?
}

/**
 * 가사 데이터 domain entity
 */
data class LyricsData(
    val id: Long,
    val title: String,
    val artist: String?,
    val genre: Genre,
    val lyrics: String,
)

/**
 * 가사와 분석 결과 domain entity
 */
data class LyricsWithAnalysis(
    val title: String,
    val lyrics: String,
    val genre: Genre,
    val analysisResult: String,
)
