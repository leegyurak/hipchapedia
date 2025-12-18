'use client';

import { useState } from 'react';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import LyricsForm from '@/presentation/components/LyricsForm';
import AnalysisResult from '@/presentation/components/AnalysisResult';
import { useLyricsAnalysis } from '@/presentation/hooks/useLyricsAnalysis';
import { LyricsAnalysisRequest } from '@/shared/types/lyrics';

export default function AnalyzePage() {
  const { isLoading, error, analysis, analyzeLyrics, reset } = useLyricsAnalysis();

  const handleSubmit = async (data: LyricsAnalysisRequest) => {
    try {
      await analyzeLyrics(data);
    } catch (err) {
      console.error('Failed to analyze lyrics:', err);
    }
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
                  가사를 입력하면 AI가 분석해드립니다
                </p>
              </div>

              <LyricsForm onSubmit={handleSubmit} isLoading={isLoading} />

              {error && (
                <div className="mt-6 p-4 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-md text-[#ff2e2e] text-center max-w-4xl mx-auto">
                  {error}
                </div>
              )}
            </div>
          ) : (
            <AnalysisResult analysis={analysis} onReset={reset} />
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
