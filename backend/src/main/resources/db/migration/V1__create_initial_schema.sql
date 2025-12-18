-- V1__create_initial_schema.sql
-- 초기 스키마 생성: lyrics 및 lyrics_analysis_result 테이블

-- lyrics 테이블 생성
CREATE TABLE IF NOT EXISTS lyrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '곡 제목',
    lyrics_hash VARCHAR(64) NOT NULL UNIQUE COMMENT '가사 SHA-256 해시',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    INDEX idx_lyrics_hash (lyrics_hash),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='가사 정보';

-- lyrics_analysis_result 테이블 생성
CREATE TABLE IF NOT EXISTS lyrics_analysis_result (
    lyrics_id BIGINT PRIMARY KEY COMMENT '가사 ID (PK, FK)',
    analysis_result TEXT NOT NULL COMMENT '분석 결과 (Markdown)',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
    INDEX idx_analysis_created_at (created_at DESC),
    CONSTRAINT fk_lyrics_analysis_result_lyrics
        FOREIGN KEY (lyrics_id) REFERENCES lyrics(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='가사 분석 결과';
