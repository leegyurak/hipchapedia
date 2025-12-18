export class Lyrics {
  constructor(
    public readonly id: string,
    public readonly title: string,
    public readonly lyrics: string,
    public readonly createdAt: Date
  ) {}

  static create(title: string, lyrics: string): Omit<Lyrics, 'id' | 'createdAt'> {
    return {
      title,
      lyrics,
    } as Omit<Lyrics, 'id' | 'createdAt'>;
  }
}

export class LyricsAnalysis {
  constructor(
    public readonly id: string,
    public readonly lyrics: Lyrics,
    public readonly theme: string,
    public readonly emotion: string,
    public readonly style: string,
    public readonly keyPhrases: string[],
    public readonly overallSummary: string,
    public readonly technicalAnalysis?: {
      rhymeScheme?: string;
      flow?: string;
      wordplay?: string[];
    }
  ) {}
}
