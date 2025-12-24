'use client';

import { useState } from 'react';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import LyricsSearchForm from '@/presentation/components/LyricsSearchForm';
import LyricsSearchResult from '@/presentation/components/LyricsSearchResult';
import AnalysisResult from '@/presentation/components/AnalysisResult';
import { useLyricsAnalysis } from '@/presentation/hooks/useLyricsAnalysis';
import { useLyricsSearch } from '@/presentation/hooks/useLyricsSearch';
import { LyricsSearchRequest, Genre } from '@/shared/types/lyrics';

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

  const handleConfirm = async (genre: Genre) => {
    if (!searchResult || !searchResult.lyrics) {
      return;
    }

    try {
      await analyzeLyrics({
        title: searchResult.title,
        lyrics: searchResult.lyrics,
        genre: genre,
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
                <p className="text-[#9f9f9f] text-sm sm:text-base md:text-lg max-w-3xl mx-auto px-4 break-keep leading-relaxed">
                  Genius 연동을 통해 가사를 검색하여 AI가 분석해드립니다
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
                    isAnalyzing={isAnalyzing}
                  />

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
