package com.hipchapedia.application.mappers

import com.hipchapedia.application.dtos.LyricsAnalysisResponseDTO
import com.hipchapedia.domain.interfaces.LyricsWithAnalysis

/**
 * ID로 가사 조회 Mapper
 */
object LyricsByIdMapper {
    /**
     * LyricsWithAnalysis를 Response DTO로 변환합니다.
     *
     * @param data LyricsWithAnalysis
     * @return LyricsAnalysisResponseDTO
     */
    fun toResponseDTO(data: LyricsWithAnalysis): LyricsAnalysisResponseDTO =
        LyricsAnalysisResponseDTO(
            title = data.title,
            lyrics = data.lyrics,
            genre = data.genre,
            analysisResult = data.analysisResult,
        )
}
