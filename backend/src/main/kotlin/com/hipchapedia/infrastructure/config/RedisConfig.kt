package com.hipchapedia.infrastructure.config

import com.hipchapedia.infrastructure.messaging.RedisLyricsSearchRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * Redis 설정
 */
@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host:localhost}")
    private lateinit var redisHost: String

    @Value("\${spring.data.redis.port:6379}")
    private var redisPort: Int = 6379

    @Value("\${redis.channel.result:lyrics:results}")
    private lateinit var resultChannel: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration()
        redisConfiguration.hostName = redisHost
        redisConfiguration.port = redisPort
        return LettuceConnectionFactory(redisConfiguration)
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        return template
    }

    @Bean
    fun messageListenerAdapter(redisLyricsSearchRepository: RedisLyricsSearchRepository): MessageListenerAdapter =
        MessageListenerAdapter(redisLyricsSearchRepository)

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        messageListenerAdapter: MessageListenerAdapter,
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(messageListenerAdapter, ChannelTopic(resultChannel))
        return container
    }
}
