'use client';

import Link from 'next/link';
import toast from 'react-hot-toast';

export default function HeroSection() {
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
    <section className="w-full px-3 sm:px-4">
      <div className="container mx-auto max-w-6xl">
        <div className="animate-slide-up text-center mb-12 sm:mb-16">
          <h1 className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-black mb-4 sm:mb-6 text-white px-2 break-keep leading-tight tracking-tight">
            가사 속 진짜 의미를
            <br />
            <span className="text-[#ff2e2e]">찾아보세요</span>
          </h1>

          <p className="text-base sm:text-lg md:text-xl text-[#888888] mb-8 sm:mb-10 max-w-2xl mx-auto px-4 break-keep font-medium">
            AI가 음악 가사를 분석해드립니다
            <br />
            힙합, R&B부터 K-POP까지 한눈에
          </p>

          <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center items-center px-4">
            <Link
              href="/analyze"
              className="w-full sm:w-auto px-8 sm:px-10 py-3.5 sm:py-4 rounded-lg bg-[#ff2e2e] text-white font-bold text-sm sm:text-base hover:bg-[#ff0000] transition-all transform hover:scale-105"
            >
              가사 분석하기
            </Link>
            <button
              onClick={handleGalleryClick}
              className="w-full sm:w-auto px-8 sm:px-10 py-3.5 sm:py-4 rounded-lg border border-[#1f1f1f] text-[#888888] font-bold text-sm sm:text-base hover:border-[#ff2e2e] hover:text-white transition-all"
            >
              분석 둘러보기
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:gap-5">
          {[
            {
              title: '주제 분석',
              description: '곡의 핵심 메시지와 주제를 파악합니다',
              icon: '🎯'
            },
            {
              title: '기술 분석',
              description: '라임 스킴, 플로우, 워드플레이 분석',
              icon: '⚡'
            },
            {
              title: '감정 분석',
              description: '가사가 담고 있는 감정을 분석합니다',
              icon: '💎'
            }
          ].map((feature, index) => (
            <div
              key={index}
              className="p-6 sm:p-7 rounded-lg bg-[#141414] border border-[#1f1f1f] hover:border-[#ff2e2e] transition-all animate-slide-up group"
              style={{
                animationDelay: `${index * 0.1}s`
              }}
            >
              <div className="text-4xl sm:text-5xl mb-3 sm:mb-4">{feature.icon}</div>
              <h3 className="text-lg sm:text-xl font-bold mb-2 text-white break-keep">
                {feature.title}
              </h3>
              <p className="text-[#888888] text-sm sm:text-base break-keep">{feature.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
