'use client';

import { useState } from 'react';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import LyricsSearchForm from '@/presentation/components/LyricsSearchForm';
import LyricsSearchResult from '@/presentation/components/LyricsSearchResult';
import AnalysisResult from '@/presentation/components/AnalysisResult';
import { useLyricsAnalysis } from '@/presentation/hooks/useLyricsAnalysis';
import { useLyricsSearch } from '@/presentation/hooks/useLyricsSearch';
import { LyricsSearchRequest } from '@/shared/types/lyrics';

export default function AnalyzePage() {
  const { isLoading: isAnalyzing, error: analysisError, analysis, analyzeLyrics, reset: resetAnalysis } = useLyricsAnalysis();
  const { isLoading: isSearching, error: searchError, searchResult, searchLyrics, reset: resetSearch } = useLyricsSearch();

  const handleSearch = async (data: LyricsSearchRequest) => {
    try {
      await searchLyrics(data);
    } catch (err) {
      console.error('Failed to search lyrics:', err);
    }
  };

  const handleConfirm = async () => {
    if (!searchResult || !searchResult.lyrics) {
      return;
    }

    try {
      await analyzeLyrics({
        title: searchResult.title,
        lyrics: searchResult.lyrics,
      });
    } catch (err) {
      console.error('Failed to analyze lyrics:', err);
    }
  };

  const handleCancel = () => {
    resetSearch();
  };

  const handleReset = () => {
    resetAnalysis();
    resetSearch();
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow py-12 px-4">
        <div className="container mx-auto">
          {!analysis ? (
            <div className="animate-slide-up">
              <div className="text-center mb-12">
                <h1 className="text-4xl md:text-5xl font-bold mb-4 text-white">
                  가사 분석
                </h1>
                <p className="text-[#9f9f9f] text-base md:text-lg">
                  아티스트명과 곡 제목을 입력하면 가사를 검색하여 AI가 분석해드립니다
                </p>
              </div>

              {!searchResult ? (
                <>
                  <LyricsSearchForm onSubmit={handleSearch} isLoading={isSearching} />

                  {searchError && (
                    <div className="mt-6 p-4 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-md text-[#ff2e2e] text-center max-w-4xl mx-auto">
                      {searchError}
                    </div>
                  )}
                </>
              ) : (
                <>
                  <LyricsSearchResult
                    searchResult={searchResult}
                    onConfirm={handleConfirm}
                    onCancel={handleCancel}
                  />

                  {isAnalyzing && (
                    <div className="mt-6 p-4 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-md text-white text-center max-w-4xl mx-auto">
                      <div className="flex items-center justify-center gap-2">
                        <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                        </svg>
                        분석 중...
                      </div>
                    </div>
                  )}

                  {analysisError && (
                    <div className="mt-6 p-4 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-md text-[#ff2e2e] text-center max-w-4xl mx-auto">
                      {analysisError}
                    </div>
                  )}
                </>
              )}
            </div>
          ) : (
            <AnalysisResult analysis={analysis} onReset={handleReset} />
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
