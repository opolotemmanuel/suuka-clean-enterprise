package com.suuka.cleaning.messages.repository;

import com.suuka.cleaning.messages.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByParticipantOneIdAndParticipantTwoIdAndRelatedModuleAndRelatedEntityId(
            UUID participantOneId,
            UUID participantTwoId,
            String relatedModule,
            String relatedEntityId
    );

    List<Conversation> findByParticipantOneIdOrParticipantTwoId(UUID participantOneId, UUID participantTwoId);
}
