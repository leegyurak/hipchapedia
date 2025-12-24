import Link from 'next/link';
import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';

export default function NotFound() {
  const title = '\uD398\uC774\uC9C0\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4';
  const description = '\uC694\uCCAD\uD558\uC2E0 \uD398\uC774\uC9C0\uAC00 \uC874\uC7AC\uD558\uC9C0 \uC54A\uAC70\uB098 \uC774\uB3D9\uB418\uC5C8\uC744 \uC218 \uC788\uC2B5\uB2C8\uB2E4.';
  const homeButton = '\uD648\uC73C\uB85C \uAC00\uAE30';
  const galleryButton = '\uAC24\uB7EC\uB9AC \uBCF4\uAE30';

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow flex items-center justify-center px-4 py-12">
        <div className="text-center space-y-6 max-w-2xl mx-auto">
          <div className="space-y-4">
            <h1 className="text-8xl sm:text-9xl font-black text-[#ff2e2e]">404</h1>
            <h2 className="text-2xl sm:text-3xl md:text-4xl font-bold text-white">
              {title}
            </h2>
            <p className="text-[#888888] text-base sm:text-lg px-4">
              {description}
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center items-center pt-6">
            <Link
              href="/"
              className="w-full sm:w-auto px-8 py-3 rounded-lg bg-[#ff2e2e] text-white font-bold hover:bg-[#ff0000] transition-all"
            >
              {homeButton}
            </Link>
            <Link
              href="/gallery"
              className="w-full sm:w-auto px-8 py-3 rounded-lg border border-[#1f1f1f] text-[#888888] font-bold hover:border-[#ff2e2e] hover:text-white transition-all"
            >
              {galleryButton}
            </Link>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
