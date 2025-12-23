-- V3__add_genre_column.sql
-- lyrics 테이블에 genre 컬럼 추가

ALTER TABLE lyrics
ADD COLUMN genre VARCHAR(20) NOT NULL DEFAULT 'HIPHOP' COMMENT '음악 장르 (HIPHOP, RNB, KPOP, JPOP, BAND)';

-- genre 인덱스 추가
CREATE INDEX idx_genre ON lyrics(genre);
