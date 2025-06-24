package com.lawyer.elguennouni_dev.embedding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiEmbeddingService {

    @Value("${google.cloud.api.key}")
    private String API_KEY;
    private final String URL = "https://generativelanguage.googleapis.com/v1beta/models/embedding-001:embedContent?key=";

    public List<Double> getEmbedding(String text) {
        System.out.println("API KEY : " + API_KEY);
        System.out.println("API URL : " + URL+API_KEY);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> content = Map.of("parts", List.of(Map.of("text", text)));
        Map<String, Object> body = Map.of("content", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map<String, Object> response = restTemplate.postForObject(URL+API_KEY, entity, Map.class);

        if (response != null && response.containsKey("embedding")) {
            Map<String, Object> embedding = (Map<String, Object>) response.get("embedding");
            return (List<Double>) embedding.get("values");
        }
        return List.of();
    }
}