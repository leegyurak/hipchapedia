package com.hipchapedia.infrastructure.db.repositories

import com.hipchapedia.infrastructure.db.models.LyricsAnalysisResultEntity
import com.hipchapedia.infrastructure.db.models.LyricsEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
                )

            every { lyricsJpaRepository.findByLyricsHash(lyricsHash) } returns entity

            // when
            val result = repository.getByHash(lyricsHash)

            // then
            assertNotNull(result)
            assertEquals(1L, result.first)
            assertEquals("Test Song", result.second)
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
    fun `분석 결과가 없으면 null을 반환해야 한다`() =
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

            val savedEntity =
                LyricsEntity(
                    id = 1L,
                    title = title,
                    lyricsHash = lyricsHash,
                    originalLyrics = originalLyrics,
                )

            every { lyricsJpaRepository.save(any<LyricsEntity>()) } returns savedEntity

            // when
            val result = repository.save(title, lyricsHash, originalLyrics)

            // then
            assertEquals(1L, result)
            verify { lyricsJpaRepository.save(any<LyricsEntity>()) }
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
}
