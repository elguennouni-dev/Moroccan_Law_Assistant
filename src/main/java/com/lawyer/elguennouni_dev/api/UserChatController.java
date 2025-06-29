package com.lawyer.elguennouni_dev.api;

import com.lawyer.elguennouni_dev.jwt.JwtUtil;
import com.lawyer.elguennouni_dev.service.UserChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-chat")
public class UserChatController {

    @Autowired
    private UserChatService userChatService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        UUID sessionId = userChatService.startSession(email);
        return ResponseEntity.ok(Map.of("sessionId",sessionId));
    }

    @PostMapping("/chat/{sessionId}")
    public ResponseEntity<?> ask(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID sessionId,
            @RequestBody Map<String, String> body
    ) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ",""));
        String question = body.get("question");
        String answer = userChatService.handleUserQuestion(email, sessionId, question);
        return ResponseEntity.ok(Map.of("answer",answer));
    }

    @GetMapping("/chat/{sessionId}/messages")
    public ResponseEntity<?> getMessages(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID sessionId
    ) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ",""));
        return ResponseEntity.ok(userChatService.getMessages(email,sessionId));
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ",""));
        return ResponseEntity.ok(userChatService.getUserSessions(email));
    }

}
