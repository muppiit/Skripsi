package com.doyatama.university.payload;

import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamClientAuditLogRequest {
    private String idUjian;
    private String idPeserta;
    private String sessionId;
    private String studyProgramId;
    private String eventType;
    private String platform;
    private String clientEventId;
    private String segmentName;
    private String status;
    private Integer attemptNumber;
    private Integer failureCount;
    private Integer retryCount;
    private String message;
    private String errorMessage;
    private Map<String, Object> deviceInfo = new HashMap<>();
    private Map<String, Object> networkInfo = new HashMap<>();
    private Map<String, Object> downloadInfo = new HashMap<>();
    private Map<String, Object> uploadInfo = new HashMap<>();
    private Map<String, Object> eventData = new HashMap<>();

    public String getIdUjian() {
        return idUjian;
    }

    public void setIdUjian(String idUjian) {
        this.idUjian = idUjian;
    }

    public String getIdPeserta() {
        return idPeserta;
    }

    public void setIdPeserta(String idPeserta) {
        this.idPeserta = idPeserta;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStudyProgramId() {
        return studyProgramId;
    }

    public void setStudyProgramId(String studyProgramId) {
        this.studyProgramId = studyProgramId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getClientEventId() {
        return clientEventId;
    }

    public void setClientEventId(String clientEventId) {
        this.clientEventId = clientEventId;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Map<String, Object> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Map<String, Object> getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(Map<String, Object> networkInfo) {
        this.networkInfo = networkInfo;
    }

    public Map<String, Object> getDownloadInfo() {
        return downloadInfo;
    }

    public void setDownloadInfo(Map<String, Object> downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public Map<String, Object> getUploadInfo() {
        return uploadInfo;
    }

    public void setUploadInfo(Map<String, Object> uploadInfo) {
        this.uploadInfo = uploadInfo;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }
}
