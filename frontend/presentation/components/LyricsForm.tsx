'use client';

import { useState } from 'react';
import { LyricsAnalysisRequest } from '@/shared/types/lyrics';

interface LyricsFormProps {
  onSubmit: (data: LyricsAnalysisRequest) => void;
  isLoading?: boolean;
}

export default function LyricsForm({ onSubmit, isLoading = false }: LyricsFormProps) {
  const [title, setTitle] = useState('');
  const [lyrics, setLyrics] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (title.trim() && lyrics.trim()) {
      onSubmit({ title: title.trim(), lyrics: lyrics.trim() });
    }
  };

  const isValid = title.trim().length > 0 && lyrics.trim().length >= 10;

  return (
    <form onSubmit={handleSubmit} className="w-full max-w-4xl mx-auto space-y-4 sm:space-y-6 px-3 sm:px-4">
      <div className="space-y-1.5 sm:space-y-2">
        <label htmlFor="title" className="block text-sm sm:text-base font-semibold text-white">
          곡 제목
        </label>
        <input
          id="title"
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="예) Dingo freestyle"
          disabled={isLoading}
          className="w-full px-3 sm:px-4 py-2.5 sm:py-3 bg-[#141414] border border-[#1f1f1f] rounded-lg text-white text-sm sm:text-base placeholder-[#555555] focus:border-[#ff2e2e] focus:outline-none transition-colors disabled:opacity-50"
        />
      </div>

      <div className="space-y-1.5 sm:space-y-2">
        <label htmlFor="lyrics" className="block text-sm sm:text-base font-semibold text-white">
          가사
        </label>
        <textarea
          id="lyrics"
          value={lyrics}
          onChange={(e) => setLyrics(e.target.value)}
          placeholder="가사를 입력하세요...&#10;&#10;1절:&#10;...&#10;&#10;후렴:&#10;..."
          disabled={isLoading}
          rows={10}
          className="w-full px-3 sm:px-4 py-2.5 sm:py-3 bg-[#141414] border border-[#1f1f1f] rounded-lg text-white text-sm sm:text-base placeholder-[#555555] focus:border-[#ff2e2e] focus:outline-none transition-colors resize-y disabled:opacity-50 min-h-[200px] sm:min-h-[300px]"
        />
        <div className="text-xs sm:text-sm text-[#888888]">
          {lyrics.length}자
        </div>
      </div>

      <button
        type="submit"
        disabled={!isValid || isLoading}
        className="w-full px-6 sm:px-8 py-3.5 sm:py-4 rounded-lg bg-[#ff2e2e] text-white font-bold text-sm sm:text-base hover:bg-[#ff0000] transition-all transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
      >
        {isLoading ? (
          <span className="flex items-center justify-center gap-2">
            <svg className="animate-spin h-4 sm:h-5 w-4 sm:w-5" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
            분석 중...
          </span>
        ) : (
          '가사 분석하기'
        )}
      </button>

      {!isValid && lyrics.length > 0 && lyrics.length < 10 && (
        <p className="text-[#ff2e2e] text-xs sm:text-sm text-center">
          가사는 최소 10자 이상 입력해주세요
        </p>
      )}
    </form>
  );
}
