package com.immoservice.immobiliers.config;

import java.time.LocalDateTime;

import com.immoservice.immobiliers.entities.AuditLog;
import com.immoservice.immobiliers.entities.BaseEntity;
import com.immoservice.immobiliers.repository.AccessLogRepository;
import com.immoservice.immobiliers.services.UserInfoService;
import com.immoservice.immobiliers.repository.AuditLogRepository;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class AuditLogListener {

    private AccessLogRepository accessLogRepository;
    private UserInfoService userInfoService;
private AuditLogRepository auditLogRepository;

    // Pour permettre l'injection manuelle (car c'est géré par JPA, pas Spring)
    // public AuditLogListener() {
    //     this.accessLogRepository = BeanUtil.getBean(AccessLogRepository.class);
    //     this.auditLogRepository = BeanUtil.getBean(AuditLogRepository.class);
    //     this.userInfoService = BeanUtil.getBean(UserInfoService.class);
    // }


    private AccessLogRepository getAccessLogRepository() {
        if (accessLogRepository == null) {
            accessLogRepository = BeanUtil.getBean(AccessLogRepository.class);
        }
        return accessLogRepository;
    }

    private UserInfoService getUserInfoService() {
        if (userInfoService == null) {
            userInfoService = BeanUtil.getBean(UserInfoService.class);
        }
        return userInfoService;
    }
     private AuditLogRepository getAuditLogRepository() {
        if (auditLogRepository == null) {
            auditLogRepository = BeanUtil.getBean(AuditLogRepository.class);
        }
        return auditLogRepository;
    }
    @PrePersist
    public void prePersist(Object entity) {
        createAuditLog("CREATE", entity);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        createAuditLog("UPDATE", entity);
    }

    @PreRemove
    public void preRemove(Object entity) {
        createAuditLog("DELETE", entity);
    }

   private void createAuditLog(String action, Object entity) {
    if (!(entity instanceof BaseEntity baseEntity))
        return;

    AuditLog log = new AuditLog();
    log.setEntityType(entity.getClass().getSimpleName());
    log.setEntityId(baseEntity.getId());
    log.setModificationType(action);
    log.setModificationDate(LocalDateTime.now());

    String username = getUserInfoService().getCurrentUsername();
    log.setModifiedBy(username != null ? username : "anonymous");

    getAuditLogRepository().save(log);
}
}
