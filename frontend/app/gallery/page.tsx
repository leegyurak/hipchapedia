'use client';

import { useEffect } from 'react';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import AnalysisCard from '@/presentation/components/AnalysisCard';
import { useLyricsList } from '@/presentation/hooks/useLyricsList';
import toast, { Toaster } from 'react-hot-toast';

export default function GalleryPage() {
  const { isLoading, error, analyses } = useLyricsList();

  useEffect(() => {
    toast('개발중입니다!', {
      icon: '🚧',
      style: {
        background: '#141414',
        color: '#fff',
        border: '1px solid #ff2e2e',
      },
    });
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <Toaster position="top-center" />
      <Header />
      <main className="flex-grow py-12 px-4">
        <div className="container mx-auto">
          <div className="text-center mb-12 animate-slide-up">
            <h1 className="text-4xl md:text-5xl font-bold mb-4 text-white">
              분석 갤러리
            </h1>
            <p className="text-[#9f9f9f] text-base md:text-lg">
              다른 사람들이 분석한 가사를 둘러보세요
            </p>
          </div>

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

          {!isLoading && !error && analyses.length === 0 && (
            <div className="text-center py-20">
              <p className="text-[#9f9f9f] text-base mb-6">아직 분석된 가사가 없습니다. 첫 번째로 분석해보세요!</p>
              <a
                href="/analyze"
                className="inline-block px-8 py-3 rounded-md bg-[#ff2e2e] text-white font-bold hover:bg-[#ff0000] transition-colors"
              >
                가사 분석하기
              </a>
            </div>
          )}

          {!isLoading && !error && analyses.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {analyses.map((analysis, index) => (
                <div
                  key={`${analysis.title}-${index}`}
                  className="animate-slide-up"
                  style={{ animationDelay: `${index * 0.05}s` }}
                >
                  <AnalysisCard analysis={analysis} />
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
