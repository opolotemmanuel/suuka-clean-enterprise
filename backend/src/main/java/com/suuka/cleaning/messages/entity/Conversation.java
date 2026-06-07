package com.suuka.cleaning.messages.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "message_conversations")
public class Conversation extends AuditableEntity {
    @Column(nullable = false)
    private UUID participantOneId;

    @Column(nullable = false)
    private UUID participantTwoId;

    private String relatedModule;
    private String relatedEntityId;

    public UUID getParticipantOneId() { return participantOneId; }
    public void setParticipantOneId(UUID participantOneId) { this.participantOneId = participantOneId; }
    public UUID getParticipantTwoId() { return participantTwoId; }
    public void setParticipantTwoId(UUID participantTwoId) { this.participantTwoId = participantTwoId; }
    public String getRelatedModule() { return relatedModule; }
    public void setRelatedModule(String relatedModule) { this.relatedModule = relatedModule; }
    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public boolean includes(UUID userId) {
        return participantOneId.equals(userId) || participantTwoId.equals(userId);
    }
}
