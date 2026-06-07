package com.suuka.cleaning.messages.repository;

import com.suuka.cleaning.messages.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    List<Message> findByConversationIdAndRecipientIdAndReadFalse(UUID conversationId, UUID recipientId);

    long countByRecipientIdAndReadFalse(UUID recipientId);
}
