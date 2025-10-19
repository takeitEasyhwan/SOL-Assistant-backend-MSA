package com.donttouch.external_assistant_service.domain.gemini.service;

import com.donttouch.external_assistant_service.domain.gemini.entity.vo.ChatRequest;
import com.donttouch.external_assistant_service.domain.gemini.entity.vo.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String getContents(String prompt) {
        String requestUrl = apiUrl + "?key=" + geminiApiKey;
        ChatRequest request = new ChatRequest(prompt);

        ChatResponse response = restTemplate.postForObject(requestUrl, request, ChatResponse.class);

        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            log.error("❌ Gemini 응답이 비어 있습니다: {}", response);
            return "{\"error\": \"empty response\"}";
        }

        var candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null ||
                candidate.getContent().getParts() == null ||
                candidate.getContent().getParts().isEmpty()) {
            log.error("❌ Gemini 응답에 content.parts가 비어 있습니다: {}", response);
            return "{\"error\": \"no parts in response\"}";
        }

        String message = candidate.getContent().getParts().get(0).getText();
        if (message == null || message.isBlank()) {
            log.error("❌ Gemini 응답에 text가 없습니다: {}", response);
            return "{\"error\": \"no text in response\"}";
        }

        // ✅ Gemini가 가끔 JSON을 문자열로 감싸서 줄 때가 있으므로 trim
        return message.trim();
    }
}