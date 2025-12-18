package com.hipchapedia.infrastructure.db.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

/**
 * 가사 분석 결과 JPA 엔티티
 */
@Entity
@Table(
    name = "lyrics_analysis_result",
    indexes = [
        Index(name = "idx_analysis_created_at", columnList = "created_at"),
    ],
)
class LyricsAnalysisResultEntity(
    @Id
    @Column(name = "lyrics_id")
    val lyricsId: Long,
    @Lob
    @Column(name = "analysis_result", nullable = false, columnDefinition = "TEXT")
    var analysisResult: String,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
