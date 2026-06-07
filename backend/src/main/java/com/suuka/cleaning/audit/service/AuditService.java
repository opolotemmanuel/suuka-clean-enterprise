package com.suuka.cleaning.audit.service;

import com.suuka.cleaning.audit.entity.AuditLog;
import com.suuka.cleaning.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(String actorId, String module, String action, String details) {
        AuditLog log = new AuditLog();
        log.setActorId(actorId);
        log.setModule(module);
        log.setAction(action);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> latest() {
        return auditLogRepository.findAll();
    }
}
