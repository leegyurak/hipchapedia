package com.hipchapedia.application.mappers

import com.hipchapedia.domain.entities.LyricsSearchResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LyricsSearchMapperTest {
    @Test
    fun `LyricsSearchResult를 LyricsSearchResponseDTO로 변환해야 한다`() {
        // given
        val result =
            LyricsSearchResult(
                title = "Test Song",
                artist = "Test Artist",
                lyrics = "Test lyrics content",
                url = "https://genius.com/test-song",
                album = "Test Album",
                releaseDate = "2024-01-01",
            )

        // when
        val dto = LyricsSearchMapper.toResponseDTO(result)

        // then
        assertEquals("Test Song", dto.title)
        assertEquals("Test Artist", dto.artist)
        assertEquals("Test lyrics content", dto.lyrics)
        assertEquals("https://genius.com/test-song", dto.url)
        assertEquals("Test Album", dto.album)
        assertEquals("2024-01-01", dto.releaseDate)
    }

    @Test
    fun `null 필드가 있는 LyricsSearchResult를 변환해야 한다`() {
        // given
        val result =
            LyricsSearchResult(
                title = "Test Song",
                artist = "Test Artist",
                lyrics = null,
                url = null,
                album = null,
                releaseDate = null,
            )

        // when
        val dto = LyricsSearchMapper.toResponseDTO(result)

        // then
        assertEquals("Test Song", dto.title)
        assertEquals("Test Artist", dto.artist)
        assertNull(dto.lyrics)
        assertNull(dto.url)
        assertNull(dto.album)
        assertNull(dto.releaseDate)
    }

    @Test
    fun `모든 필드가 null인 경우에도 변환해야 한다`() {
        // given
        val result =
            LyricsSearchResult(
                title = "Title",
                artist = "Artist",
                lyrics = null,
                url = null,
                album = null,
                releaseDate = null,
            )

        // when
        val dto = LyricsSearchMapper.toResponseDTO(result)

        // then
        assertEquals("Title", dto.title)
        assertEquals("Artist", dto.artist)
        assertNull(dto.lyrics)
        assertNull(dto.url)
        assertNull(dto.album)
        assertNull(dto.releaseDate)
    }
}
