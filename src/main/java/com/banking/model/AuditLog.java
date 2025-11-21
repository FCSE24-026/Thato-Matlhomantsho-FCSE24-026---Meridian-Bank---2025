package com.banking.model;

import java.time.LocalDateTime;

/**
 * AuditLog tracks all administrative actions and system events for compliance and audit trails.
 */
public class AuditLog {
    private String id;
    private String actorId;
    private String actorEmail;
    private String actionType; // e.g., LOGIN, REGISTER, APPROVE_CUSTOMER, DELETE_ACCOUNT
    private String targetType; // CUSTOMER, ACCOUNT, TRANSACTION, SYSTEM
    private String targetId;
    private String details;
    private LocalDateTime timestamp;
    private String status; // optional status like APPROVED/DENIED/OK/FAILED

    public AuditLog() {}

    public AuditLog(String id, String actorId, String actorEmail, String actionType, String targetType, String targetId, String details, String status, LocalDateTime timestamp) {
        this.id = id;
        this.actorId = actorId;
        this.actorEmail = actorEmail;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.details = details;
        this.status = status;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%s) on %s: %s", status, actionType, actorEmail, actorId, timestamp, details);
    }
}
