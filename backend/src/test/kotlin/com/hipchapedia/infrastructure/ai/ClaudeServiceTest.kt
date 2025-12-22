package com.hipchapedia.infrastructure.ai

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hipchapedia.infrastructure.config.AnthropicConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals

class ClaudeServiceTest {
    private val anthropicConfig: AnthropicConfig = mockk()
    private lateinit var service: ClaudeService
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        every { anthropicConfig.apiKey } returns "test-api-key"
        every { anthropicConfig.model } returns "claude-3-5-sonnet-20241022"
        service = ClaudeService(anthropicConfig)
    }

    @Test
    fun `시스템 프롬프트가 로드되어야 한다`() {
        // when
        val systemPrompt = ReflectionTestUtils.getField(service, "systemPrompt") as String

        // then
        assert(systemPrompt.isNotEmpty())
    }

    @Test
    fun `성공적인 API 응답을 파싱해야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            val responseJson =
                """
                {
                    "content": [
                        {
                            "type": "text",
                            "text": "# 가사 분석\n\n테스트 분석 결과"
                        }
                    ]
                }
                """.trimIndent()

            every { mockResponse.isSuccessful } returns true
            every { mockResponse.body } returns responseJson.toResponseBody("application/json".toMediaType())
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when
            val result = service.analyzeLyrics("Test Song", "Test lyrics")

            // then
            assertEquals("# 가사 분석\n\n테스트 분석 결과", result)
        }

    @Test
    fun `API 호출 실패 시 예외를 던져야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            every { mockResponse.isSuccessful } returns false
            every { mockResponse.code } returns 500
            every { mockResponse.message } returns "Internal Server Error"
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when & then
            val exception =
                assertThrows<RuntimeException> {
                    service.analyzeLyrics("Test Song", "Test lyrics")
                }
            assert(exception.message?.contains("Claude API 호출 실패") == true)
        }

    @Test
    fun `응답 본문이 없으면 예외를 던져야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            every { mockResponse.isSuccessful } returns true
            every { mockResponse.body } returns null
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when & then
            val exception =
                assertThrows<RuntimeException> {
                    service.analyzeLyrics("Test Song", "Test lyrics")
                }
            assertEquals("응답 본문이 없습니다", exception.message)
        }

    @Test
    fun `content가 없는 응답은 예외를 던져야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            val responseJson = "{}"

            every { mockResponse.isSuccessful } returns true
            every { mockResponse.body } returns responseJson.toResponseBody("application/json".toMediaType())
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when & then
            val exception =
                assertThrows<IllegalStateException> {
                    service.analyzeLyrics("Test Song", "Test lyrics")
                }
            assertEquals("응답에 content가 없습니다", exception.message)
        }

    @Test
    fun `content가 빈 배열이면 예외를 던져야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            val responseJson =
                """
                {
                    "content": []
                }
                """.trimIndent()

            every { mockResponse.isSuccessful } returns true
            every { mockResponse.body } returns responseJson.toResponseBody("application/json".toMediaType())
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when & then
            val exception =
                assertThrows<IllegalStateException> {
                    service.analyzeLyrics("Test Song", "Test lyrics")
                }
            assertEquals("응답에 content가 없습니다", exception.message)
        }

    @Test
    fun `content type이 text가 아니면 예외를 던져야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            val responseJson =
                """
                {
                    "content": [
                        {
                            "type": "image",
                            "data": "base64data"
                        }
                    ]
                }
                """.trimIndent()

            every { mockResponse.isSuccessful } returns true
            every { mockResponse.body } returns responseJson.toResponseBody("application/json".toMediaType())
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when & then
            val exception =
                assertThrows<IllegalStateException> {
                    service.analyzeLyrics("Test Song", "Test lyrics")
                }
            assert(exception.message?.contains("예상치 못한 content type") == true)
        }

    @Test
    fun `text 필드가 없으면 예외를 던져야 한다`() =
        runTest {
            // given
            val mockClient: OkHttpClient = mockk()
            val mockCall: Call = mockk()
            val mockResponse: Response = mockk()

            val responseJson =
                """
                {
                    "content": [
                        {
                            "type": "text"
                        }
                    ]
                }
                """.trimIndent()

            every { mockResponse.isSuccessful } returns true
            every { mockResponse.body } returns responseJson.toResponseBody("application/json".toMediaType())
            every { mockCall.execute() } returns mockResponse
            every { mockClient.newCall(any()) } returns mockCall

            ReflectionTestUtils.setField(service, "client", mockClient)

            // when & then
            val exception =
                assertThrows<IllegalStateException> {
                    service.analyzeLyrics("Test Song", "Test lyrics")
                }
            assertEquals("text 필드가 없습니다", exception.message)
        }
}
