package com.lawyer.elguennouni_dev.repository;

import com.lawyer.elguennouni_dev.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    Optional<ChatSession> findByUserEmailOrderByCreatedAtDesc(String userEmail);

}
