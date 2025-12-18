'use client';

import Link from 'next/link';
import Image from 'next/image';
import toast, { Toaster } from 'react-hot-toast';

export default function Header() {
  const handleGalleryClick = (e: React.MouseEvent) => {
    e.preventDefault();
    toast('개발중입니다!', {
      icon: '🚧',
      style: {
        background: '#141414',
        color: '#fff',
        border: '1px solid #ff2e2e',
      },
      duration: 3000,
    });
  };

  return (
    <header className="sticky top-0 z-50 backdrop-blur-md bg-[#0a0a0a]/95 border-b border-[#1f1f1f]">
      <Toaster position="top-center" />
      <div className="container mx-auto px-3 sm:px-4 md:px-6 py-3 sm:py-4">
        <div className="flex items-center justify-between">
          <Link href="/" className="flex items-center group">
            <Image
              src="/logo.png"
              alt="힙챠피디아"
              width={1400}
              height={100}
              className="h-12 sm:h-14 md:h-16 w-auto group-hover:opacity-80 transition-opacity"
              priority
            />
          </Link>

          <nav className="flex items-center gap-3 sm:gap-4 md:gap-6">
            <Link
              href="/analyze"
              className="text-sm sm:text-base font-bold text-[#888888] hover:text-white transition-colors whitespace-nowrap"
            >
              가사 분석
            </Link>
            <button
              onClick={handleGalleryClick}
              className="text-sm sm:text-base font-bold text-[#888888] hover:text-white transition-colors whitespace-nowrap"
            >
              갤러리
            </button>
          </nav>
        </div>
      </div>
    </header>
  );
}
