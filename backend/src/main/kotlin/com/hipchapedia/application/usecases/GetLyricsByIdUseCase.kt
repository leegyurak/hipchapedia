package com.hipchapedia.application.usecases

import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import com.hipchapedia.domain.interfaces.LyricsWithAnalysis
import org.springframework.stereotype.Component

/**
 * ID로 가사 조회 Use Case
 */
@Component
class GetLyricsByIdUseCase(
    private val lyricsRepository: LyricsRepositoryInterface,
) {
    /**
     * ID로 가사와 분석 결과를 조회합니다.
     *
     * @param id 가사 ID
     * @return 가사와 분석 결과 또는 null
     */
    suspend fun execute(id: Long): LyricsWithAnalysis? = lyricsRepository.getLyricsById(id)
}
