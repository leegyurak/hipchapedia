package com.hipchapedia.presentation.api.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hipchapedia.application.dtos.LyricsAnalysisRequestDTO
import com.hipchapedia.application.usecases.AnalyzeLyricsUseCase
import com.hipchapedia.application.usecases.GetLyricsByIdUseCase
import com.hipchapedia.application.usecases.GetLyricsListUseCase
import com.hipchapedia.application.usecases.SearchLyricsUseCase
import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.domain.entities.LyricsAnalysis
import com.hipchapedia.domain.entities.LyricsSearchResult
import com.hipchapedia.domain.interfaces.LyricsData
import com.hipchapedia.domain.interfaces.LyricsWithAnalysis
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

    @MockkBean
    private lateinit var getLyricsListUseCase: GetLyricsListUseCase

    @MockkBean
    private lateinit var getLyricsByIdUseCase: GetLyricsByIdUseCase

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

    @Test
    fun `가사 목록을 조회할 수 있어야 한다`() {
        // given
        val lyrics =
            listOf(
                LyricsData(
                    id = 3L,
                    title = "Song 3",
                    artist = "Artist 3",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 3",
                ),
                LyricsData(
                    id = 2L,
                    title = "Song 2",
                    artist = "Artist 2",
                    genre = Genre.RNB,
                    lyrics = "lyrics 2",
                ),
            )

        coEvery { getLyricsListUseCase.execute(null, 20, null, null) } returns Triple(lyrics, null, false)

        // when & then
        mockMvc
            .get("/api/lyrics")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.lyrics.length()") { value(2) }
                jsonPath("$.lyrics[0].id") { value(3) }
                jsonPath("$.lyrics[0].title") { value("Song 3") }
                jsonPath("$.lyrics[0].artist") { value("Artist 3") }
                jsonPath("$.lyrics[0].genre") { value("HIPHOP") }
                jsonPath("$.lyrics[0].lyrics") { value("lyrics 3") }
                jsonPath("$.nextCursor") { doesNotExist() }
                jsonPath("$.hasMore") { value(false) }
            }
    }

    @Test
    fun `가사 목록을 cursor로 조회할 수 있어야 한다`() {
        // given
        val cursor = 20L
        val lyrics =
            listOf(
                LyricsData(
                    id = 19L,
                    title = "Song 19",
                    artist = "Artist 19",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 19",
                ),
                LyricsData(
                    id = 18L,
                    title = "Song 18",
                    artist = "Artist 18",
                    genre = Genre.RNB,
                    lyrics = "lyrics 18",
                ),
            )

        coEvery { getLyricsListUseCase.execute(cursor, 20, null, null) } returns Triple(lyrics, null, false)

        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("cursor", cursor.toString())
            }.andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(2) }
                jsonPath("$.lyrics[0].id") { value(19) }
                jsonPath("$.lyrics[1].id") { value(18) }
                jsonPath("$.hasMore") { value(false) }
            }
    }

    @Test
    fun `가사 목록을 limit으로 조회할 수 있어야 한다`() {
        // given
        val limit = 5
        val lyrics =
            (1..5).map { id ->
                LyricsData(
                    id = id.toLong(),
                    title = "Song $id",
                    artist = "Artist $id",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics $id",
                )
            }

        coEvery { getLyricsListUseCase.execute(null, limit, null, null) } returns Triple(lyrics, 5L, true)

        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("limit", limit.toString())
            }.andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(5) }
                jsonPath("$.nextCursor") { value(5) }
                jsonPath("$.hasMore") { value(true) }
            }
    }

    @Test
    fun `가사 목록을 genre로 필터링할 수 있어야 한다`() {
        // given
        val genre = Genre.HIPHOP
        val lyrics =
            listOf(
                LyricsData(
                    id = 3L,
                    title = "Hip Hop Song 3",
                    artist = "Artist 3",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 3",
                ),
                LyricsData(
                    id = 1L,
                    title = "Hip Hop Song 1",
                    artist = "Artist 1",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 1",
                ),
            )

        coEvery { getLyricsListUseCase.execute(null, 20, genre, null) } returns Triple(lyrics, null, false)

        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("genre", "HIPHOP")
            }.andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(2) }
                jsonPath("$.lyrics[0].genre") { value("HIPHOP") }
                jsonPath("$.lyrics[1].genre") { value("HIPHOP") }
            }
    }

    @Test
    fun `가사 목록을 artist로 필터링할 수 있어야 한다`() {
        // given
        val artist = "Drake"
        val lyrics =
            listOf(
                LyricsData(
                    id = 2L,
                    title = "Drake Song 2",
                    artist = "Drake",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 2",
                ),
                LyricsData(
                    id = 1L,
                    title = "Drake Song 1",
                    artist = "Drake",
                    genre = Genre.RNB,
                    lyrics = "lyrics 1",
                ),
            )

        coEvery { getLyricsListUseCase.execute(null, 20, null, artist) } returns Triple(lyrics, null, false)

        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("artist", artist)
            }.andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(2) }
                jsonPath("$.lyrics[0].artist") { value("Drake") }
                jsonPath("$.lyrics[1].artist") { value("Drake") }
            }
    }

    @Test
    fun `가사 목록을 genre와 artist 모두로 필터링할 수 있어야 한다`() {
        // given
        val genre = Genre.HIPHOP
        val artist = "Drake"
        val lyrics =
            listOf(
                LyricsData(
                    id = 1L,
                    title = "Drake Hip Hop Song",
                    artist = "Drake",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics 1",
                ),
            )

        coEvery { getLyricsListUseCase.execute(null, 20, genre, artist) } returns Triple(lyrics, null, false)

        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("genre", "HIPHOP")
                param("artist", artist)
            }.andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(1) }
                jsonPath("$.lyrics[0].genre") { value("HIPHOP") }
                jsonPath("$.lyrics[0].artist") { value("Drake") }
            }
    }

    @Test
    fun `가사가 없으면 빈 리스트를 반환해야 한다`() {
        // given
        coEvery { getLyricsListUseCase.execute(null, 20, null, null) } returns Triple(emptyList(), null, false)

        // when & then
        mockMvc
            .get("/api/lyrics")
            .andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(0) }
                jsonPath("$.hasMore") { value(false) }
            }
    }

    @Test
    fun `모든 파라미터를 함께 사용할 수 있어야 한다`() {
        // given
        val cursor = 50L
        val limit = 10
        val genre = Genre.HIPHOP
        val artist = "Drake"
        val lyrics =
            listOf(
                LyricsData(
                    id = 49L,
                    title = "Drake Song",
                    artist = "Drake",
                    genre = Genre.HIPHOP,
                    lyrics = "lyrics",
                ),
            )

        coEvery { getLyricsListUseCase.execute(cursor, limit, genre, artist) } returns Triple(lyrics, 49L, true)

        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("cursor", cursor.toString())
                param("limit", limit.toString())
                param("genre", "HIPHOP")
                param("artist", artist)
            }.andExpect {
                status { isOk() }
                jsonPath("$.lyrics.length()") { value(1) }
                jsonPath("$.nextCursor") { value(49) }
                jsonPath("$.hasMore") { value(true) }
            }
    }

    @Test
    fun `유효하지 않은 genre면 400 에러를 반환해야 한다`() {
        // when & then
        mockMvc
            .get("/api/lyrics") {
                param("genre", "INVALID_GENRE")
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `ID로 가사와 분석 결과를 조회할 수 있어야 한다`() {
        // given
        val lyricsId = 1L
        val lyricsWithAnalysis =
            LyricsWithAnalysis(
                title = "Test Song",
                lyrics = "Test lyrics content",
                genre = Genre.HIPHOP,
                analysisResult = "# Analysis Result",
            )

        coEvery { getLyricsByIdUseCase.execute(lyricsId) } returns lyricsWithAnalysis

        // when & then
        mockMvc
            .get("/api/lyrics/$lyricsId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.title") { value("Test Song") }
                jsonPath("$.lyrics") { value("Test lyrics content") }
                jsonPath("$.genre") { value("HIPHOP") }
                jsonPath("$.analysisResult") { value("# Analysis Result") }
            }
    }

    @Test
    fun `존재하지 않는 ID로 조회하면 404를 반환해야 한다`() {
        // given
        val lyricsId = 999L

        coEvery { getLyricsByIdUseCase.execute(lyricsId) } returns null

        // when & then
        mockMvc
            .get("/api/lyrics/$lyricsId")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `모든 장르 타입의 가사를 ID로 조회할 수 있어야 한다`() {
        // given
        val genres = listOf("HIPHOP", "RNB", "KPOP", "JPOP", "BAND")

        genres.forEachIndexed { index, genreStr ->
            val lyricsId = index.toLong() + 1
            val genre = Genre.valueOf(genreStr)
            val lyricsWithAnalysis =
                LyricsWithAnalysis(
                    title = "Song $index",
                    lyrics = "Lyrics $index",
                    genre = genre,
                    analysisResult = "Analysis $index",
                )

            coEvery { getLyricsByIdUseCase.execute(lyricsId) } returns lyricsWithAnalysis

            // when & then
            mockMvc
                .get("/api/lyrics/$lyricsId")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.genre") { value(genreStr) }
                }
        }
    }

    @Test
    fun `ID가 0이면 조회할 수 있어야 한다`() {
        // given
        val lyricsId = 0L

        coEvery { getLyricsByIdUseCase.execute(lyricsId) } returns null

        // when & then
        mockMvc
            .get("/api/lyrics/$lyricsId")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `음수 ID로 조회하면 404를 반환해야 한다`() {
        // given
        val lyricsId = -1L

        coEvery { getLyricsByIdUseCase.execute(lyricsId) } returns null

        // when & then
        mockMvc
            .get("/api/lyrics/$lyricsId")
            .andExpect {
                status { isNotFound() }
            }
    }
}
