package com.lawyer.elguennouni_dev.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawyer.elguennouni_dev.embedding.GeminiEmbeddingService;
import com.lawyer.elguennouni_dev.embedding.Law;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimilaritySearchService {

    private final GeminiEmbeddingService embeddingService;
    private final List<Law> laws;

    public SimilaritySearchService(GeminiEmbeddingService embeddingService) throws Exception {
        this.embeddingService = embeddingService;

        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/embeddings.json");

        this.laws = mapper.readValue(file, new TypeReference<List<Law>>() {});
    }

    public List<Law> searchSimilar(String question, int topK) {
        List<Double> questionEmbedding = embeddingService.getEmbedding(question);

        List<LawScore> scored = new ArrayList<>();

        for (Law law : laws) {
            double score = VectorUtils.cosineSimilarity(questionEmbedding, law.embedding);
            scored.add(new LawScore(law, score));
        }

        return scored.stream()
                .sorted(Comparator.comparingDouble(LawScore::getScore).reversed())
                .limit(topK)
                .map(LawScore::getLaw)
                .collect(Collectors.toList());
    }

    private static class LawScore {
        private final Law law;
        private final double score;

        public LawScore(Law law, double score) {
            this.law = law;
            this.score = score;
        }

        public Law getLaw() {
            return law;
        }

        public double getScore() {
            return score;
        }
    }
}