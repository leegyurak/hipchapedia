package com.hipchapedia.infrastructure.db.repositories

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import com.hipchapedia.infrastructure.db.models.LyricsAnalysisResultEntity
import com.hipchapedia.infrastructure.db.models.LyricsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository

/**
 * 가사 저장소 구현
 */
@Repository
class LyricsRepositoryImpl(
    private val lyricsJpaRepository: LyricsJpaRepository,
    private val lyricsAnalysisResultJpaRepository: LyricsAnalysisResultJpaRepository,
) : LyricsRepositoryInterface {
    override suspend fun getByHash(lyricsHash: String): Triple<Long, String, Genre>? =
        withContext(Dispatchers.IO) {
            lyricsJpaRepository.findByLyricsHash(lyricsHash)?.let {
                Triple(it.id!!, it.title, it.genre)
            }
        }

    override suspend fun getAnalysisResultByLyricsId(lyricsId: Long): String? =
        withContext(Dispatchers.IO) {
            lyricsAnalysisResultJpaRepository
                .findById(lyricsId)
                .map { it.analysisResult }
                .orElse(null)
        }

    override suspend fun save(
        title: String,
        lyricsHash: String,
        originalLyrics: String,
        genre: Genre,
    ): Long =
        withContext(Dispatchers.IO) {
            val entity =
                LyricsEntity(
                    title = title,
                    lyricsHash = lyricsHash,
                    originalLyrics = originalLyrics,
                    genre = genre,
                )
            lyricsJpaRepository.save(entity).id!!
        }

    override suspend fun saveAnalysisResult(
        lyricsId: Long,
        analysisResult: String,
    ) {
        withContext(Dispatchers.IO) {
            // lyricsId가 존재하는지 확인
            if (!lyricsJpaRepository.existsById(lyricsId)) {
                throw IllegalArgumentException("가사를 찾을 수 없습니다: $lyricsId")
            }

            // 이미 분석 결과가 존재하는지 확인
            val existingResult = lyricsAnalysisResultJpaRepository.findById(lyricsId)

            if (existingResult.isPresent) {
                // 이미 존재하면 업데이트
                val entity = existingResult.get()
                entity.analysisResult = analysisResult
                lyricsAnalysisResultJpaRepository.save(entity)
            } else {
                // 존재하지 않으면 새로 생성
                val resultEntity =
                    LyricsAnalysisResultEntity(
                        lyricsId = lyricsId,
                        analysisResult = analysisResult,
                    )
                lyricsAnalysisResultJpaRepository.save(resultEntity)
            }
        }
    }
}
