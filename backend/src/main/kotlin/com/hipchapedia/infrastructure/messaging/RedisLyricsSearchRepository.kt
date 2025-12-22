package com.hipchapedia.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.hipchapedia.domain.entities.LyricsSearchResult
import com.hipchapedia.domain.interfaces.LyricsSearchRepositoryInterface
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Redis 기반 가사 검색 리포지토리 구현
 */
@Repository
class RedisLyricsSearchRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : LyricsSearchRepositoryInterface,
    MessageListener {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val pendingRequests = ConcurrentHashMap<String, Continuation<LyricsSearchResult?>>()

    @Value("\${redis.channel.request:lyrics:requests}")
    private lateinit var requestChannel: String

    override fun publishSearchRequest(
        title: String,
        artist: String,
    ) {
        val request =
            mapOf(
                "title" to title,
                "artist" to artist,
            )
        val message = objectMapper.writeValueAsString(request)
        redisTemplate.convertAndSend(requestChannel, message)
        logger.info("Published search request: $title by $artist")
    }

    override suspend fun waitForResult(
        title: String,
        artist: String,
        timeoutSeconds: Long,
    ): LyricsSearchResult? =
        suspendCoroutine { continuation ->
            val key = "$title:$artist"
            pendingRequests[key] = continuation

            // Timeout handler
            Thread {
                Thread.sleep(TimeUnit.SECONDS.toMillis(timeoutSeconds))
                val removed = pendingRequests.remove(key)
                if (removed != null) {
                    logger.warn("Request timed out for: $key")
                    removed.resume(null)
                }
            }.start()
        }

    override fun onMessage(
        message: Message,
        pattern: ByteArray?,
    ) {
        try {
            val body = String(message.body)
            logger.info("Received message from Redis: $body")

            val result = objectMapper.readValue(body, Map::class.java)

            // Use request_title/request_artist for key matching (original request)
            val requestTitle = result["request_title"] as? String
            val requestArtist = result["request_artist"] as? String

            // Use title/artist for the actual song data (from Genius API)
            val title = result["title"] as? String ?: return
            val artist = result["artist"] as? String ?: return

            // For key matching, prefer original request, fallback to response data
            val keyTitle = requestTitle ?: title
            val keyArtist = requestArtist ?: artist
            val key = "$keyTitle:$keyArtist"

            logger.info("Looking for pending request with key: $key")
            val continuation = pendingRequests.remove(key)

            if (continuation != null) {
                val searchResult =
                    LyricsSearchResult(
                        title = title,
                        artist = artist,
                        lyrics = result["lyrics"] as? String,
                        url = result["url"] as? String,
                        album = result["album"] as? String,
                        releaseDate = result["release_date"] as? String,
                    )
                continuation.resume(searchResult)
                logger.info("Successfully matched request for: $key (song: $title by $artist)")
            } else {
                logger.warn("No pending request found for: $key. Pending keys: ${pendingRequests.keys}")
            }
        } catch (e: Exception) {
            logger.error("Error processing Redis message", e)
        }
    }
}
