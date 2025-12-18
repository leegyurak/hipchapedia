export default function Footer() {
  return (
    <footer className="border-t border-[#1f1f1f] bg-[#0a0a0a] mt-20">
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="text-sm text-[#555555] font-medium">
            © 2025 힙챠피디아. AI 기반 힙합 가사 분석
          </div>
          <div className="flex gap-6 text-sm">
            <a href="#" className="text-[#888888] hover:text-white transition-colors font-medium">
              소개
            </a>
            <a href="#" className="text-[#888888] hover:text-white transition-colors font-medium">
              문의
            </a>
            <a href="#" className="text-[#888888] hover:text-white transition-colors font-medium">
              이용약관
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
