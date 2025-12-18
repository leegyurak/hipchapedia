import { ILyricsRepository } from '../interfaces/ILyricsRepository';
import { LyricsAnalysisRequest, LyricsAnalysisResponse } from '@/shared/types/lyrics';

export class AnalyzeLyricsUseCase {
  constructor(private lyricsRepository: ILyricsRepository) {}

  async execute(request: LyricsAnalysisRequest): Promise<LyricsAnalysisResponse> {
    if (!request.title || !request.lyrics) {
      throw new Error('Title and lyrics are required');
    }

    if (request.lyrics.length < 10) {
      throw new Error('Lyrics are too short');
    }

    return await this.lyricsRepository.analyzeLyrics(request);
  }
}
