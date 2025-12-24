package com.hipchapedia.application.dtos

import com.hipchapedia.domain.entities.Genre
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 가사 분석 요청 DTO
 */
data class LyricsAnalysisRequestDTO(
    @field:NotBlank(message = "곡 제목은 필수입니다.")
    val title: String,
    @field:NotBlank(message = "가사는 필수입니다.")
    val lyrics: String,
    @field:NotNull(message = "장르는 필수입니다.")
    val genre: Genre,
    val artist: String? = null,
)

/**
 * 가사 분석 응답 DTO
 */
data class LyricsAnalysisResponseDTO(
    val title: String,
    val lyrics: String,
    val genre: Genre,
    val analysisResult: String,
)

/**
 * 가사 검색 응답 DTO
 */
data class LyricsSearchResponseDTO(
    val title: String,
    val artist: String,
    val lyrics: String?,
    val url: String?,
    val album: String?,
    val releaseDate: String?,
)

/**
 * 가사 목록 조회 응답 DTO
 */
data class LyricsListResponseDTO(
    val lyrics: List<LyricsItemDTO>,
    val nextCursor: Long?,
    val hasMore: Boolean,
)

/**
 * 가사 아이템 DTO
 */
data class LyricsItemDTO(
    val id: Long,
    val title: String,
    val artist: String?,
    val genre: Genre,
    val lyrics: String,
)
