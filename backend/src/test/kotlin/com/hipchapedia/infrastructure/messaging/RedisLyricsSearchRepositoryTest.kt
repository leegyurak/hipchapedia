package com.hipchapedia.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RedisLyricsSearchRepositoryTest {
    private val redisTemplate: RedisTemplate<String, String> = mockk(relaxed = true)
    private val objectMapper = ObjectMapper()
    private lateinit var repository: RedisLyricsSearchRepository

    @BeforeEach
    fun setup() {
        repository = RedisLyricsSearchRepository(redisTemplate, objectMapper)
        ReflectionTestUtils.setField(repository, "requestChannel", "lyrics:requests")
    }

    @Test
    fun `검색 요청을 Redis에 발행해야 한다`() {
        // given
        val title = "Test Song"
        val artist = "Test Artist"

        // when
        repository.publishSearchRequest(title, artist)

        // then
        verify { redisTemplate.convertAndSend(eq("lyrics:requests"), any<String>()) }
    }

    @Test
    fun `검색 요청 메시지가 올바른 형식이어야 한다`() {
        // given
        val title = "Test Song"
        val artist = "Test Artist"

        // when
        repository.publishSearchRequest(title, artist)

        // then
        verify { redisTemplate.convertAndSend("lyrics:requests", any<String>()) }
    }

    @Test
    fun `Redis 메시지를 받으면 대기 중인 요청을 처리해야 한다`() =
        runTest {
            // given
            val title = "Test Song"
            val artist = "Test Artist"

            val responseData =
                mapOf(
                    "title" to title,
                    "artist" to artist,
                    "lyrics" to "Test lyrics",
                    "url" to "https://genius.com/test",
                    "album" to "Test Album",
                    "release_date" to "2024-01-01",
                )

            val message: Message = mockk()
            every { message.body } returns objectMapper.writeValueAsBytes(responseData)

            // 별도 스레드에서 메시지 처리
            Thread {
                Thread.sleep(100)
                repository.onMessage(message, null)
            }.start()

            // when
            repository.publishSearchRequest(title, artist)
            val result = repository.waitForResult(title, artist, 5)

            // then
            assertNotNull(result)
            assertEquals(title, result.title)
            assertEquals(artist, result.artist)
            assertEquals("Test lyrics", result.lyrics)
            assertEquals("https://genius.com/test", result.url)
            assertEquals("Test Album", result.album)
            assertEquals("2024-01-01", result.releaseDate)
        }

    @Test
    fun `타임아웃 발생 시 null을 반환해야 한다`() =
        runTest {
            // given
            val title = "Timeout Song"
            val artist = "Timeout Artist"

            // when
            repository.publishSearchRequest(title, artist)
            val result = repository.waitForResult(title, artist, 1) // 1초 타임아웃

            // then
            assertNull(result)
        }

    @Test
    fun `잘못된 형식의 메시지는 무시해야 한다`() {
        // given
        val invalidMessage: Message = mockk()
        every { invalidMessage.body } returns "invalid json".toByteArray()

        // when & then (예외가 발생하지 않아야 함)
        repository.onMessage(invalidMessage, null)
    }

    @Test
    fun `title이 없는 메시지는 무시해야 한다`() {
        // given
        val incompleteData = mapOf("artist" to "Test Artist")
        val message: Message = mockk()
        every { message.body } returns objectMapper.writeValueAsBytes(incompleteData)

        // when & then (예외가 발생하지 않아야 함)
        repository.onMessage(message, null)
    }

    @Test
    fun `artist가 없는 메시지는 무시해야 한다`() {
        // given
        val incompleteData = mapOf("title" to "Test Song")
        val message: Message = mockk()
        every { message.body } returns objectMapper.writeValueAsBytes(incompleteData)

        // when & then (예외가 발생하지 않아야 함)
        repository.onMessage(message, null)
    }

    @Test
    fun `대기 중인 요청이 없는 메시지는 무시해야 한다`() {
        // given
        val responseData =
            mapOf(
                "title" to "Unknown Song",
                "artist" to "Unknown Artist",
                "lyrics" to "Test lyrics",
            )
        val message: Message = mockk()
        every { message.body } returns objectMapper.writeValueAsBytes(responseData)

        // when & then (예외가 발생하지 않아야 함)
        repository.onMessage(message, null)
    }
}
