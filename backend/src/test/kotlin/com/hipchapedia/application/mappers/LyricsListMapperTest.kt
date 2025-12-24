package com.hipchapedia.application.mappers

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.LyricsData
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LyricsListMapperTest {
    @Test
    fun `LyricsData를 LyricsItemDTO로 변환해야 한다`() {
        // given
        val data =
            LyricsData(
                id = 1L,
                title = "Test Song",
                artist = "Test Artist",
                genre = Genre.HIPHOP,
                lyrics = "Test lyrics content",
            )

        // when
        val dto = LyricsListMapper.toItemDTO(data)

        // then
        assertEquals(1L, dto.id)
        assertEquals("Test Song", dto.title)
        assertEquals("Test Artist", dto.artist)
        assertEquals(Genre.HIPHOP, dto.genre)
        assertEquals("Test lyrics content", dto.lyrics)
    }

    @Test
    fun `artist가 null인 LyricsData도 정상적으로 변환해야 한다`() {
        // given
        val data =
            LyricsData(
                id = 1L,
                title = "Test Song",
                artist = null,
                genre = Genre.RNB,
                lyrics = "Test lyrics",
            )

        // when
        val dto = LyricsListMapper.toItemDTO(data)

        // then
        assertNull(dto.artist)
    }

    @Test
    fun `가사 목록을 Response DTO로 변환해야 한다`() {
        // given
        val lyricsList =
            listOf(
                LyricsData(
                    id = 3L,
                    title = "Song 3",
                    artist = "Artist 3",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 3",
                ),
                LyricsData(
                    id = 2L,
                    title = "Song 2",
                    artist = "Artist 2",
                    genre = Genre.RNB,
                    lyrics = "lyrics 2",
                ),
            )
        val nextCursor = 2L
        val hasMore = true

        // when
        val dto = LyricsListMapper.toResponseDTO(lyricsList, nextCursor, hasMore)

        // then
        assertEquals(2, dto.lyrics.size)
        assertEquals(3L, dto.lyrics[0].id)
        assertEquals("Song 3", dto.lyrics[0].title)
        assertEquals(2L, dto.nextCursor)
        assertTrue(dto.hasMore)
    }

    @Test
    fun `빈 가사 목록도 정상적으로 변환해야 한다`() {
        // given
        val lyricsList = emptyList<LyricsData>()
        val nextCursor = null
        val hasMore = false

        // when
        val dto = LyricsListMapper.toResponseDTO(lyricsList, nextCursor, hasMore)

        // then
        assertTrue(dto.lyrics.isEmpty())
        assertNull(dto.nextCursor)
        assertFalse(dto.hasMore)
    }

    @Test
    fun `다음 페이지가 없으면 nextCursor가 null이어야 한다`() {
        // given
        val lyricsList =
            listOf(
                LyricsData(
                    id = 1L,
                    title = "Song 1",
                    artist = "Artist 1",
                    genre = Genre.KPOP,
                    lyrics = "lyrics 1",
                ),
            )
        val nextCursor = null
        val hasMore = false

        // when
        val dto = LyricsListMapper.toResponseDTO(lyricsList, nextCursor, hasMore)

        // then
        assertNull(dto.nextCursor)
        assertFalse(dto.hasMore)
    }

    @Test
    fun `모든 장르 타입에 대해 정상적으로 변환해야 한다`() {
        // given
        val genres = listOf(Genre.HIPHOP, Genre.RNB, Genre.KPOP, Genre.JPOP, Genre.BAND)

        genres.forEachIndexed { index, genre ->
            val data =
                LyricsData(
                    id = index.toLong() + 1,
                    title = "Song $index",
                    artist = "Artist $index",
                    genre = genre,
                    lyrics = "lyrics $index",
                )

            // when
            val dto = LyricsListMapper.toItemDTO(data)

            // then
            assertEquals(genre, dto.genre)
        }
    }
}
