import { LyricsAnalysisRequest, LyricsAnalysisResponse } from '@/shared/types/lyrics';

export interface ILyricsRepository {
  analyzeLyrics(request: LyricsAnalysisRequest): Promise<LyricsAnalysisResponse>;
  getAnalysis(id: string): Promise<LyricsAnalysisResponse>;
  listAnalyses(): Promise<LyricsAnalysisResponse[]>;
}
