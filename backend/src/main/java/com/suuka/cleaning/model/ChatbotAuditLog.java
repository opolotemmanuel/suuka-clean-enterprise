package com.suuka.cleaning.model;

import java.time.Instant;

public class ChatbotAuditLog {
    private Instant date;
    private String user;
    private String role;
    private String question;
    private String dataAccessed;
    private String responseType;
    private String permissionResult;
    private String actionRequested;
    private String status;

    public ChatbotAuditLog(
            Instant date,
            String user,
            String role,
            String question,
            String dataAccessed,
            String responseType,
            String permissionResult,
            String actionRequested,
            String status
    ) {
        this.date = date;
        this.user = user;
        this.role = role;
        this.question = question;
        this.dataAccessed = dataAccessed;
        this.responseType = responseType;
        this.permissionResult = permissionResult;
        this.actionRequested = actionRequested;
        this.status = status;
    }

    public Instant getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getQuestion() {
        return question;
    }

    public String getDataAccessed() {
        return dataAccessed;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getPermissionResult() {
        return permissionResult;
    }

    public String getActionRequested() {
        return actionRequested;
    }

    public String getStatus() {
        return status;
    }
}
