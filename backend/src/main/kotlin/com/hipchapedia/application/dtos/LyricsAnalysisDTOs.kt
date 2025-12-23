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
