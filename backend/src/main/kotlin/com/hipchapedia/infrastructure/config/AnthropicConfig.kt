package com.hipchapedia.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Anthropic API 설정
 */
@ConfigurationProperties(prefix = "anthropic")
data class AnthropicConfig(
    val apiKey: String,
    val model: String = "claude-haiku-4-5",
)
