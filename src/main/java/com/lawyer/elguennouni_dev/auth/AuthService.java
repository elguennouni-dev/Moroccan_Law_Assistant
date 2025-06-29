package com.lawyer.elguennouni_dev.auth;

import com.lawyer.elguennouni_dev.dao.LoginRequest;
import com.lawyer.elguennouni_dev.dao.RefreshTokenRequest;
import com.lawyer.elguennouni_dev.dto.RefreshTokenResponse;
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

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int OTP_MAX_VALUE = 999999;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final AppUserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final Random secureRandom;

    @Autowired
    public AuthService(AppUserRepository userRepository,
                       OtpVerificationRepository otpVerificationRepository,
                       EmailService emailService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.secureRandom = new Random();
    }

    public ResponseEntity<?> login(LoginRequest request) {
        try {
            AppUser user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String otp = generateOtp();
            OtpVerification verification = createOtpVerification(user.getEmail(), otp);

            otpVerificationRepository.save(verification);
            emailService.sendOtpLogin(user.getEmail(), otp);

            return ResponseEntity.ok(createSuccessResponse("OTP sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to send verification email."));
        }
    }

    public ResponseEntity<?> signupWithEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("This email already exists!"));
        }

        try {
            String otp = generateOtp();
            OtpVerification verification = createOtpVerification(email, otp);

            otpVerificationRepository.save(verification);
            emailService.sendOtpEmail(email, otp);

            return ResponseEntity.ok(createSuccessResponse("OTP sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to send verification email."));
        }
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!isValidRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Invalid refresh token"));
            }

            String email = jwtUtil.extractEmail(refreshToken);
            AppUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            RefreshTokenResponse response = createRefreshTokenResponse(user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid refresh token"));
        }
    }

    public ResponseEntity<?> logout(String authHeader) {
        return ResponseEntity.ok(createSuccessResponse("Logged out successfully"));
    }

    public ResponseEntity<?> validateToken(String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);

            if (jwtUtil.validateToken(token)) {
                TokenValidationResponse response = createTokenValidationResponse(token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createTokenValidationErrorResponse("Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createTokenValidationErrorResponse("Invalid token format"));
        }
    }

    public ResponseEntity<?> verifyLogin(String email, String otp) {
        Optional<OtpVerification> verificationRecord = otpVerificationRepository.findByEmailAndOtp(email, otp);

        if (verificationRecord.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("رمز التحقق غير صحيح."));
        }

        OtpVerification verification = verificationRecord.get();
        if (isOtpExpired(verification)) {
            otpVerificationRepository.delete(verification);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("انتهت صلاحية الرمز، المرجو إعادة إرسال رمز جديد."));
        }

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found In Login Otp Verification"));

        return processSuccessfulVerification(user, verification);
    }

    public ResponseEntity<?> verifyOtp(String email, String otp) {
        Optional<OtpVerification> verificationRecord = otpVerificationRepository.findByEmailAndOtp(email, otp);

        if (verificationRecord.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("رمز التحقق غير صحيح."));
        }

        OtpVerification verification = verificationRecord.get();
        if (isOtpExpired(verification)) {
            otpVerificationRepository.delete(verification);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("انتهت صلاحية الرمز، المرجو إعادة إرسال رمز جديد."));
        }

        AppUser user = findOrCreateUser(email);
        return processSuccessfulVerification(user, verification);
    }

    private String generateOtp() {
        return String.format("%06d", secureRandom.nextInt(OTP_MAX_VALUE));
    }

    private OtpVerification createOtpVerification(String email, String otp) {
        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        return verification;
    }

    private boolean isValidRefreshToken(String refreshToken) {
        return jwtUtil.validateToken(refreshToken) && jwtUtil.isRefreshToken(refreshToken);
    }

    private RefreshTokenResponse createRefreshTokenResponse(AppUser user) {
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setEmail(user.getEmail());
        return response;
    }

    private String extractTokenFromHeader(String authHeader) {
        return authHeader.substring(BEARER_PREFIX_LENGTH);
    }

    private TokenValidationResponse createTokenValidationResponse(String token) {
        String email = jwtUtil.extractEmail(token);
        UUID userId = jwtUtil.extractUserId(token);

        TokenValidationResponse response = new TokenValidationResponse();
        response.setValid(true);
        response.setEmail(email);
        response.setUserId(userId);
        response.setRemainingTime(jwtUtil.getTokenRemainingTime(token));
        return response;
    }

    private boolean isOtpExpired(OtpVerification verification) {
        return verification.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private AppUser findOrCreateUser(String email) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setEmail(email);
            newUser.setVerified(true);
            newUser.setCreatedAt(LocalDateTime.now());
            return userRepository.save(newUser);
        });
    }

    private ResponseEntity<?> processSuccessfulVerification(AppUser user, OtpVerification verification) {
        otpVerificationRepository.delete(verification);

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return ResponseEntity.ok(createAuthenticationResponse(user, accessToken, refreshToken));
    }

    private Map<String, Object> createSuccessResponse(String message) {
        return Map.of("message", message);
    }

    private Map<String, Object> createErrorResponse(String message) {
        return Map.of("message", message);
    }

    private Map<String, Object> createTokenValidationErrorResponse(String message) {
        return Map.of("valid", false, "message", message);
    }

    private Map<String, Object> createAuthenticationResponse(AppUser user, String accessToken, String refreshToken) {
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail()
                )
        );
    }
}