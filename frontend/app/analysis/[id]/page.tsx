'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import AnalysisResult from '@/presentation/components/AnalysisResult';
import { LyricsAnalysisResponse } from '@/shared/types/lyrics';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';

export default function AnalysisDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const [analysis, setAnalysis] = useState<LyricsAnalysisResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAnalysis = async () => {
      if (!id) return;

      try {
        setIsLoading(true);
        const repository = new LyricsApiRepository();
        const result = await repository.getAnalysis(id);
        setAnalysis(result);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to fetch analysis';
        setError(errorMessage);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAnalysis();
  }, [id]);

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow py-12 px-4">
        <div className="container mx-auto">
          {isLoading && (
            <div className="flex items-center justify-center py-20">
              <div className="text-center">
                <svg className="animate-spin h-12 w-12 text-[#ff2e2e] mx-auto mb-4" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                <p className="text-[#9f9f9f]">로딩 중...</p>
              </div>
            </div>
          )}

          {error && (
            <div className="p-6 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-md text-[#ff2e2e] text-center max-w-2xl mx-auto">
              {error}
            </div>
          )}

          {!isLoading && !error && analysis && (
            <>
              <div className="mb-8 text-center">
                <a
                  href="/gallery"
                  className="inline-block px-6 py-3 rounded-md border border-[#333] text-white font-semibold hover:border-[#ff2e2e] hover:text-[#ff2e2e] transition-all"
                >
                  ← 갤러리로 돌아가기
                </a>
              </div>
              <AnalysisResult analysis={analysis} />
            </>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
