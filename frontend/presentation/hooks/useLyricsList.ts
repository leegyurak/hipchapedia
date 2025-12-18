'use client';

import { useState, useEffect } from 'react';
import { LyricsAnalysisResponse } from '@/shared/types/lyrics';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';

export function useLyricsList() {
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [analyses, setAnalyses] = useState<LyricsAnalysisResponse[]>([]);

  const fetchAnalyses = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const repository = new LyricsApiRepository();
      const result = await repository.listAnalyses();
      setAnalyses(result);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch analyses';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalyses();
  }, []);

  return {
    isLoading,
    error,
    analyses,
    refetch: fetchAnalyses,
  };
}
