package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.LyricsData
import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import org.springframework.stereotype.Component

/**
 * 가사 목록 조회 Use Case
 */
@Component
class GetLyricsListUseCase(
    private val lyricsRepository: LyricsRepositoryInterface,
) {
    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    /**
     * 가사 목록을 cursor pagination으로 조회합니다.
     *
     * @param cursor 마지막 가사 ID (null이면 처음부터)
     * @param limit 조회할 개수 (기본값: 20)
     * @param genre 필터링할 장르 (nullable)
     * @param artist 필터링할 아티스트 (nullable)
     * @return (가사 목록, 다음 cursor, 다음 페이지 여부)
     */
    suspend fun execute(
        cursor: Long?,
        limit: Int = DEFAULT_PAGE_SIZE,
        genre: Genre?,
        artist: String?,
    ): Triple<List<LyricsData>, Long?, Boolean> {
        // limit + 1개를 조회하여 다음 페이지 존재 여부 확인
        val fetchLimit = limit + 1
        val lyrics = lyricsRepository.getLyricsList(cursor, fetchLimit, genre, artist)

        // 다음 페이지가 있는지 확인
        val hasMore = lyrics.size > limit
        val resultLyrics = if (hasMore) lyrics.take(limit) else lyrics

        // 다음 cursor 계산 (마지막 아이템의 ID)
        val nextCursor =
            if (hasMore && resultLyrics.isNotEmpty()) {
                resultLyrics.last().id
            } else {
                null
            }

        return Triple(resultLyrics, nextCursor, hasMore)
    }
}
