package com.pulsedesk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsedesk.dto.TicketAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Slf4j
@Service
public class HuggingFaceService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    private static final String API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String MODEL = "Qwen/Qwen2.5-72B-Instruct";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // sends comment to AI and returns structured ticket data
    public TicketAnalysis analyze(String text) {
        String prompt = """
                Analyze the following user comment and return ONLY valid JSON, no explanation:

                {
                  "needsTicket": true or false,
                  "title": "short title (max 10 words)",
                  "category": "bug|feature|billing|account|other",
                  "priority": "low|medium|high",
                  "summary": "one sentence summary"
                }

                Comment: %s
                """.formatted(text);

        try {
            String response = fetchAiResponse(prompt);
            response = response
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            return objectMapper.readValue(response, TicketAnalysis.class);

        } catch (Exception e) {
            log.error("AI processing failed", e);

            TicketAnalysis fallback = new TicketAnalysis();
            fallback.setNeedsTicket(false);
            fallback.setManualReview(true);
            fallback.setSummary(text);

            return fallback;
        }
    }

    // makes the HTTP call to Hugging Face API
    private String fetchAiResponse(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        body.put("model", MODEL);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        body.put("max_tokens", 150);
        body.put("temperature", 0.1); // deterministic output, reduces risk of malformed JSON

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_URL, new HttpEntity<>(body, headers), Map.class);

            Map<String, Object> rawMap = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) rawMap.get("choices");
            Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
            return ((String) msg.get("content")).trim();

        } catch (Exception e) {
            log.error("HuggingFace API error", e);
            throw new RuntimeException("Failed to call HuggingFace API", e);
        }
    }
}