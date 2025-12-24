package com.hipchapedia.application.mappers

import com.hipchapedia.application.dtos.LyricsItemDTO
import com.hipchapedia.application.dtos.LyricsListResponseDTO
import com.hipchapedia.domain.interfaces.LyricsData

/**
 * 가사 목록 Mapper
 */
object LyricsListMapper {
    /**
     * LyricsData를 LyricsItemDTO로 변환합니다.
     *
     * @param data LyricsData
     * @return LyricsItemDTO
     */
    fun toItemDTO(data: LyricsData): LyricsItemDTO =
        LyricsItemDTO(
            id = data.id,
            title = data.title,
            artist = data.artist,
            genre = data.genre,
            lyrics = data.lyrics,
        )

    /**
     * 가사 목록을 Response DTO로 변환합니다.
     *
     * @param lyricsList 가사 목록
     * @param nextCursor 다음 cursor
     * @param hasMore 다음 페이지 존재 여부
     * @return LyricsListResponseDTO
     */
    fun toResponseDTO(
        lyricsList: List<LyricsData>,
        nextCursor: Long?,
        hasMore: Boolean,
    ): LyricsListResponseDTO =
        LyricsListResponseDTO(
            lyrics = lyricsList.map { toItemDTO(it) },
            nextCursor = nextCursor,
            hasMore = hasMore,
        )
}
