'use client';

import { useState, useEffect } from 'react';
import { LyricsItem, Genre } from '@/shared/types/lyrics';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';

interface UseLyricsListOptions {
  genre?: Genre;
  artist?: string;
}

export function useLyricsList(options?: UseLyricsListOptions) {
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lyrics, setLyrics] = useState<LyricsItem[]>([]);
  const [nextCursor, setNextCursor] = useState<number | null>(null);
  const [hasMore, setHasMore] = useState(false);

  const repository = new LyricsApiRepository();

  const fetchLyrics = async (cursor?: number, append: boolean = false) => {
    if (append) {
      setIsLoadingMore(true);
    } else {
      setIsLoading(true);
      setLyrics([]);
    }
    setError(null);

    try {
      const result = await repository.listAnalyses(cursor, 20, options?.genre, options?.artist);

      if (append) {
        setLyrics(prev => [...prev, ...result.lyrics]);
      } else {
        setLyrics(result.lyrics);
      }
      setNextCursor(result.nextCursor);
      setHasMore(result.hasMore);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch lyrics';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
    }
  };

  const loadMore = () => {
    if (nextCursor && hasMore && !isLoadingMore) {
      fetchLyrics(nextCursor, true);
    }
  };

  useEffect(() => {
    fetchLyrics();
  }, [options?.genre, options?.artist]);

  return {
    isLoading,
    isLoadingMore,
    error,
    lyrics,
    hasMore,
    loadMore,
    refetch: () => fetchLyrics(),
  };
}
