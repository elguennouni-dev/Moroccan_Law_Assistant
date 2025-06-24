package com.lawyer.elguennouni_dev.auth;

import com.lawyer.elguennouni_dev.entity.AppUser;
import com.lawyer.elguennouni_dev.entity.OtpVerification;
import com.lawyer.elguennouni_dev.repository.AppUserRepository;
import com.lawyer.elguennouni_dev.repository.OtpVerificationRepository;
import com.lawyer.elguennouni_dev.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private OtpVerificationRepository otpVerificationRepository;
    @Autowired
    private EmailService emailService;


    public void signupWithEmail(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(verification);

        // System.out.println("Sending OTP to: " + email + " | Code: " + otp);

        emailService.sendOtpEmail(email, otp);
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
