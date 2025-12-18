package com.hipchapedia.domain.utils

import java.security.MessageDigest

/**
 * 가사 해시를 생성합니다.
 * 가사의 모든 공백 문자(공백, 탭, 줄바꿈 등)를 제거하고 정규화한 후 해시를 생성합니다.
 *
 * @param lyrics 가사 내용
 * @return SHA-256 해시 (16진수 문자열)
 */
fun generateLyricsHash(lyrics: String): String {
    // 가사 정규화: 모든 공백 문자(공백, 탭, 줄바꿈 등) 제거
    val normalizedLyrics = lyrics.replace(Regex("\\s+"), "")

    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(normalizedLyrics.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}
