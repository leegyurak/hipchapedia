package com.hipchapedia.application.mappers

import com.hipchapedia.application.dtos.LyricsSearchResponseDTO
import com.hipchapedia.domain.entities.LyricsSearchResult

/**
 * 가사 검색 매퍼
 */
object LyricsSearchMapper {
    /**
     * Domain Entity를 Response DTO로 변환
     */
    fun toResponseDTO(result: LyricsSearchResult): LyricsSearchResponseDTO =
        LyricsSearchResponseDTO(
            title = result.title,
            artist = result.artist,
            lyrics = result.lyrics,
            url = result.url,
            album = result.album,
            releaseDate = result.releaseDate,
        )
}
