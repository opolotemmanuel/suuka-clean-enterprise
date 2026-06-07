package com.suuka.cleaning.model;

public class ChatbotResponse {
    private String answer;
    private String responseType;
    private String permissionResult;
    private String dataAccessed;
    private String actionRequested;
    private String status;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getPermissionResult() {
        return permissionResult;
    }

    public void setPermissionResult(String permissionResult) {
        this.permissionResult = permissionResult;
    }

    public String getDataAccessed() {
        return dataAccessed;
    }

    public void setDataAccessed(String dataAccessed) {
        this.dataAccessed = dataAccessed;
    }

    public String getActionRequested() {
        return actionRequested;
    }

    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
