package com.hipchapedia.application.mappers

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.LyricsWithAnalysis
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LyricsByIdMapperTest {
    @Test
    fun `LyricsWithAnalysis를 LyricsAnalysisResponseDTO로 변환해야 한다`() {
        // given
        val data =
            LyricsWithAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics content",
                genre = Genre.HIPHOP,
                analysisResult = "# Analysis Result\n\nTest analysis",
            )

        // when
        val dto = LyricsByIdMapper.toResponseDTO(data)

        // then
        assertEquals("Test Song", dto.title)
        assertEquals("Test lyrics content", dto.lyrics)
        assertEquals(Genre.HIPHOP, dto.genre)
        assertEquals("# Analysis Result\n\nTest analysis", dto.analysisResult)
    }

    @Test
    fun `빈 문자열인 analysisResult도 정상적으로 변환해야 한다`() {
        // given
        val data =
            LyricsWithAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics",
                genre = Genre.RNB,
                analysisResult = "",
            )

        // when
        val dto = LyricsByIdMapper.toResponseDTO(data)

        // then
        assertEquals("", dto.analysisResult)
        assertEquals(Genre.RNB, dto.genre)
    }

    @Test
    fun `긴 analysisResult도 정상적으로 변환해야 한다`() {
        // given
        val longAnalysis = "# Analysis\n".repeat(1000)
        val data =
            LyricsWithAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics",
                genre = Genre.KPOP,
                analysisResult = longAnalysis,
            )

        // when
        val dto = LyricsByIdMapper.toResponseDTO(data)

        // then
        assertEquals(longAnalysis, dto.analysisResult)
        assertEquals(Genre.KPOP, dto.genre)
    }

    @Test
    fun `모든 장르 타입에 대해 정상적으로 변환해야 한다`() {
        // given
        val genres = listOf(Genre.HIPHOP, Genre.RNB, Genre.KPOP, Genre.JPOP, Genre.BAND)

        genres.forEach { genre ->
            val data =
                LyricsWithAnalysis(
                    title = "Test Song",
                    lyrics = "Test lyrics",
                    genre = genre,
                    analysisResult = "# Analysis",
                )

            // when
            val dto = LyricsByIdMapper.toResponseDTO(data)

            // then
            assertEquals(genre, dto.genre)
        }
    }

    @Test
    fun `긴 가사 내용도 정상적으로 변환해야 한다`() {
        // given
        val longLyrics = "Verse line\n".repeat(1000)
        val data =
            LyricsWithAnalysis(
                title = "Long Song",
                lyrics = longLyrics,
                genre = Genre.JPOP,
                analysisResult = "# Analysis",
            )

        // when
        val dto = LyricsByIdMapper.toResponseDTO(data)

        // then
        assertEquals(longLyrics, dto.lyrics)
    }
}
