package com.lawyer.elguennouni_dev.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne
    private AppUser user;
    private LocalDateTime createdAt = LocalDateTime.now();
}
