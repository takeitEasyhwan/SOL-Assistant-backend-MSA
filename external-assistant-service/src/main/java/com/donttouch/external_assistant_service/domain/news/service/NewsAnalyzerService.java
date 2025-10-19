package com.donttouch.external_assistant_service.domain.news.service;

import com.donttouch.common_service.sector.entity.Sector;
import com.donttouch.external_assistant_service.domain.exception.ErrorMessage;
import com.donttouch.external_assistant_service.domain.exception.NewsNotFoundException;
import com.donttouch.external_assistant_service.domain.gemini.prompt.GeminiPromptBuilder;
import com.donttouch.external_assistant_service.domain.gemini.service.GeminiService;
import com.donttouch.external_assistant_service.domain.news.entity.Emotion;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import com.donttouch.external_assistant_service.domain.news.entity.vo.SectorAnalysisResponse;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsSummaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class NewsAnalyzerService {

    private final GeminiService geminiService;
    private final GeminiPromptBuilder promptBuilder;
    private final SectorNewsSummaryRepository summaryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SectorNewsSummary analyze(String sectorName, List<SectorNews> newsList) {
        try {
            // 1️⃣ 뉴스 본문 병합
            StringBuilder sb = new StringBuilder();
            for (SectorNews news : newsList) {
                sb.append(news.getTitle()).append("\n");
                sb.append(news.getContents()).append("\n\n");
            }
            String combinedText = sb.toString();

            // 2️⃣ 프롬프트 생성 (요약 + 감정)
            String prompt = """
                당신은 한국 증시의 섹터별 동향을 분석하는 금융 애널리스트입니다.
                아래 뉴스 기사들을 종합 분석하여 섹터의 분위기와 주요 흐름을 객관적으로 요약하세요.
                투자 판단이나 추천은 하지 마세요.
                요약은 100자 이내로 하세요.
                
                결과는 반드시 JSON 형식으로 반환하세요:
                {
                  "sector": "%s",
                  "analysis": "...",
                  "sentiment": "긍정 | 중립 | 부정"
                }
                
                뉴스 기사:
                %s
                """.formatted(sectorName, combinedText);
            // 3️⃣ Gemini 호출
            String response = geminiService.getContents(prompt);

            // 4️⃣ 백틱(```json`, ```) 제거 후 정제
            String cleaned = response
                    .replaceAll("(?i)```json", "")  // 대소문자 구분 없이 제거
                    .replaceAll("```", "")
                    .trim();

            if (!cleaned.startsWith("{")) {
                log.error("❌ Gemini 응답이 JSON 형식이 아닙니다: {}", response);
                throw new RuntimeException("Gemini 응답 파싱 실패: JSON 형식이 아님");
            }

            // 5️⃣ JSON 파싱
            JsonNode jsonNode = objectMapper.readTree(cleaned);

            if (jsonNode.has("error")) {
                String errorMsg = jsonNode.get("error").asText();
                log.error("⚠️ Gemini 에러 응답 감지: {}", errorMsg);
                throw new RuntimeException("Gemini 에러 응답: " + errorMsg);
            }

            SectorAnalysisResponse parsed = objectMapper.treeToValue(jsonNode, SectorAnalysisResponse.class);

            // 6️⃣ 감정 매핑
            Emotion emotion;
            if(parsed.getSentiment().equals("긍정"))
                emotion = Emotion.POSITIVE;
            else if(parsed.getSentiment().equals("부정"))
                emotion = Emotion.NEGATIVE;
            else emotion = Emotion.NEUTRAL;

            // 7️⃣ 결과 엔티티 생성
            return SectorNewsSummary.builder()
                    .sector(newsList.get(0).getSector())
                    .emotion(emotion)
                    .summary(parsed.getAnalysis())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("❌ Gemini JSON 파싱 오류: {}", e.getMessage());
            throw new RuntimeException("Gemini 응답 파싱 실패", e);
        } catch (Exception e) {
            log.error("❌ NewsAnalyzerService 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini 분석 중 오류 발생", e);
        }
    }
}