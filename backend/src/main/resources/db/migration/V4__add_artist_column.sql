-- V4__add_artist_column.sql
-- lyrics 테이블에 artist 컬럼 추가

ALTER TABLE lyrics
ADD COLUMN artist VARCHAR(200) NULL COMMENT '아티스트명';
