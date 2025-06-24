package com.lawyer.elguennouni_dev.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TokenValidationResponse {
    private boolean valid;
    private String email;
    private UUID userId;
    private Long remainingTime;
}
