package com.lawyer.elguennouni_dev.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Service
public class GeminiPromptService {

    @Value("${google.cloud.api.key}")
    private String API_KEY;

    private final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public String sendPrompt(String promptText) {
        System.out.println("API KEY : " + API_KEY);
        System.out.println("API URL : " + URL+API_KEY);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> part = Map.of("text", promptText);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map<String, Object> response = restTemplate.postForObject(URL + API_KEY, entity, Map.class);

        if (response != null) {
            try {
                var candidates = (List<Map<String, Object>>) response.get("candidates");
                if (candidates == null || candidates.isEmpty()) {
                    return "No candidates found in response.";
                }
                Map<String, Object> firstCandidate = candidates.get(0);
                Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
                if (contentMap == null) {
                    return "No content found in candidate.";
                }
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                if (parts == null || parts.isEmpty()) {
                    return "No parts found in content.";
                }
                String text = (String) parts.get(0).get("text");
                return text != null ? text : "No text found in content part.";
            } catch (Exception e) {
                return "Error parsing response: " + e.getMessage();
            }
        }
        return "Empty response from API";
    }
}