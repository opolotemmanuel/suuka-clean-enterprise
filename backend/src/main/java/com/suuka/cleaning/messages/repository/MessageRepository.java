package com.suuka.cleaning.messages.repository;

import com.suuka.cleaning.messages.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    long countByRecipientIdAndReadFalse(UUID recipientId);
}
