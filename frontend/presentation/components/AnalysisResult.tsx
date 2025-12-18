'use client';

import { LyricsAnalysisResponse } from '@/shared/types/lyrics';
import MarkdownContent from './MarkdownContent';

interface AnalysisResultProps {
  analysis: LyricsAnalysisResponse;
}

export default function AnalysisResult({ analysis }: AnalysisResultProps) {
  return (
    <div className="w-full max-w-5xl mx-auto space-y-4 sm:space-y-6 animate-slide-up px-3 sm:px-4">
      {/* Header */}
      <div className="text-center space-y-2 py-2 sm:py-4">
        <h2 className="text-2xl sm:text-3xl md:text-4xl font-bold text-white break-keep">
          {analysis.title}
        </h2>
      </div>

      {/* Analysis Result - Markdown Content */}
      <div className="p-4 sm:p-5 md:p-6 bg-[#141414] border border-[#1f1f1f] rounded-lg">
        <MarkdownContent content={analysis.analysisResult} className="text-sm sm:text-base" />
      </div>


      {/* Original Lyrics */}
      <div className="p-4 sm:p-5 md:p-6 bg-[#141414] border border-[#1f1f1f] rounded-lg">
        <h3 className="text-lg sm:text-xl font-bold text-white mb-3 sm:mb-4">원본 가사</h3>
        <pre className="text-[#888888] whitespace-pre-wrap text-xs sm:text-sm leading-relaxed overflow-x-auto break-keep">
          {analysis.lyrics}
        </pre>
      </div>
    </div>
  );
}
