package com.hipchapedia

import com.hipchapedia.infrastructure.config.AnthropicConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

/**
 * Hipchapedia Application
 *
 * 힙합 가사 분석 서비스
 */
@SpringBootApplication
@EnableConfigurationProperties(AnthropicConfig::class)
class HipchapediaApplication

fun main(args: Array<String>) {
    runApplication<HipchapediaApplication>(*args)
}
