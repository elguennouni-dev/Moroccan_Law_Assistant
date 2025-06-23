package com.lawyer.elguennouni_dev.embedding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

//@Component
//public class EmbeddingGenerator implements CommandLineRunner {
//
//    private final GeminiEmbeddingService embeddingService;
//
//    public EmbeddingGenerator(GeminiEmbeddingService service) {
//        this.embeddingService = service;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        File inputFile = new File("src/main/resources/laws.json");
//        File outputFile = new File("src/main/resources/embeddings.json");
//
//        List<Law> laws = mapper.readValue(inputFile, new TypeReference<List<Law>>() {});
//        List<Law> enriched = new ArrayList<>();
//
//        for (Law law : laws) {
//            System.out.println("Generating embedding for law id: " + law.id);
//            List<Double> vector = embeddingService.getEmbedding(law.text);
//            law.embedding = vector;
//            enriched.add(law);
//            Thread.sleep(1000);
//        }
//
//        mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, enriched);
//        System.out.println("Embeddings generated and saved to embeddings.json");
//    }
// }