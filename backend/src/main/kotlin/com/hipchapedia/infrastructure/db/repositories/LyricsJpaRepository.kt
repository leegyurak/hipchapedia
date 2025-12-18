package com.hipchapedia.infrastructure.db.repositories

import com.hipchapedia.infrastructure.db.models.LyricsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 가사 JPA Repository
 */
@Repository
interface LyricsJpaRepository : JpaRepository<LyricsEntity, Long> {
    /**
     * 가사 해시로 가사를 조회합니다.
     *
     * @param lyricsHash 가사 해시
     * @return LyricsEntity 또는 null
     */
    fun findByLyricsHash(lyricsHash: String): LyricsEntity?
}
