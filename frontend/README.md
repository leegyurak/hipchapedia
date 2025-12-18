# Hipchapedia - AI Hip-Hop Lyrics Analysis

![Hipchapedia](https://img.shields.io/badge/Hip--Hop-Analysis-00ffff?style=for-the-badge)
![Next.js](https://img.shields.io/badge/Next.js-16.0-black?style=for-the-badge&logo=next.js)
![TypeScript](https://img.shields.io/badge/TypeScript-5-blue?style=for-the-badge&logo=typescript)

Decode the bars with AI-powered hip-hop lyrics analysis. Discover themes, emotions, rhyme schemes, and wordplay.

## Features

- **AI-Powered Analysis**: Deep analysis of hip-hop lyrics using AI
- **Theme Detection**: Identify core themes and messages
- **Technical Breakdown**: Analyze rhyme schemes, flow, and wordplay
- **Emotional Insight**: Understand the emotional landscape of lyrics
- **Community Gallery**: Explore analyzed tracks from the community
- **Clean Architecture**: Built with maintainable, scalable architecture

## Tech Stack

- **Framework**: Next.js 16 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4
- **Architecture**: Clean Architecture
- **State Management**: React Hooks
- **API Communication**: Fetch API with Repository Pattern

## Project Structure

```
frontend/
├── app/                        # Next.js app router pages
│   ├── analyze/               # Lyrics analysis page
│   ├── gallery/               # Analysis gallery page
│   ├── analysis/[id]/         # Individual analysis detail page
│   ├── layout.tsx             # Root layout
│   ├── page.tsx               # Home page
│   └── globals.css            # Global styles
├── core/                      # Core business logic (Clean Architecture)
│   ├── entities/              # Domain entities
│   ├── usecases/              # Business use cases
│   └── interfaces/            # Repository interfaces
├── infrastructure/            # External services & implementations
│   └── api/                   # API repositories
├── presentation/              # UI layer
│   ├── components/            # React components
│   └── hooks/                 # Custom React hooks
└── shared/                    # Shared utilities
    ├── types/                 # TypeScript types
    ├── utils/                 # Utility functions
    └── constants/             # Constants
```

## Getting Started

### Prerequisites

- Node.js 20+
- npm or yarn
- Backend API running (see ../backend)

### Installation

1. Install dependencies:

```bash
npm install
```

2. Set up environment variables:

```bash
cp .env.example .env.local
```

Edit `.env.local` and configure:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

3. Run the development server:

```bash
npm run dev
```

4. Open [http://localhost:3000](http://localhost:3000) in your browser

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm run lint` - Run ESLint

## Design System

### Color Palette

- **Neon Cyan**: `#00ffff` - Primary accent
- **Neon Pink**: `#ff00ff` - Secondary accent
- **Neon Yellow**: `#ffff00` - Tertiary accent
- **Dark Background**: `#0a0a0a` - Main background
- **Card Background**: `#1a1a1a` - Component background
- **Border**: `#333333` - Border color

### Typography

- Headings: Bold, large, with glow effects
- Body: Clean, readable sans-serif
- Code/Lyrics: Monospace font

### Components

- **Glow Effects**: Text and border glow for neon aesthetic
- **Animations**: Smooth slide-up and pulse animations
- **Custom Scrollbar**: Themed scrollbar with neon colors

## API Integration

The frontend communicates with the backend through the Repository Pattern:

```typescript
// Example: Analyzing lyrics
const repository = new LyricsApiRepository();
const useCase = new AnalyzeLyricsUseCase(repository);
const result = await useCase.execute({
  title: "Song Title",
  lyrics: "Lyrics content..."
});
```

## Clean Architecture Layers

1. **Entities** (`core/entities`): Domain models
2. **Use Cases** (`core/usecases`): Business logic
3. **Interfaces** (`core/interfaces`): Contracts for repositories
4. **Infrastructure** (`infrastructure`): External service implementations
5. **Presentation** (`presentation`): UI components and hooks

## Contributing

1. Follow the existing code structure
2. Use TypeScript for type safety
3. Maintain Clean Architecture principles
4. Keep components small and focused
5. Use custom hooks for business logic

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_URL` | Backend API URL | `http://localhost:8080` |

## License

MIT

## Support

For issues and questions, please open an issue on GitHub.

---

Built with 💎 for the hip-hop community
