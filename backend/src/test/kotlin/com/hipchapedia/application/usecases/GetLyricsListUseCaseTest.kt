package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.LyricsData
import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetLyricsListUseCaseTest {
    private val lyricsRepository: LyricsRepositoryInterface = mockk()
    private val useCase = GetLyricsListUseCase(lyricsRepository)

    @Test
    fun `가사 목록을 조회할 수 있어야 한다`() =
        runTest {
            // given
            val lyrics =
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
                    LyricsData(
                        id = 1L,
                        title = "Song 1",
                        artist = null,
                        genre = Genre.KPOP,
                        lyrics = "lyrics 1",
                    ),
                )

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = null,
                    artist = null,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, 20, null, null)

            // then
            assertEquals(3, result.size)
            assertNull(nextCursor)
            assertFalse(hasMore)
        }

    @Test
    fun `페이지 크기보다 많은 데이터가 있으면 hasMore가 true여야 한다`() =
        runTest {
            // given
            val limit = 20
            val lyrics =
                (1..21).map { id ->
                    LyricsData(
                        id = id.toLong(),
                        title = "Song $id",
                        artist = "Artist $id",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics $id",
                    )
                }

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = null,
                    artist = null,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, limit, null, null)

            // then
            assertEquals(20, result.size)
            assertEquals(20L, nextCursor)
            assertTrue(hasMore)
        }

    @Test
    fun `cursor를 사용하여 다음 페이지를 조회할 수 있어야 한다`() =
        runTest {
            // given
            val cursor = 20L
            val limit = 20
            val lyrics =
                listOf(
                    LyricsData(
                        id = 19L,
                        title = "Song 19",
                        artist = "Artist 19",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 19",
                    ),
                    LyricsData(
                        id = 18L,
                        title = "Song 18",
                        artist = "Artist 18",
                        genre = Genre.RNB,
                        lyrics = "lyrics 18",
                    ),
                )

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = cursor,
                    limit = 21,
                    genre = null,
                    artist = null,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(cursor, limit, null, null)

            // then
            assertEquals(2, result.size)
            assertEquals(19L, result[0].id)
            assertEquals(18L, result[1].id)
            assertNull(nextCursor)
            assertFalse(hasMore)
        }

    @Test
    fun `genre로 필터링하여 조회할 수 있어야 한다`() =
        runTest {
            // given
            val genre = Genre.HIPHOP
            val lyrics =
                listOf(
                    LyricsData(
                        id = 3L,
                        title = "Hip Hop Song 3",
                        artist = "Artist 3",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 3",
                    ),
                    LyricsData(
                        id = 1L,
                        title = "Hip Hop Song 1",
                        artist = "Artist 1",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 1",
                    ),
                )

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = genre,
                    artist = null,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, 20, genre, null)

            // then
            assertEquals(2, result.size)
            assertTrue(result.all { it.genre == Genre.HIPHOP })
            assertNull(nextCursor)
            assertFalse(hasMore)
        }

    @Test
    fun `artist로 필터링하여 조회할 수 있어야 한다`() =
        runTest {
            // given
            val artist = "Drake"
            val lyrics =
                listOf(
                    LyricsData(
                        id = 2L,
                        title = "Drake Song 2",
                        artist = "Drake",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 2",
                    ),
                    LyricsData(
                        id = 1L,
                        title = "Drake Song 1",
                        artist = "Drake",
                        genre = Genre.RNB,
                        lyrics = "lyrics 1",
                    ),
                )

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = null,
                    artist = artist,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, 20, null, artist)

            // then
            assertEquals(2, result.size)
            assertTrue(result.all { it.artist == "Drake" })
            assertNull(nextCursor)
            assertFalse(hasMore)
        }

    @Test
    fun `genre와 artist 모두로 필터링할 수 있어야 한다`() =
        runTest {
            // given
            val genre = Genre.HIPHOP
            val artist = "Drake"
            val lyrics =
                listOf(
                    LyricsData(
                        id = 1L,
                        title = "Drake Hip Hop Song",
                        artist = "Drake",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 1",
                    ),
                )

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = genre,
                    artist = artist,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, 20, genre, artist)

            // then
            assertEquals(1, result.size)
            assertEquals(Genre.HIPHOP, result[0].genre)
            assertEquals("Drake", result[0].artist)
            assertNull(nextCursor)
            assertFalse(hasMore)
        }

    @Test
    fun `결과가 없으면 빈 리스트를 반환해야 한다`() =
        runTest {
            // given
            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = null,
                    artist = null,
                )
            } returns emptyList()

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, 20, null, null)

            // then
            assertTrue(result.isEmpty())
            assertNull(nextCursor)
            assertFalse(hasMore)
        }

    @Test
    fun `limit이 1인 경우도 정상 동작해야 한다`() =
        runTest {
            // given
            val lyrics =
                listOf(
                    LyricsData(
                        id = 2L,
                        title = "Song 2",
                        artist = "Artist 2",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 2",
                    ),
                    LyricsData(
                        id = 1L,
                        title = "Song 1",
                        artist = "Artist 1",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics 1",
                    ),
                )

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 2,
                    genre = null,
                    artist = null,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, 1, null, null)

            // then
            assertEquals(1, result.size)
            assertEquals(2L, result[0].id)
            assertEquals(2L, nextCursor)
            assertTrue(hasMore)
        }

    @Test
    fun `정확히 페이지 크기만큼의 데이터가 있으면 hasMore가 false여야 한다`() =
        runTest {
            // given
            val limit = 20
            val lyrics =
                (1..20).map { id ->
                    LyricsData(
                        id = id.toLong(),
                        title = "Song $id",
                        artist = "Artist $id",
                        genre = Genre.HIPHOP,
                        lyrics = "lyrics $id",
                    )
                }

            coEvery {
                lyricsRepository.getLyricsList(
                    cursor = null,
                    limit = 21,
                    genre = null,
                    artist = null,
                )
            } returns lyrics

            // when
            val (result, nextCursor, hasMore) = useCase.execute(null, limit, null, null)

            // then
            assertEquals(20, result.size)
            assertNull(nextCursor)
            assertFalse(hasMore)
        }
}
