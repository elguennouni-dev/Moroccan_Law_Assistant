package com.lawyer.elguennouni_dev.service;

import com.lawyer.elguennouni_dev.dto.ChatMessageDto;
import com.lawyer.elguennouni_dev.dto.SessionPreviewDto;
import com.lawyer.elguennouni_dev.entity.AppUser;
import com.lawyer.elguennouni_dev.entity.ChatMessage;
import com.lawyer.elguennouni_dev.entity.ChatSession;
import com.lawyer.elguennouni_dev.repository.AppUserRepository;
import com.lawyer.elguennouni_dev.repository.ChatMessageRepository;
import com.lawyer.elguennouni_dev.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private LegalQueryService aiService;

    public UUID startSession(String userEmail) {

        System.out.println("Email: " + userEmail);

        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User Found: " + user.getId());

        ChatSession session = new ChatSession();
        // session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setCreatedAt(LocalDateTime.now());

        System.out.println("Session Created: " + session.getId());

        ChatSession savedSession = chatSessionRepository.save(session);

        System.out.println("Session Saved: " + savedSession.getId());

        return savedSession.getId();
    }

    public String handleUserQuestion(String userEmail, UUID sessionId, String question) {
        try {
            ChatSession session = chatSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            if (session.getUser() == null || !session.getUser().getEmail().equals(userEmail)) {
                throw new RuntimeException("Unauthorized session access");
            }

            ChatMessage userMsg = new ChatMessage();
            userMsg.setId(UUID.randomUUID());
            userMsg.setChat(session);
            userMsg.setSender("user");
            userMsg.setMessage(question);
            userMsg.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(userMsg);
            String aiReply = aiService.getProfessionalAnswer(question);



            ChatMessage aiMsg = new ChatMessage();
            aiMsg.setId(UUID.randomUUID());
            aiMsg.setChat(session);
            aiMsg.setSender("ai");
            aiMsg.setMessage(aiReply);
            aiMsg.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(aiMsg);

            return aiReply;

        } catch (IOException e) {
            throw new RuntimeException("AI SERVICE Error : " + e.getMessage());
        }
    }


    public List<ChatMessageDto> getMessages(String userEmail, UUID sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getUser() == null || !session.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access");
        }

        return chatMessageRepository.findByChatIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(msg -> new ChatMessageDto(msg.getSender(), msg.getMessage(), msg.getCreatedAt()))
                .toList();
    }


    public List<SessionPreviewDto> getUserSessions(String userEmail) {
        return chatSessionRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(session -> new SessionPreviewDto(session.getId(), session.getCreatedAt()))
                .toList();
    }

}













