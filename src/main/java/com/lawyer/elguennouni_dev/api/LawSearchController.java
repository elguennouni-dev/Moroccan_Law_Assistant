package com.lawyer.elguennouni_dev.api;

import com.lawyer.elguennouni_dev.dto.ApiResponse;
import com.lawyer.elguennouni_dev.embedding.Law;
import com.lawyer.elguennouni_dev.service.LegalQueryService;
import com.lawyer.elguennouni_dev.utils.SimilaritySearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/law")
public class LawSearchController {

    private final SimilaritySearchService searchService;

    private final LegalQueryService legalQueryService;

    public LawSearchController(SimilaritySearchService service, LegalQueryService queryService) {
        this.searchService = service;
        this.legalQueryService = queryService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String question) {
        try {
            String answer = legalQueryService.getProfessionalAnswer(question);
            ApiResponse response = new ApiResponse();
            response.setStatus(true);
            response.setContent(answer);
            response.setCreatedAt(LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            ApiResponse response = new ApiResponse();
            response.setStatus(false);
            response.setContent("المرجو المحاولة لاحقاً.");
            response.setCreatedAt(LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
    }

}
