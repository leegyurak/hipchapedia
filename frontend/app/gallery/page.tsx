'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import LyricsCard from '@/presentation/components/LyricsCard';
import { useLyricsList } from '@/presentation/hooks/useLyricsList';
import { Genre } from '@/shared/types/lyrics';

const GENRES: { value: Genre | 'ALL'; label: string }[] = [
  { value: 'ALL', label: '전체' },
  { value: 'HIPHOP', label: '힙합' },
  { value: 'RNB', label: 'R&B' },
  { value: 'KPOP', label: 'K-POP' },
  { value: 'JPOP', label: 'J-POP' },
  { value: 'BAND', label: '밴드' },
];

export default function GalleryPage() {
  const [selectedGenre, setSelectedGenre] = useState<Genre | 'ALL'>('ALL');
  const [artistSearch, setArtistSearch] = useState('');
  const [artistInput, setArtistInput] = useState('');

  const { isLoading, isLoadingMore, error, lyrics, hasMore, loadMore } = useLyricsList({
    genre: selectedGenre === 'ALL' ? undefined : selectedGenre,
    artist: artistSearch || undefined,
  });

  const observerTarget = useRef<HTMLDivElement>(null);

  const handleArtistSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setArtistSearch(artistInput.trim());
  };

  const handleClearArtist = () => {
    setArtistInput('');
    setArtistSearch('');
  };

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !isLoadingMore) {
          loadMore();
        }
      },
      { threshold: 0.5 }
    );

    const currentTarget = observerTarget.current;
    if (currentTarget) {
      observer.observe(currentTarget);
    }

    return () => {
      if (currentTarget) {
        observer.unobserve(currentTarget);
      }
    };
  }, [hasMore, isLoadingMore, loadMore]);

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow py-12 px-4">
        <div className="container mx-auto">
          <div className="text-center mb-12 animate-slide-up">
            <h1 className="text-4xl md:text-5xl font-bold mb-4 text-white">
              분석 갤러리
            </h1>
            <p className="text-[#9f9f9f] text-sm sm:text-base md:text-lg max-w-3xl mx-auto px-4 break-keep leading-relaxed">
              다른 사람들이 분석한 가사들을 둘러보세요
            </p>
          </div>

          {/* Artist Search */}
          <form onSubmit={handleArtistSearch} className="max-w-2xl mx-auto mb-8">
            <div className="flex gap-2">
              <div className="flex-1 relative">
                <input
                  type="text"
                  value={artistInput}
                  onChange={(e) => setArtistInput(e.target.value)}
                  placeholder="아티스트 이름으로 검색..."
                  className="w-full px-4 py-3 rounded-lg bg-[#141414] border border-[#1f1f1f] text-white placeholder-[#888888] focus:outline-none focus:border-[#ff2e2e] transition-all"
                />
                {artistSearch && (
                  <button
                    type="button"
                    onClick={handleClearArtist}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-[#888888] hover:text-white transition-colors"
                  >
                    ✕
                  </button>
                )}
              </div>
              <button
                type="submit"
                className="px-6 py-3 rounded-lg bg-[#ff2e2e] text-white font-medium hover:bg-[#ff0000] transition-all"
              >
                검색
              </button>
            </div>
          </form>

          {/* Genre Filter - 3x2 Grid */}
          <div className="grid grid-cols-3 gap-2 max-w-md mx-auto mb-8">
            {GENRES.map(genre => (
              <button
                key={genre.value}
                onClick={() => setSelectedGenre(genre.value)}
                className={`px-3 py-2 rounded-lg font-medium transition-all text-sm sm:text-base ${
                  selectedGenre === genre.value
                    ? 'bg-[#ff2e2e] text-white'
                    : 'bg-[#141414] text-[#888888] border border-[#1f1f1f] hover:border-[#ff2e2e] hover:text-white'
                }`}
              >
                {genre.label}
              </button>
            ))}
          </div>

          {/* Error */}
          {error && (
            <div className="mb-6 p-4 bg-[#ff2e2e]/10 border border-[#ff2e2e] rounded-md text-[#ff2e2e] text-center max-w-4xl mx-auto">
              {error}
            </div>
          )}

          {/* Loading */}
          {isLoading ? (
            <div className="flex justify-center items-center py-20">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#ff2e2e]"></div>
            </div>
          ) : lyrics.length === 0 ? (
            <div className="text-center py-20">
              <p className="text-[#9f9f9f] text-base mb-6">아직 분석된 가사가 없습니다. 첫 번째로 분석해보세요!</p>
              <a
                href="/analyze"
                className="inline-block px-8 py-3 rounded-md bg-[#ff2e2e] text-white font-bold hover:bg-[#ff0000] transition-colors"
              >
                가사 분석하기
              </a>
            </div>
          ) : (
            <>
              {/* Lyrics Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {lyrics.map((item, index) => (
                  <div
                    key={item.id}
                    className="animate-slide-up"
                    style={{ animationDelay: `${index * 0.05}s` }}
                  >
                    <LyricsCard lyrics={item} />
                  </div>
                ))}
              </div>

              {/* Infinite Scroll Observer */}
              {hasMore && (
                <div ref={observerTarget} className="flex justify-center py-8">
                  {isLoadingMore && (
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff2e2e]"></div>
                  )}
                </div>
              )}
            </>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}
