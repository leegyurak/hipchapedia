package com.hipchapedia.application.usecases

import com.hipchapedia.domain.entities.LyricsAnalysis
import com.hipchapedia.domain.interfaces.AIServiceInterface
import com.hipchapedia.domain.interfaces.LyricsRepositoryInterface
import com.hipchapedia.domain.utils.generateLyricsHash
import org.springframework.stereotype.Component

/**
 * 가사 분석 Use Case
 */
@Component
class AnalyzeLyricsUseCase(
    private val aiService: AIServiceInterface,
    private val lyricsRepository: LyricsRepositoryInterface,
) {
    /**
     * 가사를 분석합니다.
     *
     * 동일한 가사(해시 기준)가 이미 분석된 경우, 저장된 결과를 반환합니다.
     *
     * @param title 곡 제목
     * @param lyrics 가사 내용
     * @param genre 장르
     * @return 분석 결과가 포함된 LyricsAnalysis 엔티티
     */
    suspend fun execute(
        title: String,
        lyrics: String,
        genre: com.hipchapedia.domain.entities.Genre,
    ): LyricsAnalysis {
        // 가사 해시 생성
        val lyricsHash = generateLyricsHash(lyrics)

        // DB에서 기존 가사 조회
        val existingLyrics = lyricsRepository.getByHash(lyricsHash)

        val lyricsId: Long
        if (existingLyrics != null) {
            // 기존 가사가 있으면 분석 결과 조회
            val (id, existingTitle, existingGenre) = existingLyrics
            val cachedResult = lyricsRepository.getAnalysisResultByLyricsId(id)

            if (cachedResult != null) {
                // 캐시된 분석 결과가 있으면 반환
                return LyricsAnalysis(
                    title = existingTitle,
                    lyrics = lyrics,
                    genre = existingGenre,
                    analysisResult = cachedResult,
                    id = id,
                )
            }

            // 분석 결과가 없으면 새로 분석
            lyricsId = id
        } else {
            // 가사가 없으면 새로 저장
            lyricsId = lyricsRepository.save(title, lyricsHash, lyrics, genre)
        }

        // 엔티티 생성
        var analysis = LyricsAnalysis(title = title, lyrics = lyrics, genre = genre).withId(lyricsId)

        // AI 서비스를 통한 분석 (장르별 프롬프트 적용)
        val analysisResult = aiService.analyzeLyrics(title, lyrics, genre)
        analysis = analysis.withAnalysisResult(analysisResult)

        // 분석 결과 DB에 저장
        lyricsRepository.saveAnalysisResult(lyricsId, analysisResult)

        return analysis
    }
}
