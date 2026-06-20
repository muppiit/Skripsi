package com.doyatama.university.repository;

import com.doyatama.university.helper.HBaseCustomClient;
import com.doyatama.university.model.ExamClientAuditLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.springframework.stereotype.Repository;

@Repository
public class ExamClientAuditLogRepository {

    Configuration conf = HBaseConfiguration.create();
    String tableName = "exam_client_audit_logs";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExamClientAuditLog save(ExamClientAuditLog auditLog) throws IOException {
        if (auditLog.getIdLog() == null || auditLog.getIdLog().trim().isEmpty()) {
            auditLog.setIdLog(java.util.UUID.randomUUID().toString());
        }
        if (auditLog.getEventTime() == null) {
            auditLog.setEventTime(Instant.now());
        }
        if (auditLog.getCreatedAt() == null) {
            auditLog.setCreatedAt(Instant.now());
        }
        auditLog.setUpdatedAt(Instant.now());

        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableAuditLogs = TableName.valueOf(tableName);
        String rowKey = auditLog.getIdLog();

        saveMainInfo(client, tableAuditLogs, rowKey, auditLog);
        saveRelationships(client, tableAuditLogs, rowKey, auditLog);
        saveClientInfo(client, tableAuditLogs, rowKey, auditLog);
        saveEventInfo(client, tableAuditLogs, rowKey, auditLog);

        client.insertRecord(tableAuditLogs, rowKey, "detail", "created_by", "CAT-CLIENT-AUDIT");
        return auditLog;
    }

    public List<ExamClientAuditLog> findByUjianId(String idUjian, int limit) throws IOException {
        return findByColumn("main", "idUjian", idUjian, limit);
    }

    public List<ExamClientAuditLog> findBySessionId(String sessionId, int limit) throws IOException {
        return findByColumn("main", "sessionId", sessionId, limit);
    }

    public List<ExamClientAuditLog> findByPesertaId(String idPeserta, int limit) throws IOException {
        return findByColumn("main", "idPeserta", idPeserta, limit);
    }

    private List<ExamClientAuditLog> findByColumn(String family, String column, String value, int limit)
            throws IOException {
        HBaseCustomClient client = new HBaseCustomClient(conf);
        TableName tableAuditLogs = TableName.valueOf(tableName);

        return client.getDataListByColumnIndeks(
                tableAuditLogs.toString(),
                getStandardColumnMapping(),
                family,
                column,
                value,
                ExamClientAuditLog.class,
                limit,
                getIndexedFields());
    }

    private void saveMainInfo(HBaseCustomClient client, TableName table, String rowKey, ExamClientAuditLog auditLog) {
        client.insertRecord(table, rowKey, "main", "idLog", safe(auditLog.getIdLog()));
        client.insertRecord(table, rowKey, "main", "idUjian", safe(auditLog.getIdUjian()));
        client.insertRecord(table, rowKey, "main", "idPeserta", safe(auditLog.getIdPeserta()));
        client.insertRecord(table, rowKey, "main", "sessionId", safe(auditLog.getSessionId()));
        client.insertRecord(table, rowKey, "main", "studyProgramId", safe(auditLog.getStudyProgramId()));
        client.insertRecord(table, rowKey, "main", "eventType", safe(auditLog.getEventType()));
        client.insertRecord(table, rowKey, "main", "platform", safe(auditLog.getPlatform()));
        client.insertRecord(table, rowKey, "main", "clientEventId", safe(auditLog.getClientEventId()));
        client.insertRecord(table, rowKey, "main", "status", safe(auditLog.getStatus()));
        client.insertRecord(table, rowKey, "main", "segmentName", safe(auditLog.getSegmentName()));
        client.insertRecord(table, rowKey, "main", "message", safe(auditLog.getMessage()));
        client.insertRecord(table, rowKey, "main", "errorMessage", safe(auditLog.getErrorMessage()));

        if (auditLog.getAttemptNumber() != null) {
            client.insertRecord(table, rowKey, "main", "attemptNumber", auditLog.getAttemptNumber().toString());
        }
        if (auditLog.getFailureCount() != null) {
            client.insertRecord(table, rowKey, "main", "failureCount", auditLog.getFailureCount().toString());
        }
        if (auditLog.getRetryCount() != null) {
            client.insertRecord(table, rowKey, "main", "retryCount", auditLog.getRetryCount().toString());
        }
        if (auditLog.getEventTime() != null) {
            client.insertRecord(table, rowKey, "main", "eventTime", auditLog.getEventTime().toString());
        }
        if (auditLog.getCreatedAt() != null) {
            client.insertRecord(table, rowKey, "main", "createdAt", auditLog.getCreatedAt().toString());
        }
        if (auditLog.getUpdatedAt() != null) {
            client.insertRecord(table, rowKey, "main", "updatedAt", auditLog.getUpdatedAt().toString());
        }
    }

    private void saveRelationships(HBaseCustomClient client, TableName table, String rowKey,
            ExamClientAuditLog auditLog) {
        client.insertRecord(table, rowKey, "peserta", "id", safe(auditLog.getIdPeserta()));
        client.insertRecord(table, rowKey, "ujian", "id", safe(auditLog.getIdUjian()));
        client.insertRecord(table, rowKey, "session", "id", safe(auditLog.getSessionId()));
        client.insertRecord(table, rowKey, "study_program", "id", safe(auditLog.getStudyProgramId()));
    }

    private void saveClientInfo(HBaseCustomClient client, TableName table, String rowKey, ExamClientAuditLog auditLog)
            throws IOException {
        client.insertRecord(table, rowKey, "device", "deviceInfo", toJson(auditLog.getDeviceInfo()));
        client.insertRecord(table, rowKey, "network", "networkInfo", toJson(auditLog.getNetworkInfo()));
    }

    private void saveEventInfo(HBaseCustomClient client, TableName table, String rowKey, ExamClientAuditLog auditLog)
            throws IOException {
        client.insertRecord(table, rowKey, "download", "downloadInfo", toJson(auditLog.getDownloadInfo()));
        client.insertRecord(table, rowKey, "upload", "uploadInfo", toJson(auditLog.getUploadInfo()));
        client.insertRecord(table, rowKey, "event", "eventData", toJson(auditLog.getEventData()));
        client.insertRecord(table, rowKey, "error", "message", safe(auditLog.getErrorMessage()));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String toJson(Map<String, Object> value) throws IOException {
        try {
            return objectMapper.writeValueAsString(value != null ? value : new HashMap<>());
        } catch (JsonProcessingException e) {
            throw new IOException("Failed to serialize audit log map", e);
        }
    }

    private Map<String, String> getStandardColumnMapping() {
        Map<String, String> columnMapping = new HashMap<>();
        columnMapping.put("idLog", "idLog");
        columnMapping.put("idUjian", "idUjian");
        columnMapping.put("idPeserta", "idPeserta");
        columnMapping.put("sessionId", "sessionId");
        columnMapping.put("studyProgramId", "studyProgramId");
        columnMapping.put("eventType", "eventType");
        columnMapping.put("platform", "platform");
        columnMapping.put("clientEventId", "clientEventId");
        columnMapping.put("segmentName", "segmentName");
        columnMapping.put("status", "status");
        columnMapping.put("attemptNumber", "attemptNumber");
        columnMapping.put("failureCount", "failureCount");
        columnMapping.put("retryCount", "retryCount");
        columnMapping.put("message", "message");
        columnMapping.put("errorMessage", "errorMessage");
        columnMapping.put("eventTime", "eventTime");
        columnMapping.put("createdAt", "createdAt");
        columnMapping.put("updatedAt", "updatedAt");
        columnMapping.put("deviceInfo", "deviceInfo");
        columnMapping.put("networkInfo", "networkInfo");
        columnMapping.put("downloadInfo", "downloadInfo");
        columnMapping.put("uploadInfo", "uploadInfo");
        columnMapping.put("eventData", "eventData");
        return columnMapping;
    }

    private Map<String, String> getIndexedFields() {
        Map<String, String> indexedFields = new HashMap<>();
        indexedFields.put("deviceInfo", "MAP");
        indexedFields.put("networkInfo", "MAP");
        indexedFields.put("downloadInfo", "MAP");
        indexedFields.put("uploadInfo", "MAP");
        indexedFields.put("eventData", "MAP");
        return indexedFields;
    }
}
