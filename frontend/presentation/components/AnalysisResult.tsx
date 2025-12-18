'use client';

import { LyricsAnalysisResponse } from '@/shared/types/lyrics';
import MarkdownContent from './MarkdownContent';
import html2canvas from 'html2canvas';
import { useRef } from 'react';

interface AnalysisResultProps {
  analysis: LyricsAnalysisResponse;
  onReset?: () => void;
}

export default function AnalysisResult({ analysis, onReset }: AnalysisResultProps) {
  const resultRef = useRef<HTMLDivElement>(null);

  const handleSaveAsImage = async () => {
    if (!resultRef.current) return;

    try {
      const canvas = await html2canvas(resultRef.current, {
        backgroundColor: '#1a1a1a',
        scale: 2,
        logging: false,
      });

      const link = document.createElement('a');
      link.download = `${analysis.title}_분석결과.png`;
      link.href = canvas.toDataURL('image/png');
      link.click();
    } catch (error) {
      console.error('Failed to save image:', error);
      alert('이미지 저장에 실패했습니다.');
    }
  };

  return (
    <div className="w-full max-w-5xl mx-auto space-y-4 sm:space-y-6 animate-slide-up px-3 sm:px-4">
      {/* Header */}
      <div ref={resultRef} className="space-y-4 sm:space-y-6">
        <div className="text-center space-y-2 py-2 sm:py-4">
          <h2 className="text-2xl sm:text-3xl md:text-4xl font-bold text-white break-keep">
            {analysis.title}
          </h2>
        </div>

        {/* Analysis Result - Markdown Content */}
        <div className="p-4 sm:p-5 md:p-6 bg-[#141414] border border-[#1f1f1f] rounded-lg">
          <MarkdownContent content={analysis.analysisResult} className="text-sm sm:text-base" />
        </div>
      </div>

      {/* Original Lyrics */}
      <div className="p-4 sm:p-5 md:p-6 bg-[#141414] border border-[#1f1f1f] rounded-lg">
        <h3 className="text-lg sm:text-xl font-bold text-white mb-3 sm:mb-4">원본 가사</h3>
        <pre className="text-[#888888] whitespace-pre-wrap text-xs sm:text-sm leading-relaxed overflow-x-auto break-keep">
          {analysis.lyrics}
        </pre>
      </div>

      {/* Action Buttons */}
      <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 pt-4 pb-2">
        <button
          onClick={onReset}
          className="flex-1 px-6 py-3 rounded-md border border-[#333] text-white font-semibold hover:border-[#ff2e2e] hover:text-[#ff2e2e] transition-all"
        >
          ← 다른 가사 분석하기
        </button>
        <button
          onClick={handleSaveAsImage}
          className="flex-1 px-6 py-3 rounded-md bg-[#ff2e2e] text-white font-semibold hover:bg-[#e62929] transition-all"
        >
          이미지로 저장하기
        </button>
      </div>
    </div>
  );
}
