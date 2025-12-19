package com.hipchapedia.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web Configuration
 *
 * CORS 설정 등 웹 관련 설정을 관리
 */
@Configuration
@Profile("dev", "local", "default")
class DevWebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}

/**
 * Production Web Configuration
 *
 * Production 환경에서는 특정 origin만 허용
 */
@Configuration
@Profile("prod")
class ProdWebConfig(
    @Value("\${cors.allowed-origins:}") private val allowedOrigins: String
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        val origins = allowedOrigins.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        registry
            .addMapping("/**")
            .allowedOrigins(*origins.toTypedArray())
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
