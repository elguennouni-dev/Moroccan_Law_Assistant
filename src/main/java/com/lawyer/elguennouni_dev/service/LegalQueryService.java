package com.lawyer.elguennouni_dev.service;

import com.lawyer.elguennouni_dev.embedding.Law;
import com.lawyer.elguennouni_dev.gemini.GeminiPromptService;
import com.lawyer.elguennouni_dev.repository.ChatMessageRepository;
import com.lawyer.elguennouni_dev.repository.ChatSessionRepository;
import com.lawyer.elguennouni_dev.utils.SimilaritySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegalQueryService {

    private final SimilaritySearchService searchService;
    private final GeminiPromptService geminiPromptService;


    @Autowired
    public LegalQueryService(SimilaritySearchService service, GeminiPromptService gemini) {
        this.searchService = service;
        this.geminiPromptService = gemini;
    }

    public String getProfessionalAnswer(String question) throws IOException {
        List<Law> relevantLaws = searchService.searchSimilar(question, 3);
        String context = formatLawForPrompt(relevantLaws);

        System.out.println("QUESTION: " + question);
        System.out.println("CONTEXT: " + context);

        String finalPrompt = "جاوبني بطريقة مختصرة وواضحة بالدارجة المغربية بحال محامي كيتكلم مع زبون، "
                + "على هاد السؤال: '" + question + "'. "
                + "خد بعين الاعتبار هاد القوانين اللي غادي نعطيك، وذكر في الجواب ديالك أسماء القوانين اللي استعملتي.\n\n"
                + "--- القوانين المرتبطة ---\n" + context;

        // return geminiPromptService.sendPrompt(question);
        return geminiPromptService.sendPrompt(finalPrompt);
    }






    // Helpers
    private String formatLawForPrompt(List<Law> laws) {
        if (laws == null || laws.isEmpty()) {
            return "No relevant laws found";
        }

        return laws.stream()
                .map(Law::toString)
                .collect(Collectors.joining("\n\n"));
    }

}
