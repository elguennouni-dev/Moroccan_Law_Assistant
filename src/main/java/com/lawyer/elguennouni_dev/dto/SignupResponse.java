package com.lawyer.elguennouni_dev.dto;

import lombok.Data;

@Data
public class SignupResponse {
    private String email;
    private String token;
    private String refreshToken;
}
