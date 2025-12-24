'use client';

import { useEffect, useState, useRef } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { notFound } from 'next/navigation';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import MarkdownContent from '@/presentation/components/MarkdownContent';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';
import { LyricsAnalysisResponse } from '@/shared/types/lyrics';
import html2canvas from 'html2canvas';

const repository = new LyricsApiRepository();

export default function LyricsDetailPage() {
  const params = useParams();
  const router = useRouter();
  const resultRef = useRef<HTMLDivElement>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [analysis, setAnalysis] = useState<LyricsAnalysisResponse | null>(null);

  useEffect(() => {
    const fetchAnalysis = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const id = parseInt(params.id as string);

        if (isNaN(id)) {
          notFound();
          return;
        }

        const result = await repository.getAnalysis(id);
        setAnalysis(result);
      } catch (err) {
        if (err instanceof Error && err.message.includes('찾을 수 없습니다')) {
          notFound();
        } else {
          setError(err instanceof Error ? err.message : 'Failed to load analysis');
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchAnalysis();
  }, [params.id]);

  const handleBack = () => {
    router.push('/gallery');
  };

  const handleSaveAsImage = async () => {
    if (!resultRef.current) return;

    try {
      const canvas = await html2canvas(resultRef.current, {
        backgroundColor: '#1a1a1a',
        scale: 2,
        logging: false,
      });

      const link = document.createElement('a');
      link.download = `${analysis?.title}_analysis.png`;
      link.href = canvas.toDataURL('image/png');
      link.click();
    } catch (error) {
      console.error('Failed to save image:', error);
      alert('Failed to save image');
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow py-12 px-4">
        <div className="container mx-auto">
          {isLoading && (
            <div className="flex justify-center items-center py-20">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#ff2e2e]"></div>
            </div>
          )}

          {error && (
            <div className="max-w-4xl mx-auto">
              <div className="p-6 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-lg text-[#ff2e2e] text-center mb-6">
                {error}
              </div>
              <div className="text-center">
                <button
                  onClick={handleBack}
                  className="px-6 py-3 rounded-lg bg-[#ff2e2e] text-white font-medium hover:bg-[#ff0000] transition-all"
                >
                  Back
                </button>
              </div>
            </div>
          )}

          {analysis && (
            <div className="w-full max-w-5xl mx-auto space-y-4 sm:space-y-6 animate-slide-up px-3 sm:px-4">
              <div ref={resultRef} className="space-y-4 sm:space-y-6">
                <div className="text-center space-y-2 py-2 sm:py-4">
                  <h2 className="text-2xl sm:text-3xl md:text-4xl font-bold text-white break-keep">
                    {analysis.title}
                  </h2>
                </div>

                <div className="p-4 sm:p-5 md:p-6 bg-[#141414] border border-[#1f1f1f] rounded-lg">
                  <MarkdownContent content={analysis.analysisResult} className="text-sm sm:text-base" />
                </div>
              </div>

              <div className="p-4 sm:p-5 md:p-6 bg-[#141414] border border-[#1f1f1f] rounded-lg">
                <h3 className="text-lg sm:text-xl font-bold text-white mb-3 sm:mb-4">원본 가사</h3>
                <pre className="text-[#888888] whitespace-pre-wrap text-xs sm:text-sm leading-relaxed overflow-x-auto break-keep">
                  {analysis.lyrics}
                </pre>
              </div>

              <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 pt-4 pb-2">
                <button
                  onClick={handleBack}
                  className="flex-1 px-6 py-3 rounded-md border border-[#333] text-white font-semibold hover:border-[#ff2e2e] hover:text-[#ff2e2e] transition-all"
                >
                  ← 갤러리로 돌아가기
                </button>
                <button
                  onClick={handleSaveAsImage}
                  className="flex-1 px-6 py-3 rounded-md bg-[#ff2e2e] text-white font-semibold hover:bg-[#e62929] transition-all"
                >
                  이미지로 저장하기
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
