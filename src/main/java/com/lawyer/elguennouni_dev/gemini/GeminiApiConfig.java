package com.lawyer.elguennouni_dev.gemini;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GeminiApiConfig {

    @Value("${google.cloud.project.id}")
    private String projectId;

    @Value("${google.cloud.location}")
    private String location;


    @Bean
    public VertexAI vertexAISearch() throws IOException {
        return new VertexAI(projectId, location);
    }

}
