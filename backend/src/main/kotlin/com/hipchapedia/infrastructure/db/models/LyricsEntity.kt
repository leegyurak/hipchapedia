package com.hipchapedia.infrastructure.db.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

/**
 * 가사 JPA 엔티티
 */
@Entity
@Table(
    name = "lyrics",
    indexes = [
        Index(name = "idx_lyrics_hash", columnList = "lyrics_hash"),
        Index(name = "idx_created_at", columnList = "created_at"),
    ],
)
class LyricsEntity(
    @Column(name = "title", nullable = false, length = 200)
    var title: String,
    @Column(name = "lyrics_hash", nullable = false, unique = true, length = 64)
    val lyricsHash: String,
    @Column(name = "original_lyrics", nullable = false, columnDefinition = "TEXT")
    val originalLyrics: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
