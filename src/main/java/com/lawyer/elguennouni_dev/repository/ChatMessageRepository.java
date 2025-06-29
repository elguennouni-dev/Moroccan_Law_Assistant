package com.lawyer.elguennouni_dev.repository;

import com.lawyer.elguennouni_dev.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    Optional<ChatMessage> findByChatIdOrderByCreatedAtAsc(UUID sessionId);

}
