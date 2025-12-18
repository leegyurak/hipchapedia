package com.hipchapedia.application.dtos

import jakarta.validation.constraints.NotBlank

/**
 * 가사 분석 요청 DTO
 */
data class LyricsAnalysisRequestDTO(
    @field:NotBlank(message = "곡 제목은 필수입니다.")
    val title: String,
    @field:NotBlank(message = "가사는 필수입니다.")
    val lyrics: String,
)

/**
 * 가사 분석 응답 DTO
 */
data class LyricsAnalysisResponseDTO(
    val title: String,
    val lyrics: String,
    val analysisResult: String,
)
