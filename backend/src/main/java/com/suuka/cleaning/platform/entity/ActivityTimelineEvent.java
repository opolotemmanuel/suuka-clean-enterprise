package com.suuka.cleaning.platform.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import com.suuka.cleaning.platform.enums.PlatformModule;
import jakarta.persistence.*;

@Entity
@Table(name = "activity_timeline_events")
public class ActivityTimelineEvent extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformModule module;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private String eventType;

    @Column(length = 4000)
    private String details;

    private String actorId;

    public PlatformModule getModule() { return module; }
    public void setModule(PlatformModule module) { this.module = module; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
}
