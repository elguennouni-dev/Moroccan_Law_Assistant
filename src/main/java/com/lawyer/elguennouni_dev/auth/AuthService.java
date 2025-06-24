package com.lawyer.elguennouni_dev.auth;

import com.lawyer.elguennouni_dev.dao.RefreshTokenRequest;
import com.lawyer.elguennouni_dev.dto.RefreshTokenResponse;
import com.lawyer.elguennouni_dev.dto.SignupResponse;
import com.lawyer.elguennouni_dev.dto.TokenValidationResponse;
import com.lawyer.elguennouni_dev.entity.AppUser;
import com.lawyer.elguennouni_dev.entity.OtpVerification;
import com.lawyer.elguennouni_dev.jwt.JwtUtil;
import com.lawyer.elguennouni_dev.repository.AppUserRepository;
import com.lawyer.elguennouni_dev.repository.OtpVerificationRepository;
import com.lawyer.elguennouni_dev.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private OtpVerificationRepository otpVerificationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;


    public ResponseEntity<?> signupWithEmail(String email) {

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "This email already exists!"));
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(verification);

        try {
            emailService.sendOtpEmail(email, otp);
            String token = jwtUtil.generateToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            SignupResponse response = new SignupResponse();
            response.setEmail(email);
            response.setToken(token);
            response.setRefreshToken(refreshToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            otpVerificationRepository.delete(verification);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send verification email. Please try again."));
        }

    }


    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid refresh token"));
            }

            String email = jwtUtil.extractEmail(refreshToken);

            Optional<AppUser> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not found"));
            }

            AppUser user = userOptional.get();

            String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getId());

            String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setEmail(user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","Invalid refresh token"));
        }
    }

    public ResponseEntity<?> logout(String authHeader) {
        try {
            String token = authHeader.substring(7);

            return ResponseEntity.ok(Map.of("message","Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
    }

    public ResponseEntity<?> validateToken(String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                UUID userId = jwtUtil.extractUserId(token);

                TokenValidationResponse response = new TokenValidationResponse();
                response.setValid(true);
                response.setEmail(email);
                response.setUserId(userId);
                response.setRemainingTime(jwtUtil.getTokenRemainingTime(token));

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "message", "Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid token format"));
        }
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OtpVerification> record = otpVerificationRepository.findByEmailAndOtp(email, otp);
        if (record.isPresent() && record.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            if (!userRepository.findByEmail(email).isPresent()) {
                AppUser user = new AppUser();
                user.setEmail(email);
                user.setVerified(true);
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

}
