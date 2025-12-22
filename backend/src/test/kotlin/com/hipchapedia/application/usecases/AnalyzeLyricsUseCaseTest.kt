package com.hipchapedia.application.usecases

import com.hipchapedia.domain.interfaces.AIServiceInterface
import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AnalyzeLyricsUseCaseTest {
    private val aiService: AIServiceInterface = mockk()
    private val lyricsRepository: LyricsRepositoryInterface = mockk()
    private val useCase = AnalyzeLyricsUseCase(aiService, lyricsRepository)

    @Test
    fun `새로운 가사를 분석하고 저장해야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val analysisResult = "# Analysis Result\n\nTest analysis"

            coEvery { lyricsRepository.getByHash(any()) } returns null
            coEvery { lyricsRepository.save(any(), any(), any()) } returns 1L
            coEvery { aiService.analyzeLyrics(title, lyrics) } returns analysisResult
            coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

            // when
            val result = useCase.execute(title, lyrics)

            // then
            assertNotNull(result)
            assertEquals(title, result.title)
            assertEquals(lyrics, result.lyrics)
            assertEquals(analysisResult, result.analysisResult)

            coVerify { lyricsRepository.save(title, any(), lyrics) }
            coVerify { aiService.analyzeLyrics(title, lyrics) }
            coVerify { lyricsRepository.saveAnalysisResult(1L, analysisResult) }
        }

    @Test
    fun `캐시된 분석 결과가 있으면 AI 서비스를 호출하지 않아야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val cachedResult = "# Cached Analysis Result"
            val existingId = 1L

            coEvery { lyricsRepository.getByHash(any()) } returns (existingId to title)
            coEvery { lyricsRepository.getAnalysisResultByLyricsId(existingId) } returns cachedResult

            // when
            val result = useCase.execute(title, lyrics)

            // then
            assertNotNull(result)
            assertEquals(title, result.title)
            assertEquals(cachedResult, result.analysisResult)

            coVerify(exactly = 0) { aiService.analyzeLyrics(any(), any()) }
            coVerify(exactly = 0) { lyricsRepository.saveAnalysisResult(any(), any()) }
        }

    @Test
    fun `기존 가사가 있지만 분석 결과가 없으면 새로 분석해야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val existingId = 1L
            val analysisResult = "# New Analysis Result"

            coEvery { lyricsRepository.getByHash(any()) } returns (existingId to title)
            coEvery { lyricsRepository.getAnalysisResultByLyricsId(existingId) } returns null
            coEvery { aiService.analyzeLyrics(title, lyrics) } returns analysisResult
            coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

            // when
            val result = useCase.execute(title, lyrics)

            // then
            assertNotNull(result)
            assertEquals(analysisResult, result.analysisResult)

            coVerify { aiService.analyzeLyrics(title, lyrics) }
            coVerify { lyricsRepository.saveAnalysisResult(existingId, analysisResult) }
            coVerify(exactly = 0) { lyricsRepository.save(any(), any(), any()) }
        }
}
