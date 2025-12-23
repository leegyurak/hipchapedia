'use client';

import { LyricsSearchResponse } from '@/shared/types/lyrics';

interface LyricsSearchResultProps {
  searchResult: LyricsSearchResponse;
  onConfirm: () => void;
  onCancel: () => void;
  isAnalyzing?: boolean;
}

export default function LyricsSearchResult({ searchResult, onConfirm, onCancel, isAnalyzing = false }: LyricsSearchResultProps) {
  return (
    <div className="w-full max-w-4xl mx-auto animate-slide-up">
      <div className="bg-[#141414] border border-[#1f1f1f] rounded-lg p-6 space-y-6">
        <div className="text-center mb-4">
          <h2 className="text-2xl md:text-3xl font-bold text-white mb-2">
            검색 결과
          </h2>
          <p className="text-[#9f9f9f] text-sm md:text-base">
            이 곡이 맞나요? 확인하시면 가사 분석을 시작합니다.
          </p>
        </div>

        <div className="space-y-4">
          <div className="space-y-2">
            <div className="text-sm font-semibold text-[#9f9f9f]">제목</div>
            <div className="text-lg font-bold text-white">{searchResult.title}</div>
          </div>

          <div className="space-y-2">
            <div className="text-sm font-semibold text-[#9f9f9f]">아티스트</div>
            <div className="text-lg font-bold text-white">{searchResult.artist}</div>
          </div>

          {searchResult.album && (
            <div className="space-y-2">
              <div className="text-sm font-semibold text-[#9f9f9f]">앨범</div>
              <div className="text-base text-white">{searchResult.album}</div>
            </div>
          )}

          {searchResult.releaseDate && (
            <div className="space-y-2">
              <div className="text-sm font-semibold text-[#9f9f9f]">발매일</div>
              <div className="text-base text-white">{searchResult.releaseDate}</div>
            </div>
          )}

          {searchResult.lyrics && (
            <div className="space-y-2">
              <div className="text-sm font-semibold text-[#9f9f9f]">가사 미리보기</div>
              <div className="text-sm text-[#cccccc] whitespace-pre-line max-h-60 overflow-y-auto bg-[#0a0a0a] p-4 rounded border border-[#1f1f1f]">
                {searchResult.lyrics.slice(0, 500)}
                {searchResult.lyrics.length > 500 && '...'}
              </div>
            </div>
          )}
        </div>

        <div className="flex gap-4 pt-4">
          <button
            onClick={onCancel}
            disabled={isAnalyzing}
            className="flex-1 px-6 py-3 rounded-lg bg-[#1f1f1f] text-white font-semibold hover:bg-[#2a2a2a] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            다시 검색
          </button>
          <button
            onClick={onConfirm}
            disabled={isAnalyzing}
            className="flex-1 px-6 py-3 rounded-lg bg-[#ff2e2e] text-white font-bold hover:bg-[#ff0000] transition-all transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
          >
            {isAnalyzing ? (
              <span className="flex items-center justify-center gap-2">
                <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                분석 중...
              </span>
            ) : (
              '분석 시작'
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
