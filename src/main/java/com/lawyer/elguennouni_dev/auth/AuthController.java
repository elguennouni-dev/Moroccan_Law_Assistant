package com.lawyer.elguennouni_dev.auth;

import com.lawyer.elguennouni_dev.dao.LoginRequest;
import com.lawyer.elguennouni_dev.dao.RefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        return authService.logout(authHeader);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        return authService.validateToken(authHeader);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> requestBody) {
        String email = extractEmail(requestBody);
        return authService.signupWithEmail(email);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> verifyLogin(@RequestBody Map<String, String> requestBody) {
        String email = extractEmail(requestBody);
        String otp = extractOtp(requestBody);
        return authService.verifyLogin(email, otp);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> requestBody) {
        String email = extractEmail(requestBody);
        String otp = extractOtp(requestBody);
        return authService.verifyOtp(email, otp);
    }

    private String extractEmail(Map<String, String> requestBody) {
        return requestBody.get("email");
    }

    private String extractOtp(Map<String, String> requestBody) {
        return requestBody.get("otp");
    }
}