package com.hipchapedia.presentation.api.controllers

import com.hipchapedia.application.dtos.LyricsAnalysisRequestDTO
import com.hipchapedia.application.dtos.LyricsAnalysisResponseDTO
import com.hipchapedia.application.mappers.LyricsAnalysisMapper
import com.hipchapedia.domain.services.LyricsAnalysisService
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 가사 분석 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/lyrics")
class LyricsAnalysisController(
    private val lyricsAnalysisService: LyricsAnalysisService,
) {
    /**
     * 가사를 분석합니다.
     *
     * POST /api/lyrics/analyze
     *
     * @param request 분석 요청 DTO
     * @return 분석 결과
     */
    @PostMapping("/analyze")
    fun analyzeLyrics(
        @Valid @RequestBody request: LyricsAnalysisRequestDTO,
    ): ResponseEntity<LyricsAnalysisResponseDTO> =
        runBlocking {
            val result = lyricsAnalysisService.analyze(request.title, request.lyrics)
            val responseDTO = LyricsAnalysisMapper.toResponseDTO(result)
            ResponseEntity.status(HttpStatus.OK).body(responseDTO)
        }
}
