package com.suuka.cleaning.platform.repository;

import com.suuka.cleaning.platform.entity.ActivityTimelineEvent;
import com.suuka.cleaning.platform.enums.PlatformModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityTimelineEventRepository extends JpaRepository<ActivityTimelineEvent, UUID> {
    List<ActivityTimelineEvent> findByModuleAndEntityIdOrderByCreatedAtDesc(PlatformModule module, String entityId);
}
