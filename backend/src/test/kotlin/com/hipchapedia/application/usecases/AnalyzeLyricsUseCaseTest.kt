package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.Genre
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
            val genre = Genre.HIPHOP
            val artist = "Test Artist"
            val analysisResult = "# Analysis Result\n\nTest analysis"

            coEvery { lyricsRepository.getByHash(any()) } returns null
            coEvery { lyricsRepository.save(any(), any(), any(), any(), any()) } returns 1L
            coEvery { aiService.analyzeLyrics(title, lyrics, genre) } returns analysisResult
            coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

            // when
            val result = useCase.execute(title, lyrics, genre, artist)

            // then
            assertNotNull(result)
            assertEquals(title, result.title)
            assertEquals(lyrics, result.lyrics)
            assertEquals(genre, result.genre)
            assertEquals(analysisResult, result.analysisResult)

            coVerify { lyricsRepository.save(title, any(), lyrics, genre, artist) }
            coVerify { aiService.analyzeLyrics(title, lyrics, genre) }
            coVerify { lyricsRepository.saveAnalysisResult(1L, analysisResult) }
        }

    @Test
    fun `캐시된 분석 결과가 있으면 AI 서비스를 호출하지 않아야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val genre = Genre.HIPHOP
            val cachedResult = "# Cached Analysis Result"
            val existingId = 1L

            coEvery { lyricsRepository.getByHash(any()) } returns Triple(existingId, title, genre)
            coEvery { lyricsRepository.getAnalysisResultByLyricsId(existingId) } returns cachedResult

            // when
            val result = useCase.execute(title, lyrics, genre)

            // then
            assertNotNull(result)
            assertEquals(title, result.title)
            assertEquals(genre, result.genre)
            assertEquals(cachedResult, result.analysisResult)

            coVerify(exactly = 0) { aiService.analyzeLyrics(any(), any(), any()) }
            coVerify(exactly = 0) { lyricsRepository.saveAnalysisResult(any(), any()) }
        }

    @Test
    fun `기존 가사가 있지만 분석 결과가 없으면 새로 분석해야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val genre = Genre.RNB
            val existingId = 1L
            val analysisResult = "# New Analysis Result"

            coEvery { lyricsRepository.getByHash(any()) } returns Triple(existingId, title, genre)
            coEvery { lyricsRepository.getAnalysisResultByLyricsId(existingId) } returns null
            coEvery { aiService.analyzeLyrics(title, lyrics, genre) } returns analysisResult
            coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

            // when
            val result = useCase.execute(title, lyrics, genre)

            // then
            assertNotNull(result)
            assertEquals(analysisResult, result.analysisResult)
            assertEquals(genre, result.genre)

            coVerify { aiService.analyzeLyrics(title, lyrics, genre) }
            coVerify { lyricsRepository.saveAnalysisResult(existingId, analysisResult) }
            coVerify(exactly = 0) { lyricsRepository.save(any(), any(), any(), any(), any()) }
        }

    @Test
    fun `모든 장르 타입에 대해 정상적으로 동작해야 한다`() =
        runTest {
            // given
            val genres = listOf(Genre.HIPHOP, Genre.RNB, Genre.KPOP, Genre.JPOP, Genre.BAND)

            genres.forEach { genre ->
                val title = "Test Song - $genre"
                val lyrics = "Test lyrics for $genre"
                val analysisResult = "# Analysis for $genre"

                coEvery { lyricsRepository.getByHash(any()) } returns null
                coEvery { lyricsRepository.save(any(), any(), any(), any(), any()) } returns 1L
                coEvery { aiService.analyzeLyrics(title, lyrics, genre) } returns analysisResult
                coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

                // when
                val result = useCase.execute(title, lyrics, genre)

                // then
                assertEquals(genre, result.genre)
            }
        }

    @Test
    fun `artist 정보와 함께 가사를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val genre = Genre.HIPHOP
            val artist = "Test Artist"
            val analysisResult = "# Analysis Result"

            coEvery { lyricsRepository.getByHash(any()) } returns null
            coEvery { lyricsRepository.save(any(), any(), any(), any(), any()) } returns 1L
            coEvery { aiService.analyzeLyrics(title, lyrics, genre) } returns analysisResult
            coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

            // when
            val result = useCase.execute(title, lyrics, genre, artist)

            // then
            assertNotNull(result)
            coVerify { lyricsRepository.save(title, any(), lyrics, genre, artist) }
        }

    @Test
    fun `artist 없이도 가사를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyrics = "Test lyrics content"
            val genre = Genre.HIPHOP
            val analysisResult = "# Analysis Result"

            coEvery { lyricsRepository.getByHash(any()) } returns null
            coEvery { lyricsRepository.save(any(), any(), any(), any(), any()) } returns 1L
            coEvery { aiService.analyzeLyrics(title, lyrics, genre) } returns analysisResult
            coEvery { lyricsRepository.saveAnalysisResult(any(), any()) } returns Unit

            // when
            val result = useCase.execute(title, lyrics, genre)

            // then
            assertNotNull(result)
            coVerify { lyricsRepository.save(title, any(), lyrics, genre, null) }
        }
}
