'use client';

import { useState } from 'react';
import { LyricsSearchRequest, LyricsSearchResponse } from '@/shared/types/lyrics';
import { SearchLyricsUseCase } from '@/core/usecases/SearchLyricsUseCase';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';

export function useLyricsSearch() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchResult, setSearchResult] = useState<LyricsSearchResponse | null>(null);

  const searchLyrics = async (request: LyricsSearchRequest) => {
    setIsLoading(true);
    setError(null);
    setSearchResult(null);

    try {
      const repository = new LyricsApiRepository();
      const useCase = new SearchLyricsUseCase(repository);
      const result = await useCase.execute(request);

      if (result === null) {
        setError('검색 결과를 찾을 수 없습니다.');
        return null;
      }

      setSearchResult(result);
      return result;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to search lyrics';
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const reset = () => {
    setSearchResult(null);
    setError(null);
    setIsLoading(false);
  };

  return {
    isLoading,
    error,
    searchResult,
    searchLyrics,
    reset,
  };
}
