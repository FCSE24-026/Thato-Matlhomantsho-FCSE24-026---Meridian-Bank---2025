package com.banking.model;

import java.time.LocalDateTime;

/**
 * AuditLog tracks all administrative actions and system events for compliance and audit trails.
 */
public class AuditLog {
    private String auditId;
    private String adminId;
    private String action;
    private String targetType; // "CUSTOMER", "ACCOUNT", "TRANSACTION", "SYSTEM"
    private String targetId;
    private String details;
    private LocalDateTime timestamp;
    private String status; // "APPROVED", "DENIED", "LOGGED"

    public AuditLog(String auditId, String adminId, String action, String targetType, 
                    String targetId, String details, String status) {
        this.auditId = auditId;
        this.adminId = adminId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    // Getters
    public String getAuditId() { return auditId; }
    public String getAdminId() { return adminId; }
    public String getAction() { return action; }
    public String getTargetType() { return targetType; }
    public String getTargetId() { return targetId; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s on %s %s - %s", 
            status, action, targetType, targetId, timestamp, details);
    }
}
