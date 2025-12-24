export default function Footer() {
  const footerText = '\u00A9 2025 \uD799\uCC60\uD53C\uB514\uC544. AI \uAE30\uBC18 \uC74C\uC545 \uAC00\uC0AC \uBD84\uC11D';

  return (
    <footer className="border-t border-[#1f1f1f] bg-[#0a0a0a] mt-20">
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center">
          <div className="text-sm text-[#555555] font-medium">
            {footerText}
          </div>
        </div>
      </div>
    </footer>
  );
}
