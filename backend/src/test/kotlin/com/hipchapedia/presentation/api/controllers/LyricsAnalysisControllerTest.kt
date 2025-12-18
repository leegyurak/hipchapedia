package com.hipchapedia.presentation.api.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hipchapedia.application.dtos.LyricsAnalysisRequestDTO
import com.hipchapedia.domain.entities.LyricsAnalysis
import com.hipchapedia.domain.services.LyricsAnalysisService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(LyricsAnalysisController::class)
class LyricsAnalysisControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var lyricsAnalysisService: LyricsAnalysisService

    @Test
    fun `가사 분석 요청이 성공해야 한다`() {
        // given
        val request =
            LyricsAnalysisRequestDTO(
                title = "Test Song",
                lyrics = "Test lyrics content",
            )

        val analysisResult = "# Analysis Result\n\nTest analysis"
        val entity =
            LyricsAnalysis(
                title = request.title,
                lyrics = request.lyrics,
                analysisResult = analysisResult,
                id = 1L,
            )

        coEvery { lyricsAnalysisService.analyze(request.title, request.lyrics) } returns entity

        // when & then
        mockMvc
            .post("/api/lyrics/analyze") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.title") { value("Test Song") }
                jsonPath("$.lyrics") { value("Test lyrics content") }
                jsonPath("$.analysisResult") { value(analysisResult) }
            }
    }

    @Test
    fun `제목이 없으면 400 에러를 반환해야 한다`() {
        // given
        val request =
            mapOf(
                "title" to "",
                "lyrics" to "Test lyrics",
            )

        // when & then
        mockMvc
            .post("/api/lyrics/analyze") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `가사가 없으면 400 에러를 반환해야 한다`() {
        // given
        val request =
            mapOf(
                "title" to "Test Song",
                "lyrics" to "",
            )

        // when & then
        mockMvc
            .post("/api/lyrics/analyze") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isBadRequest() }
            }
    }
}
