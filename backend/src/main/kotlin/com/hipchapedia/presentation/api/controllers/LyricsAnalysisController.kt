package com.hipchapedia.presentation.api.controllers

import com.hipchapedia.application.dtos.LyricsAnalysisRequestDTO
import com.hipchapedia.application.dtos.LyricsAnalysisResponseDTO
import com.hipchapedia.application.dtos.LyricsSearchResponseDTO
import com.hipchapedia.application.mappers.LyricsAnalysisMapper
import com.hipchapedia.application.mappers.LyricsSearchMapper
import com.hipchapedia.application.usecases.AnalyzeLyricsUseCase
import com.hipchapedia.application.usecases.SearchLyricsUseCase
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 가사 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/lyrics")
class LyricsAnalysisController(
    private val analyzeLyricsUseCase: AnalyzeLyricsUseCase,
    private val searchLyricsUseCase: SearchLyricsUseCase,
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
            val result = analyzeLyricsUseCase.execute(request.title, request.lyrics)
            val responseDTO = LyricsAnalysisMapper.toResponseDTO(result)
            ResponseEntity.status(HttpStatus.OK).body(responseDTO)
        }

    /**
     * 가사를 검색합니다.
     *
     * GET /api/lyrics/search?title=&artist=
     *
     * @param title 곡 제목
     * @param artist 아티스트명
     * @return 검색 결과
     */
    @GetMapping("/search")
    fun searchLyrics(
        @RequestParam title: String,
        @RequestParam artist: String,
    ): ResponseEntity<LyricsSearchResponseDTO> =
        runBlocking {
            val result = searchLyricsUseCase.execute(title, artist)
            if (result != null) {
                val responseDTO = LyricsSearchMapper.toResponseDTO(result)
                ResponseEntity.status(HttpStatus.OK).body(responseDTO)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        }
}
