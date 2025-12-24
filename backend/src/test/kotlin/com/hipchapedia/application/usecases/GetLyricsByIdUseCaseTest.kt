package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import com.hipchapedia.domain.interfaces.LyricsWithAnalysis
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetLyricsByIdUseCaseTest {
    private val lyricsRepository: LyricsRepositoryInterface = mockk()
    private val useCase = GetLyricsByIdUseCase(lyricsRepository)

    @Test
    fun `ID로 가사와 분석 결과를 조회할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val lyricsWithAnalysis =
                LyricsWithAnalysis(
                    title = "Test Song",
                    lyrics = "Test lyrics content",
                    genre = Genre.HIPHOP,
                    analysisResult = "# Analysis Result",
                )

            coEvery { lyricsRepository.getLyricsById(lyricsId) } returns lyricsWithAnalysis

            // when
            val result = useCase.execute(lyricsId)

            // then
            assertNotNull(result)
            assertEquals("Test Song", result.title)
            assertEquals("Test lyrics content", result.lyrics)
            assertEquals(Genre.HIPHOP, result.genre)
            assertEquals("# Analysis Result", result.analysisResult)
        }

    @Test
    fun `존재하지 않는 ID로 조회하면 null을 반환해야 한다`() =
        runTest {
            // given
            val lyricsId = 999L

            coEvery { lyricsRepository.getLyricsById(lyricsId) } returns null

            // when
            val result = useCase.execute(lyricsId)

            // then
            assertNull(result)
        }

    @Test
    fun `모든 장르 타입의 가사를 조회할 수 있어야 한다`() =
        runTest {
            // given
            val genres = listOf(Genre.HIPHOP, Genre.RNB, Genre.KPOP, Genre.JPOP, Genre.BAND)

            genres.forEachIndexed { index, genre ->
                val lyricsId = index.toLong() + 1
                val lyricsWithAnalysis =
                    LyricsWithAnalysis(
                        title = "Song $index",
                        lyrics = "Lyrics $index",
                        genre = genre,
                        analysisResult = "Analysis $index",
                    )

                coEvery { lyricsRepository.getLyricsById(lyricsId) } returns lyricsWithAnalysis

                // when
                val result = useCase.execute(lyricsId)

                // then
                assertNotNull(result)
                assertEquals(genre, result.genre)
            }
        }
}
