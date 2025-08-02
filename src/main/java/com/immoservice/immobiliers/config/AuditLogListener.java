package com.immoservice.immobiliers.config;

import java.sql.Date;

import com.immoservice.immobiliers.entities.AuditLog;
import com.immoservice.immobiliers.entities.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class AuditLogListener {
    @PrePersist
    public void prePersist(Object entity) {
        AuditLog auditLog = new AuditLog();

        auditLog.setEntityType(entity.getClass().getSimpleName());
        auditLog.setEntityId(((BaseEntity) entity).getId());
        auditLog.setModificationDate(new Date(0));
        auditLog.setModificationType("CREATE");
        auditLog.setModifiedBy("utilisateur courant");
    }

    @PreUpdate
    public void preUpdate(Object entity) {

    }

    @PreRemove
    public void preRemove(Object entity) {

    }
}