package com.hipchapedia.infrastructure.db.repositories

import com.hipchapedia.domain.entities.Genre
import com.hipchapedia.infrastructure.db.models.LyricsEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    /**
     * Cursor pagination을 사용하여 가사 목록을 조회합니다.
     * cursor가 null이면 처음부터 조회하고, 있으면 해당 ID보다 작은 ID를 조회합니다.
     * artist는 LIKE 연산으로 부분 일치 검색을 수행합니다.
     * artist가 null인 레코드는 결과에서 제외됩니다.
     * 분석 결과가 있는 가사만 조회합니다.
     *
     * @param cursor 마지막 가사 ID
     * @param genre 필터링할 장르
     * @param artist 필터링할 아티스트 (부분 일치)
     * @param pageable 페이징 정보
     * @return LyricsEntity 리스트
     */
    @Query(
        """
        SELECT l FROM LyricsEntity l
        WHERE l.artist IS NOT NULL
        AND EXISTS (SELECT 1 FROM LyricsAnalysisResultEntity a WHERE a.lyricsId = l.id)
        AND (:cursor IS NULL OR l.id < :cursor)
        AND (:genre IS NULL OR l.genre = :genre)
        AND (:artist IS NULL OR l.artist LIKE %:artist%)
        ORDER BY l.id DESC
        """,
    )
    fun findLyricsWithCursor(
        @Param("cursor") cursor: Long?,
        @Param("genre") genre: Genre?,
        @Param("artist") artist: String?,
        pageable: Pageable,
    ): List<LyricsEntity>
}
