package com.hipchapedia.domain.entities

/**
 * 가사 검색 결과 엔티티
 */
data class LyricsSearchResult(
    val title: String,
    val artist: String,
    val lyrics: String?,
    val url: String?,
    val album: String?,
    val releaseDate: String?,
)
