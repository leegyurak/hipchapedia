package com.hipchapedia.application.mappers

import com.hipchapedia.application.dtos.LyricsAnalysisResponseDTO
import com.hipchapedia.domain.entities.LyricsAnalysis

/**
 * 가사 분석 Mapper
 */
object LyricsAnalysisMapper {
    /**
     * Entity를 Response DTO로 변환합니다.
     *
     * @param entity LyricsAnalysis 엔티티
     * @return LyricsAnalysisResponseDTO
     */
    fun toResponseDTO(entity: LyricsAnalysis): LyricsAnalysisResponseDTO =
        LyricsAnalysisResponseDTO(
            title = entity.title,
            lyrics = entity.lyrics,
            genre = entity.genre,
            analysisResult =
                entity.analysisResult
                    ?: throw IllegalStateException("분석 결과가 없습니다."),
        )
}
