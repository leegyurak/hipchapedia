package com.hipchapedia.presentation.api.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hipchapedia.application.dtos.LyricsAnalysisRequestDTO
import com.hipchapedia.application.usecases.AnalyzeLyricsUseCase
import com.hipchapedia.application.usecases.SearchLyricsUseCase
import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.entities.LyricsAnalysis
import com.hipchapedia.domain.entities.LyricsSearchResult
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(LyricsAnalysisController::class)
class LyricsAnalysisControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var analyzeLyricsUseCase: AnalyzeLyricsUseCase

    @MockkBean
    private lateinit var searchLyricsUseCase: SearchLyricsUseCase

    @Test
    fun `가사 분석 요청이 성공해야 한다`() {
        // given
        val request =
            LyricsAnalysisRequestDTO(
                title = "Test Song",
                lyrics = "Test lyrics content",
                genre = Genre.HIPHOP,
            )

        val analysisResult = "# Analysis Result\n\nTest analysis"
        val entity =
            LyricsAnalysis(
                title = request.title,
                lyrics = request.lyrics,
                genre = request.genre,
                analysisResult = analysisResult,
                id = 1L,
            )

        coEvery { analyzeLyricsUseCase.execute(request.title, request.lyrics, request.genre) } returns entity

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
                jsonPath("$.genre") { value("HIPHOP") }
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
                "genre" to "HIPHOP",
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
                "genre" to "HIPHOP",
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
    fun `장르가 누락되면 에러를 반환해야 한다`() {
        // given - genre 필드를 아예 보내지 않는 경우
        val request =
            """
            {
                "title": "Test Song",
                "lyrics": "Test lyrics"
            }
            """.trimIndent()

        // when & then
        mockMvc
            .post("/api/lyrics/analyze") {
                contentType = MediaType.APPLICATION_JSON
                content = request
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `유효하지 않은 장르면 400 에러를 반환해야 한다`() {
        // given
        val request =
            """
            {
                "title": "Test Song",
                "lyrics": "Test lyrics",
                "genre": "INVALID_GENRE"
            }
            """.trimIndent()

        // when & then
        mockMvc
            .post("/api/lyrics/analyze") {
                contentType = MediaType.APPLICATION_JSON
                content = request
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `모든 장르 타입에 대해 정상적으로 처리해야 한다`() {
        // given
        val genres = listOf("HIPHOP", "RNB", "KPOP", "JPOP", "BAND")

        genres.forEach { genreStr ->
            val request =
                mapOf(
                    "title" to "Test Song",
                    "lyrics" to "Test lyrics",
                    "genre" to genreStr,
                )

            val genre = Genre.valueOf(genreStr)
            val analysisResult = "# Analysis Result"
            val entity =
                LyricsAnalysis(
                    title = "Test Song",
                    lyrics = "Test lyrics",
                    genre = genre,
                    analysisResult = analysisResult,
                    id = 1L,
                )

            coEvery { analyzeLyricsUseCase.execute("Test Song", "Test lyrics", genre) } returns entity

            // when & then
            mockMvc
                .post("/api/lyrics/analyze") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.genre") { value(genreStr) }
                }
        }
    }

    @Test
    fun `가사 검색 요청이 성공해야 한다`() {
        // given
        val title = "Test Song"
        val artist = "Test Artist"
        val searchResult =
            LyricsSearchResult(
                title = title,
                artist = artist,
                lyrics = "Test lyrics content",
                url = "https://genius.com/test-song",
                album = "Test Album",
                releaseDate = "2024-01-01",
            )

        coEvery { searchLyricsUseCase.execute(title, artist) } returns searchResult

        // when & then
        mockMvc
            .get("/api/lyrics/search") {
                param("title", title)
                param("artist", artist)
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.title") { value(title) }
                jsonPath("$.artist") { value(artist) }
                jsonPath("$.lyrics") { value("Test lyrics content") }
                jsonPath("$.url") { value("https://genius.com/test-song") }
                jsonPath("$.album") { value("Test Album") }
                jsonPath("$.releaseDate") { value("2024-01-01") }
            }
    }

    @Test
    fun `가사 검색 결과가 없으면 404를 반환해야 한다`() {
        // given
        val title = "Unknown Song"
        val artist = "Unknown Artist"

        coEvery { searchLyricsUseCase.execute(title, artist) } returns null

        // when & then
        mockMvc
            .get("/api/lyrics/search") {
                param("title", title)
                param("artist", artist)
            }.andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `가사 검색 시 title 파라미터가 빈 문자열이면 검색이 실행되어야 한다`() {
        // given
        val artist = "Test Artist"
        coEvery { searchLyricsUseCase.execute("", artist) } returns null

        // when & then
        mockMvc
            .get("/api/lyrics/search") {
                param("title", "")
                param("artist", artist)
            }.andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `가사 검색 시 artist 파라미터가 빈 문자열이면 검색이 실행되어야 한다`() {
        // given
        val title = "Test Song"
        coEvery { searchLyricsUseCase.execute(title, "") } returns null

        // when & then
        mockMvc
            .get("/api/lyrics/search") {
                param("title", title)
                param("artist", "")
            }.andExpect {
                status { isNotFound() }
            }
    }
}
