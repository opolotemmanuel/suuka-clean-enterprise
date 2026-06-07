package com.suuka.cleaning.model;

import java.util.List;

public class ChatbotRequest {
    private String userId;
    private String role;
    private List<String> permissionScope;
    private String tenant;
    private String branch;
    private String sessionId;
    private String question;
    private String requestedAction;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPermissionScope() {
        return permissionScope;
    }

    public void setPermissionScope(List<String> permissionScope) {
        this.permissionScope = permissionScope;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRequestedAction() {
        return requestedAction;
    }

    public void setRequestedAction(String requestedAction) {
        this.requestedAction = requestedAction;
    }
}
