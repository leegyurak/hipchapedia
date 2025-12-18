package com.hipchapedia.infrastructure.db.repositories

import com.hipchapedia.infrastructure.db.models.LyricsAnalysisResultEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 가사 분석 결과 JPA Repository
 */
@Repository
interface LyricsAnalysisResultJpaRepository : JpaRepository<LyricsAnalysisResultEntity, Long>
