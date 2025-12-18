import { ILyricsRepository } from '@/core/interfaces/ILyricsRepository';
import { LyricsAnalysisRequest, LyricsAnalysisResponse } from '@/shared/types/lyrics';

export class LyricsApiRepository implements ILyricsRepository {
  private baseUrl: string;

  constructor(baseUrl: string = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080') {
    this.baseUrl = baseUrl;
  }

  async analyzeLyrics(request: LyricsAnalysisRequest): Promise<LyricsAnalysisResponse> {
    try {
      const response = await fetch(`${this.baseUrl}/api/lyrics/analyze`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`분석에 실패했습니다: ${errorText || response.statusText}`);
      }

      return response.json();
    } catch (error) {
      if (error instanceof TypeError && error.message.includes('fetch')) {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      throw error;
    }
  }

  async getAnalysis(id: string): Promise<LyricsAnalysisResponse> {
    try {
      const response = await fetch(`${this.baseUrl}/api/lyrics/${id}`);

      if (!response.ok) {
        throw new Error(`분석 결과를 가져오는데 실패했습니다: ${response.statusText}`);
      }

      return response.json();
    } catch (error) {
      if (error instanceof TypeError && error.message.includes('fetch')) {
        throw new Error('백엔드 서버에 연결할 수 없습니다.');
      }
      throw error;
    }
  }

  async listAnalyses(): Promise<LyricsAnalysisResponse[]> {
    try {
      const response = await fetch(`${this.baseUrl}/api/lyrics`);

      if (!response.ok) {
        throw new Error(`목록을 가져오는데 실패했습니다: ${response.statusText}`);
      }

      return response.json();
    } catch (error) {
      if (error instanceof TypeError && error.message.includes('fetch')) {
        throw new Error('백엔드 서버에 연결할 수 없습니다.');
      }
      throw error;
    }
  }
}
