package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.LyricsSearchResult
import com.hipchapedia.domain.interfaces.LyricsSearchRepositoryInterface
import org.springframework.stereotype.Component

/**
 * 가사 검색 Use Case
 */
@Component
class SearchLyricsUseCase(
    private val lyricsSearchRepository: LyricsSearchRepositoryInterface,
) {
    /**
     * 가사를 검색합니다.
     *
     * @param title 곡 제목
     * @param artist 아티스트명
     * @return 검색 결과 (타임아웃 시 null)
     */
    suspend fun execute(
        title: String,
        artist: String,
    ): LyricsSearchResult? {
        // 검색 요청 발행
        lyricsSearchRepository.publishSearchRequest(title, artist)

        // 결과 대기
        return lyricsSearchRepository.waitForResult(title, artist)
    }
}
