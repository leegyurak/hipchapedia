'use client';

import Link from 'next/link';
import { LyricsItem } from '@/shared/types/lyrics';

interface LyricsCardProps {
  lyrics: LyricsItem;
}

const GENRE_LABELS: Record<string, string> = {
  HIPHOP: 'Hip-Hop',
  RNB: 'R&B',
  KPOP: 'K-POP',
  JPOP: 'J-POP',
  BAND: 'Band',
};

export default function LyricsCard({ lyrics }: LyricsCardProps) {
  const previewText = lyrics.lyrics.slice(0, 150);

  return (
    <div className="p-4 sm:p-5 bg-[#141414] border border-[#1f1f1f] rounded-lg hover:border-[#ff2e2e] transition-all group h-full flex flex-col">
      <div className="flex items-start justify-between mb-3 sm:mb-4">
        <div className="flex-1 min-w-0">
          <h3 className="text-base sm:text-lg md:text-xl font-bold text-white group-hover:text-[#ff2e2e] transition-colors break-keep line-clamp-2 mb-1">
            {lyrics.title}
          </h3>
          {lyrics.artist && (
            <p className="text-[#9f9f9f] text-xs sm:text-sm truncate">
              {lyrics.artist}
            </p>
          )}
        </div>
        <span className="ml-2 px-2 py-1 rounded text-xs font-medium bg-[#ff2e2e]/20 text-[#ff2e2e] whitespace-nowrap flex-shrink-0">
          {GENRE_LABELS[lyrics.genre] || lyrics.genre}
        </span>
      </div>

      <p className="text-[#888888] text-xs sm:text-sm mb-3 sm:mb-4 line-clamp-3 break-keep flex-grow whitespace-pre-wrap">
        {previewText}...
      </p>

      <Link
        href={`/lyrics/${lyrics.id}`}
        className="w-full text-center px-3 sm:px-4 py-2 rounded-lg bg-[#1f1f1f] text-white font-bold text-xs sm:text-sm hover:bg-[#ff2e2e] transition-all transform hover:scale-105 block"
      >
        View Full
      </Link>
    </div>
  );
}
