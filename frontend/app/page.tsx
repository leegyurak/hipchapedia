import Header from '@/presentation/components/Header';
import Footer from '@/presentation/components/Footer';
import HeroSection from '@/presentation/components/HeroSection';

export default function Home() {
  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-grow flex items-center justify-center pt-12 lg:pt-16">
        <HeroSection />
      </main>
      <Footer />
    </div>
  );
}
