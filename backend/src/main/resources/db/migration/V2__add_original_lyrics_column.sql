-- V2__add_original_lyrics_column.sql
-- lyrics 테이블에 original_lyrics 컬럼 추가

ALTER TABLE lyrics
ADD COLUMN original_lyrics TEXT NOT NULL COMMENT '원본 가사 내용';
