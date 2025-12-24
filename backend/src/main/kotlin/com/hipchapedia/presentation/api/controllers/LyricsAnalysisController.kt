package com.hipchapedia.presentation.api.controllers

import com.hipchapedia.application.dtos.LyricsAnalysisRequestDTO
import com.hipchapedia.application.dtos.LyricsAnalysisResponseDTO
import com.hipchapedia.application.dtos.LyricsListResponseDTO
import com.hipchapedia.application.dtos.LyricsSearchResponseDTO
import com.hipchapedia.application.mappers.LyricsAnalysisMapper
import com.hipchapedia.application.mappers.LyricsByIdMapper
import com.hipchapedia.application.mappers.LyricsListMapper
import com.hipchapedia.application.mappers.LyricsSearchMapper
import com.hipchapedia.application.usecases.AnalyzeLyricsUseCase
import com.hipchapedia.application.usecases.GetLyricsByIdUseCase
import com.hipchapedia.application.usecases.GetLyricsListUseCase
import com.hipchapedia.application.usecases.SearchLyricsUseCase
import com.hipchapedia.domain.entities.Genre
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
    private val getLyricsListUseCase: GetLyricsListUseCase,
    private val getLyricsByIdUseCase: GetLyricsByIdUseCase,
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
            val result = analyzeLyricsUseCase.execute(request.title, request.lyrics, request.genre, request.artist)
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

    /**
     * 가사 목록을 조회합니다.
     *
     * GET /api/lyrics?cursor=&limit=&genre=&artist=
     *
     * @param cursor 마지막 가사 ID (nullable)
     * @param limit 조회할 개수 (기본값: 20)
     * @param genre 필터링할 장르 (nullable)
     * @param artist 필터링할 아티스트 (nullable)
     * @return 가사 목록 및 pagination 정보
     */
    @GetMapping
    fun getLyricsList(
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(required = false, defaultValue = "20") limit: Int,
        @RequestParam(required = false) genre: Genre?,
        @RequestParam(required = false) artist: String?,
    ): ResponseEntity<LyricsListResponseDTO> =
        runBlocking {
            val (lyrics, nextCursor, hasMore) =
                getLyricsListUseCase.execute(cursor, limit, genre, artist)

            val response = LyricsListMapper.toResponseDTO(lyrics, nextCursor, hasMore)

            ResponseEntity.status(HttpStatus.OK).body(response)
        }

    /**
     * ID로 가사와 분석 결과를 조회합니다.
     *
     * GET /api/lyrics/{id}
     *
     * @param id 가사 ID
     * @return 가사와 분석 결과
     */
    @GetMapping("/{id}")
    fun getLyricsById(
        @PathVariable id: Long,
    ): ResponseEntity<LyricsAnalysisResponseDTO> =
        runBlocking {
            val result = getLyricsByIdUseCase.execute(id)
            if (result != null) {
                val responseDTO = LyricsByIdMapper.toResponseDTO(result)
                ResponseEntity.status(HttpStatus.OK).body(responseDTO)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        }
}
