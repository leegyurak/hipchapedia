import { LyricsAnalysisRequest, LyricsAnalysisResponse, LyricsListResponse, Genre } from '@/shared/types/lyrics';

export interface ILyricsRepository {
  analyzeLyrics(request: LyricsAnalysisRequest): Promise<LyricsAnalysisResponse>;
  getAnalysis(id: number): Promise<LyricsAnalysisResponse>;
  listAnalyses(cursor?: number, limit?: number, genre?: Genre, artist?: string): Promise<LyricsListResponse>;
}
