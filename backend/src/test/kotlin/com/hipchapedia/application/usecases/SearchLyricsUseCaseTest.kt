package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.LyricsSearchResult
import com.hipchapedia.domain.interfaces.LyricsSearchRepositoryInterface
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SearchLyricsUseCaseTest {
    private val lyricsSearchRepository: LyricsSearchRepositoryInterface = mockk()
    private val useCase = SearchLyricsUseCase(lyricsSearchRepository)

    @Test
    fun `가사 검색이 성공하면 결과를 반환해야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val artist = "Test Artist"
            val searchResult =
                LyricsSearchResult(
                    title = title,
                    artist = artist,
                    lyrics = "Test lyrics content",
                    url = "https://genius.com/test-song",
                    album = "Test Album",
                    releaseDate = "2024-01-01",
                )

            coEvery { lyricsSearchRepository.publishSearchRequest(title, artist) } returns Unit
            coEvery { lyricsSearchRepository.waitForResult(title, artist, any()) } returns searchResult

            // when
            val result = useCase.execute(title, artist)

            // then
            assertNotNull(result)
            assertEquals(title, result.title)
            assertEquals(artist, result.artist)
            assertEquals("Test lyrics content", result.lyrics)
            assertEquals("https://genius.com/test-song", result.url)
            assertEquals("Test Album", result.album)
            assertEquals("2024-01-01", result.releaseDate)

            coVerify { lyricsSearchRepository.publishSearchRequest(title, artist) }
            coVerify { lyricsSearchRepository.waitForResult(title, artist, any()) }
        }

    @Test
    fun `검색 결과가 없으면 null을 반환해야 한다`() =
        runTest {
            // given
            val title = "Unknown Song"
            val artist = "Unknown Artist"

            coEvery { lyricsSearchRepository.publishSearchRequest(title, artist) } returns Unit
            coEvery { lyricsSearchRepository.waitForResult(title, artist, any()) } returns null

            // when
            val result = useCase.execute(title, artist)

            // then
            assertNull(result)

            coVerify { lyricsSearchRepository.publishSearchRequest(title, artist) }
            coVerify { lyricsSearchRepository.waitForResult(title, artist, any()) }
        }

    @Test
    fun `타임아웃 발생 시 null을 반환해야 한다`() =
        runTest {
            // given
            val title = "Timeout Song"
            val artist = "Timeout Artist"

            coEvery { lyricsSearchRepository.publishSearchRequest(title, artist) } returns Unit
            coEvery { lyricsSearchRepository.waitForResult(title, artist, any()) } returns null

            // when
            val result = useCase.execute(title, artist)

            // then
            assertNull(result)

            coVerify { lyricsSearchRepository.publishSearchRequest(title, artist) }
            coVerify { lyricsSearchRepository.waitForResult(title, artist, any()) }
        }

    @Test
    fun `검색 요청이 항상 발행되어야 한다`() =
        runTest {
            // given
            val title = "Request Song"
            val artist = "Request Artist"

            coEvery { lyricsSearchRepository.publishSearchRequest(title, artist) } returns Unit
            coEvery { lyricsSearchRepository.waitForResult(title, artist, any()) } returns null

            // when
            useCase.execute(title, artist)

            // then
            coVerify(exactly = 1) { lyricsSearchRepository.publishSearchRequest(title, artist) }
            coVerify(exactly = 1) { lyricsSearchRepository.waitForResult(title, artist, any()) }
        }
}
