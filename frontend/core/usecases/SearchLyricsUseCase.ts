import { LyricsSearchRequest, LyricsSearchResponse } from '@/shared/types/lyrics';
import { LyricsApiRepository } from '@/infrastructure/api/LyricsApiRepository';

export class SearchLyricsUseCase {
  constructor(private lyricsRepository: LyricsApiRepository) {}

  async execute(request: LyricsSearchRequest): Promise<LyricsSearchResponse | null> {
    if (!request.title || !request.artist) {
      throw new Error('Title and artist are required');
    }

    return await this.lyricsRepository.searchLyrics(request);
  }
}
