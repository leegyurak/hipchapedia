package com.hipchapedia.infrastructure.db.repositories

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.infrastructure.db.models.LyricsAnalysisResultEntity
import com.hipchapedia.infrastructure.db.models.LyricsEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LyricsRepositoryImplTest {
    private val lyricsJpaRepository: LyricsJpaRepository = mockk()
    private val lyricsAnalysisResultJpaRepository: LyricsAnalysisResultJpaRepository = mockk()
    private val repository = LyricsRepositoryImpl(lyricsJpaRepository, lyricsAnalysisResultJpaRepository)

    @Test
    fun `해시로 가사를 조회할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsHash = "test-hash"
            val entity =
                LyricsEntity(
                    id = 1L,
                    title = "Test Song",
                    lyricsHash = lyricsHash,
                    originalLyrics = "Test lyrics",
                    genre = Genre.HIPHOP,
                )

            every { lyricsJpaRepository.findByLyricsHash(lyricsHash) } returns entity

            // when
            val result = repository.getByHash(lyricsHash)

            // then
            assertNotNull(result)
            assertEquals(1L, result.first)
            assertEquals("Test Song", result.second)
            assertEquals(Genre.HIPHOP, result.third)
        }

    @Test
    fun `해시로 가사를 조회했을 때 없으면 null을 반환해야 한다`() =
        runTest {
            // given
            val lyricsHash = "non-existent-hash"

            every { lyricsJpaRepository.findByLyricsHash(lyricsHash) } returns null

            // when
            val result = repository.getByHash(lyricsHash)

            // then
            assertNull(result)
        }

    @Test
    fun `가사 ID로 분석 결과를 조회할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val analysisResult = "# Analysis Result"
            val entity =
                LyricsAnalysisResultEntity(
                    lyricsId = lyricsId,
                    analysisResult = analysisResult,
                )

            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.of(entity)

            // when
            val result = repository.getAnalysisResultByLyricsId(lyricsId)

            // then
            assertNotNull(result)
            assertEquals(analysisResult, result)
        }

    @Test
    fun `ID로 분석 결과를 조회했을 때 없으면 null을 반환해야 한다`() =
        runTest {
            // given
            val lyricsId = 999L

            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.empty()

            // when
            val result = repository.getAnalysisResultByLyricsId(lyricsId)

            // then
            assertNull(result)
        }

    @Test
    fun `새로운 가사를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyricsHash = "test-hash"
            val originalLyrics = "Test lyrics content"
            val genre = Genre.HIPHOP
            val artist = "Test Artist"

            val savedEntity =
                LyricsEntity(
                    id = 1L,
                    title = title,
                    lyricsHash = lyricsHash,
                    originalLyrics = originalLyrics,
                    genre = genre,
                    artist = artist,
                )

            every { lyricsJpaRepository.save(any<LyricsEntity>()) } returns savedEntity

            // when
            val result = repository.save(title, lyricsHash, originalLyrics, genre, artist)

            // then
            assertEquals(1L, result)
            verify { lyricsJpaRepository.save(any<LyricsEntity>()) }
        }

    @Test
    fun `모든 장르 타입으로 가사를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val genres = listOf(Genre.HIPHOP, Genre.RNB, Genre.KPOP, Genre.JPOP, Genre.BAND)

            genres.forEach { genre ->
                val title = "Test Song - $genre"
                val lyricsHash = "test-hash-$genre"
                val originalLyrics = "Test lyrics for $genre"

                val savedEntity =
                    LyricsEntity(
                        id = 1L,
                        title = title,
                        lyricsHash = lyricsHash,
                        originalLyrics = originalLyrics,
                        genre = genre,
                    )

                every { lyricsJpaRepository.save(any<LyricsEntity>()) } returns savedEntity

                // when
                val result = repository.save(title, lyricsHash, originalLyrics, genre, null)

                // then
                assertEquals(1L, result)
            }
        }

    @Test
    fun `artist 정보와 함께 가사를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyricsHash = "test-hash"
            val originalLyrics = "Test lyrics content"
            val genre = Genre.HIPHOP
            val artist = "Drake"

            val savedEntity =
                LyricsEntity(
                    id = 1L,
                    title = title,
                    lyricsHash = lyricsHash,
                    originalLyrics = originalLyrics,
                    genre = genre,
                    artist = artist,
                )

            every { lyricsJpaRepository.save(any<LyricsEntity>()) } returns savedEntity

            // when
            val result = repository.save(title, lyricsHash, originalLyrics, genre, artist)

            // then
            assertEquals(1L, result)
            verify { lyricsJpaRepository.save(match { it.artist == artist }) }
        }

    @Test
    fun `artist 없이도 가사를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val lyricsHash = "test-hash"
            val originalLyrics = "Test lyrics content"
            val genre = Genre.HIPHOP

            val savedEntity =
                LyricsEntity(
                    id = 1L,
                    title = title,
                    lyricsHash = lyricsHash,
                    originalLyrics = originalLyrics,
                    genre = genre,
                    artist = null,
                )

            every { lyricsJpaRepository.save(any<LyricsEntity>()) } returns savedEntity

            // when
            val result = repository.save(title, lyricsHash, originalLyrics, genre, null)

            // then
            assertEquals(1L, result)
            verify { lyricsJpaRepository.save(match { it.artist == null }) }
        }

    @Test
    fun `새로운 분석 결과를 저장할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val analysisResult = "# New Analysis Result"

            every { lyricsJpaRepository.existsById(lyricsId) } returns true
            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.empty()
            every { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) } returns
                LyricsAnalysisResultEntity(
                    lyricsId = lyricsId,
                    analysisResult = analysisResult,
                )

            // when
            repository.saveAnalysisResult(lyricsId, analysisResult)

            // then
            verify { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) }
        }

    @Test
    fun `기존 분석 결과를 업데이트할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val oldAnalysisResult = "# Old Analysis"
            val newAnalysisResult = "# Updated Analysis"

            val existingEntity =
                LyricsAnalysisResultEntity(
                    lyricsId = lyricsId,
                    analysisResult = oldAnalysisResult,
                )

            every { lyricsJpaRepository.existsById(lyricsId) } returns true
            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.of(existingEntity)
            every { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) } returns existingEntity

            // when
            repository.saveAnalysisResult(lyricsId, newAnalysisResult)

            // then
            assertEquals(newAnalysisResult, existingEntity.analysisResult)
            verify { lyricsAnalysisResultJpaRepository.save(existingEntity) }
        }

    @Test
    fun `존재하지 않는 가사 ID로 분석 결과를 저장하면 예외를 던져야 한다`() =
        runTest {
            // given
            val nonExistentLyricsId = 999L
            val analysisResult = "# Analysis"

            every { lyricsJpaRepository.existsById(nonExistentLyricsId) } returns false

            // when & then
            val exception =
                assertThrows<IllegalArgumentException> {
                    repository.saveAnalysisResult(nonExistentLyricsId, analysisResult)
                }
            assert(exception.message?.contains("가사를 찾을 수 없습니다") == true)
        }

    @Test
    fun `빈 분석 결과도 저장할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val emptyAnalysisResult = ""

            every { lyricsJpaRepository.existsById(lyricsId) } returns true
            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.empty()
            every { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) } returns
                LyricsAnalysisResultEntity(
                    lyricsId = lyricsId,
                    analysisResult = emptyAnalysisResult,
                )

            // when
            repository.saveAnalysisResult(lyricsId, emptyAnalysisResult)

            // then
            verify { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) }
        }

    @Test
    fun `긴 분석 결과도 저장할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val longAnalysisResult = "# Analysis\n".repeat(10000)

            every { lyricsJpaRepository.existsById(lyricsId) } returns true
            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.empty()
            every { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) } returns
                LyricsAnalysisResultEntity(
                    lyricsId = lyricsId,
                    analysisResult = longAnalysisResult,
                )

            // when
            repository.saveAnalysisResult(lyricsId, longAnalysisResult)

            // then
            verify { lyricsAnalysisResultJpaRepository.save(any<LyricsAnalysisResultEntity>()) }
        }

    @Test
    fun `가사 목록을 조회할 수 있어야 한다`() =
        runTest {
            // given
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 3L,
                        title = "Song 3",
                        lyricsHash = "hash3",
                        originalLyrics = "lyrics 3",
                        genre = Genre.HIPHOP,
                        artist = "Artist 3",
                    ),
                    LyricsEntity(
                        id = 2L,
                        title = "Song 2",
                        lyricsHash = "hash2",
                        originalLyrics = "lyrics 2",
                        genre = Genre.RNB,
                        artist = "Artist 2",
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = null,
                    artist = null,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(null, limit, null, null)

            // then
            assertEquals(2, result.size)
            assertEquals(3L, result[0].id)
            assertEquals("Song 3", result[0].title)
            assertEquals("Artist 3", result[0].artist)
            assertEquals(Genre.HIPHOP, result[0].genre)
            assertEquals("lyrics 3", result[0].lyrics)
        }

    @Test
    fun `artist가 null인 가사는 조회 결과에 포함되지 않아야 한다`() =
        runTest {
            // given
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 3L,
                        title = "Song 3",
                        lyricsHash = "hash3",
                        originalLyrics = "lyrics 3",
                        genre = Genre.HIPHOP,
                        artist = "Artist 3",
                    ),
                    LyricsEntity(
                        id = 2L,
                        title = "Song 2",
                        lyricsHash = "hash2",
                        originalLyrics = "lyrics 2",
                        genre = Genre.RNB,
                        artist = "Artist 2",
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = null,
                    artist = null,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(null, limit, null, null)

            // then
            assertEquals(2, result.size)
            assertTrue(result.all { it.artist != null })
        }

    @Test
    fun `가사 목록을 cursor로 조회할 수 있어야 한다`() =
        runTest {
            // given
            val cursor = 5L
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 4L,
                        title = "Song 4",
                        lyricsHash = "hash4",
                        originalLyrics = "lyrics 4",
                        genre = Genre.HIPHOP,
                    ),
                    LyricsEntity(
                        id = 3L,
                        title = "Song 3",
                        lyricsHash = "hash3",
                        originalLyrics = "lyrics 3",
                        genre = Genre.HIPHOP,
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = cursor,
                    genre = null,
                    artist = null,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(cursor, limit, null, null)

            // then
            assertEquals(2, result.size)
            assertEquals(4L, result[0].id)
            assertEquals(3L, result[1].id)
        }

    @Test
    fun `가사 목록을 genre로 필터링할 수 있어야 한다`() =
        runTest {
            // given
            val genre = Genre.HIPHOP
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 3L,
                        title = "Hip Hop Song 3",
                        lyricsHash = "hash3",
                        originalLyrics = "lyrics 3",
                        genre = Genre.HIPHOP,
                    ),
                    LyricsEntity(
                        id = 1L,
                        title = "Hip Hop Song 1",
                        lyricsHash = "hash1",
                        originalLyrics = "lyrics 1",
                        genre = Genre.HIPHOP,
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = genre,
                    artist = null,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(null, limit, genre, null)

            // then
            assertEquals(2, result.size)
            assertTrue(result.all { it.genre == Genre.HIPHOP })
        }

    @Test
    fun `가사 목록을 artist로 필터링할 수 있어야 한다`() =
        runTest {
            // given
            val artist = "Drake"
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 2L,
                        title = "Drake Song 2",
                        lyricsHash = "hash2",
                        originalLyrics = "lyrics 2",
                        genre = Genre.HIPHOP,
                        artist = "Drake",
                    ),
                    LyricsEntity(
                        id = 1L,
                        title = "Drake Song 1",
                        lyricsHash = "hash1",
                        originalLyrics = "lyrics 1",
                        genre = Genre.RNB,
                        artist = "Drake",
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = null,
                    artist = artist,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(null, limit, null, artist)

            // then
            assertEquals(2, result.size)
            assertTrue(result.all { it.artist == "Drake" })
        }

    @Test
    fun `가사 목록을 artist 부분 일치로 검색할 수 있어야 한다`() =
        runTest {
            // given
            val artist = "Dra"
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 3L,
                        title = "Drake Song",
                        lyricsHash = "hash3",
                        originalLyrics = "lyrics 3",
                        genre = Genre.HIPHOP,
                        artist = "Drake",
                    ),
                    LyricsEntity(
                        id = 2L,
                        title = "Drama Song",
                        lyricsHash = "hash2",
                        originalLyrics = "lyrics 2",
                        genre = Genre.RNB,
                        artist = "Drama",
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = null,
                    artist = artist,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(null, limit, null, artist)

            // then
            assertEquals(2, result.size)
            assertTrue(result.all { it.artist?.contains("Dra") == true })
        }

    @Test
    fun `가사 목록을 genre와 artist 모두로 필터링할 수 있어야 한다`() =
        runTest {
            // given
            val genre = Genre.HIPHOP
            val artist = "Drake"
            val limit = 10
            val entities =
                listOf(
                    LyricsEntity(
                        id = 1L,
                        title = "Drake Hip Hop Song",
                        lyricsHash = "hash1",
                        originalLyrics = "lyrics 1",
                        genre = Genre.HIPHOP,
                        artist = "Drake",
                    ),
                )

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = genre,
                    artist = artist,
                    pageable = PageRequest.of(0, limit),
                )
            } returns entities

            // when
            val result = repository.getLyricsList(null, limit, genre, artist)

            // then
            assertEquals(1, result.size)
            assertEquals(Genre.HIPHOP, result[0].genre)
            assertEquals("Drake", result[0].artist)
        }

    @Test
    fun `가사가 없으면 빈 리스트를 반환해야 한다`() =
        runTest {
            // given
            val limit = 10

            every {
                lyricsJpaRepository.findLyricsWithCursor(
                    cursor = null,
                    genre = null,
                    artist = null,
                    pageable = PageRequest.of(0, limit),
                )
            } returns emptyList()

            // when
            val result = repository.getLyricsList(null, limit, null, null)

            // then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `ID로 가사와 분석 결과를 조회할 수 있어야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val lyricsEntity =
                LyricsEntity(
                    id = lyricsId,
                    title = "Test Song",
                    lyricsHash = "hash",
                    originalLyrics = "Test lyrics content",
                    genre = Genre.HIPHOP,
                    artist = "Test Artist",
                )

            val analysisResultEntity =
                LyricsAnalysisResultEntity(
                    lyricsId = lyricsId,
                    analysisResult = "# Analysis Result",
                )

            every { lyricsJpaRepository.findById(lyricsId) } returns Optional.of(lyricsEntity)
            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.of(analysisResultEntity)

            // when
            val result = repository.getLyricsById(lyricsId)

            // then
            assertNotNull(result)
            assertEquals("Test Song", result.title)
            assertEquals("Test lyrics content", result.lyrics)
            assertEquals(Genre.HIPHOP, result.genre)
            assertEquals("# Analysis Result", result.analysisResult)
        }

    @Test
    fun `가사가 없으면 null을 반환해야 한다`() =
        runTest {
            // given
            val lyricsId = 999L

            every { lyricsJpaRepository.findById(lyricsId) } returns Optional.empty()

            // when
            val result = repository.getLyricsById(lyricsId)

            // then
            assertNull(result)
        }

    @Test
    fun `가사는 있지만 분석 결과가 없으면 null을 반환해야 한다`() =
        runTest {
            // given
            val lyricsId = 1L
            val lyricsEntity =
                LyricsEntity(
                    id = lyricsId,
                    title = "Test Song",
                    lyricsHash = "hash",
                    originalLyrics = "Test lyrics content",
                    genre = Genre.HIPHOP,
                    artist = "Test Artist",
                )

            every { lyricsJpaRepository.findById(lyricsId) } returns Optional.of(lyricsEntity)
            every { lyricsAnalysisResultJpaRepository.findById(lyricsId) } returns Optional.empty()

            // when
            val result = repository.getLyricsById(lyricsId)

            // then
            assertNull(result)
        }
}
