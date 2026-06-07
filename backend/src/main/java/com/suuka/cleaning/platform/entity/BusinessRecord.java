package com.suuka.cleaning.platform.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import com.suuka.cleaning.platform.enums.PlatformModule;
import jakarta.persistence.*;

@Entity
@Table(name = "business_records")
public class BusinessRecord extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformModule module;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    private String relatedEntityId;
    private String ownerId;

    @Column(length = 8000)
    private String metadataJson;

    public PlatformModule getModule() {
        return module;
    }

    public void setModule(PlatformModule module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(String relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }
}
