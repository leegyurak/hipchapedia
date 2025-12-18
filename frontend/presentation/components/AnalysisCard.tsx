'use client';

import { LyricsAnalysisResponse } from '@/shared/types/lyrics';
import { useState } from 'react';
import AnalysisResult from './AnalysisResult';

interface AnalysisCardProps {
  analysis: LyricsAnalysisResponse;
}

export default function AnalysisCard({ analysis }: AnalysisCardProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  // analysisResult에서 첫 200자만 추출 (마크다운 제외)
  const previewText = analysis.analysisResult
    .replace(/[#*`_\[\]]/g, '') // 마크다운 문자 제거
    .slice(0, 150);

  return (
    <>
      <div className="p-4 sm:p-5 bg-[#141414] border border-[#1f1f1f] rounded-lg hover:border-[#ff2e2e] transition-all group h-full flex flex-col">
        <div className="mb-3 sm:mb-4">
          <h3 className="text-base sm:text-lg md:text-xl font-bold text-white group-hover:text-[#ff2e2e] transition-colors break-keep line-clamp-2">
            {analysis.title}
          </h3>
        </div>

        <p className="text-[#888888] text-xs sm:text-sm mb-3 sm:mb-4 line-clamp-3 break-keep flex-grow">
          {previewText}...
        </p>

        <button
          onClick={() => setIsModalOpen(true)}
          className="w-full text-center px-3 sm:px-4 py-2 rounded-lg bg-[#ff2e2e] text-white font-bold text-xs sm:text-sm hover:bg-[#ff0000] transition-all transform hover:scale-105"
        >
          분석 결과 보기
        </button>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div
          className="fixed inset-0 bg-black/80 flex items-start justify-center z-50 p-4 overflow-y-auto"
          onClick={() => setIsModalOpen(false)}
        >
          <div
            className="relative w-full max-w-5xl my-8"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => setIsModalOpen(false)}
              className="absolute top-4 right-4 z-10 text-white hover:text-[#ff2e2e] transition-colors text-2xl font-bold"
              aria-label="닫기"
            >
              ×
            </button>
            <AnalysisResult analysis={analysis} />
          </div>
        </div>
      )}
    </>
  );
}
