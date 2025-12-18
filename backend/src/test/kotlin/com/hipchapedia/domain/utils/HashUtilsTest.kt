package com.hipchapedia.domain.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HashUtilsTest {
    @Test
    fun `동일한 가사는 동일한 해시를 생성해야 한다`() {
        // given
        val lyrics = "Test lyrics content"

        // when
        val hash1 = generateLyricsHash(lyrics)
        val hash2 = generateLyricsHash(lyrics)

        // then
        assertEquals(hash1, hash2)
    }

    @Test
    fun `다른 가사는 다른 해시를 생성해야 한다`() {
        // given
        val lyrics1 = "Test lyrics 1"
        val lyrics2 = "Test lyrics 2"

        // when
        val hash1 = generateLyricsHash(lyrics1)
        val hash2 = generateLyricsHash(lyrics2)

        // then
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `SHA-256 해시는 64자리 16진수여야 한다`() {
        // given
        val lyrics = "Test lyrics"

        // when
        val hash = generateLyricsHash(lyrics)

        // then
        assertEquals(64, hash.length)
        assert(hash.all { it in '0'..'9' || it in 'a'..'f' })
    }

    @Test
    fun `공백이 다른 동일한 가사는 같은 해시를 생성해야 한다`() {
        // given
        val lyrics1 = "안녕하세요 반갑습니다"
        val lyrics2 = "안녕하세요  반갑습니다" // 공백 2개
        val lyrics3 = "안녕하세요\t반갑습니다" // 탭
        val lyrics4 = "안녕하세요반갑습니다" // 공백 없음

        // when
        val hash1 = generateLyricsHash(lyrics1)
        val hash2 = generateLyricsHash(lyrics2)
        val hash3 = generateLyricsHash(lyrics3)
        val hash4 = generateLyricsHash(lyrics4)

        // then
        assertEquals(hash1, hash2)
        assertEquals(hash2, hash3)
        assertEquals(hash3, hash4)
    }

    @Test
    fun `줄바꿈이 다른 동일한 가사는 같은 해시를 생성해야 한다`() {
        // given
        val lyrics1 = "첫번째 줄\n두번째 줄"
        val lyrics2 = "첫번째 줄\r\n두번째 줄" // Windows 스타일
        val lyrics3 = "첫번째 줄 두번째 줄" // 공백
        val lyrics4 = "첫번째줄두번째줄" // 공백 없음

        // when
        val hash1 = generateLyricsHash(lyrics1)
        val hash2 = generateLyricsHash(lyrics2)
        val hash3 = generateLyricsHash(lyrics3)
        val hash4 = generateLyricsHash(lyrics4)

        // then
        assertEquals(hash1, hash2)
        assertEquals(hash2, hash3)
        assertEquals(hash3, hash4)
    }

    @Test
    fun `앞뒤 공백이 있는 가사는 공백 없는 가사와 같은 해시를 생성해야 한다`() {
        // given
        val lyrics1 = "가사 내용"
        val lyrics2 = " 가사 내용 "
        val lyrics3 = "\n가사 내용\n"
        val lyrics4 = "가사내용"

        // when
        val hash1 = generateLyricsHash(lyrics1)
        val hash2 = generateLyricsHash(lyrics2)
        val hash3 = generateLyricsHash(lyrics3)
        val hash4 = generateLyricsHash(lyrics4)

        // then
        assertEquals(hash1, hash2)
        assertEquals(hash2, hash3)
        assertEquals(hash3, hash4)
    }
}
