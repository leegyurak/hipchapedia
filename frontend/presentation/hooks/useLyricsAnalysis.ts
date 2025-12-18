'use client';

import { useState } from 'react';
import { LyricsAnalysisRequest, LyricsAnalysisResponse } from '@/shared/types/lyrics';
import { AnalyzeLyricsUseCase } from '@/core/usecases/AnalyzeLyricsUseCase';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';

export function useLyricsAnalysis() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [analysis, setAnalysis] = useState<LyricsAnalysisResponse | null>(null);

  const analyzeLyrics = async (request: LyricsAnalysisRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      const repository = new LyricsApiRepository();
      const useCase = new AnalyzeLyricsUseCase(repository);
      const result = await useCase.execute(request);
      setAnalysis(result);
      return result;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to analyze lyrics';
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const reset = () => {
    setAnalysis(null);
    setError(null);
    setIsLoading(false);
  };

  return {
    isLoading,
    error,
    analysis,
    analyzeLyrics,
    reset,
  };
}
