export interface LyricsAnalysisRequest {
  title: string;
  lyrics: string;
}

export interface LyricsAnalysisResponse {
  title: string;
  lyrics: string;
  analysisResult: string; // Markdown 형식의 분석 결과
}
