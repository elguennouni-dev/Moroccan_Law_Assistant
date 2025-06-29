package com.lawyer.elguennouni_dev.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne
    private ChatSession chat;
    private String sender;
    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();
}
