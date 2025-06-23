package com.lawyer.elguennouni_dev.dto;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
public class ApiResponse {

    private boolean status;
    private String content;
    private LocalDateTime createdAt;

}
