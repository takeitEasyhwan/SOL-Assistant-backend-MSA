package com.donttouch.external_assistant_service.domain.gemini.prompt;

import org.springframework.stereotype.Component;

@Component
public class GeminiPromptBuilder {

    public String buildJsonSectorAnalysisPrompt(String sectorName, String mergedNewsText) {
        return String.format("""
            너는 산업 분석 전문가다.
            다음 뉴스들은 '%s' 섹터와 관련된 기사들이다.
            모든 뉴스를 통합하여 해당 섹터의 전반적인 상황을 요약하고,
            시장 흐름과 산업 방향을 객관적으로 분석하라.
            투자나 매수 권유와 같은 표현은 절대 사용하지 마라.

            반드시 아래 JSON 형식으로 출력하라:
            {
              "sector": "%s",
              "analysis": "[요약과 전망을 합친 분석 내용, 3~6문장]",
              "sentiment": "[긍정 | 중립 | 부정]"
            }

            뉴스:
            \"\"\"%s\"\"\"
            """, sectorName, sectorName, mergedNewsText);
    }
}