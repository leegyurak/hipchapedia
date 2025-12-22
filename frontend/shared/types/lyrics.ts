export interface LyricsAnalysisRequest {
  title: string;
  lyrics: string;
}

export interface LyricsAnalysisResponse {
  title: string;
  lyrics: string;
  analysisResult: string; // Markdown 형식의 분석 결과
}

export interface LyricsSearchRequest {
  title: string;
  artist: string;
}

export interface LyricsSearchResponse {
  title: string;
  artist: string;
  lyrics: string | null;
  url: string | null;
  album: string | null;
  releaseDate: string | null;
}
