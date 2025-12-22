package com.hipchapedia.application.mappers

import com.hipchapedia.domain.entities.LyricsAnalysis
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class LyricsAnalysisMapperTest {
    @Test
    fun `LyricsAnalysis를 LyricsAnalysisResponseDTO로 변환해야 한다`() {
        // given
        val entity =
            LyricsAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics content",
                analysisResult = "# Analysis Result\n\nTest analysis",
                id = 1L,
            )

        // when
        val dto = LyricsAnalysisMapper.toResponseDTO(entity)

        // then
        assertEquals("Test Song", dto.title)
        assertEquals("Test lyrics content", dto.lyrics)
        assertEquals("# Analysis Result\n\nTest analysis", dto.analysisResult)
    }

    @Test
    fun `analysisResult가 null이면 IllegalStateException을 던져야 한다`() {
        // given
        val entity =
            LyricsAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics content",
                analysisResult = null,
                id = 1L,
            )

        // when & then
        val exception =
            assertThrows<IllegalStateException> {
                LyricsAnalysisMapper.toResponseDTO(entity)
            }
        assertEquals("분석 결과가 없습니다.", exception.message)
    }

    @Test
    fun `빈 문자열인 analysisResult도 정상적으로 변환해야 한다`() {
        // given
        val entity =
            LyricsAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics",
                analysisResult = "",
                id = 1L,
            )

        // when
        val dto = LyricsAnalysisMapper.toResponseDTO(entity)

        // then
        assertEquals("", dto.analysisResult)
    }

    @Test
    fun `긴 analysisResult도 정상적으로 변환해야 한다`() {
        // given
        val longAnalysis = "# Analysis\n".repeat(1000)
        val entity =
            LyricsAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics",
                analysisResult = longAnalysis,
                id = 1L,
            )

        // when
        val dto = LyricsAnalysisMapper.toResponseDTO(entity)

        // then
        assertEquals(longAnalysis, dto.analysisResult)
    }
}
