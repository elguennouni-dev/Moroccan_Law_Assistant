package com.lawyer.elguennouni_dev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionPreviewDto {
    private UUID sessionId;
    private LocalDateTime createdAt;
}
