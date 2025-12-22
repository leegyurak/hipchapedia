package com.hipchapedia.domain.interfaces

import com.hipchapedia.domain.entities.LyricsSearchResult

/**
 * 가사 검색 리포지토리 인터페이스
 */
interface LyricsSearchRepositoryInterface {
    /**
     * 가사 검색 요청을 발행합니다.
     *
     * @param title 곡 제목
     * @param artist 아티스트명
     */
    fun publishSearchRequest(
        title: String,
        artist: String,
    )

    /**
     * 가사 검색 결과를 대기합니다.
     *
     * @param title 곡 제목
     * @param artist 아티스트명
     * @param timeoutSeconds 타임아웃 (초)
     * @return 검색 결과 (타임아웃 시 null)
     */
    suspend fun waitForResult(
        title: String,
        artist: String,
        timeoutSeconds: Long = 10,
    ): LyricsSearchResult?
}
