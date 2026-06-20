package com.doyatama.university.service;

import com.doyatama.university.model.ExamClientAuditLog;
import com.doyatama.university.payload.ExamClientAuditLogRequest;
import com.doyatama.university.repository.ExamClientAuditLogRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamClientAuditLogService {

    @Autowired
    private ExamClientAuditLogRepository auditLogRepository;

    public ExamClientAuditLog recordLog(ExamClientAuditLogRequest request, String currentUserId,
            String currentUserStudyProgramId) throws IOException {
        ExamClientAuditLog auditLog = new ExamClientAuditLog();

        auditLog.setIdUjian(request.getIdUjian());
        auditLog.setIdPeserta(currentUserId);
        auditLog.setSessionId(request.getSessionId());
        auditLog.setStudyProgramId(resolveStudyProgramId(request.getStudyProgramId(), currentUserStudyProgramId));
        auditLog.setEventType(request.getEventType());
        auditLog.setPlatform(request.getPlatform());
        auditLog.setClientEventId(request.getClientEventId());
        auditLog.setSegmentName(request.getSegmentName());
        auditLog.setStatus(request.getStatus());
        auditLog.setAttemptNumber(request.getAttemptNumber());
        auditLog.setFailureCount(request.getFailureCount());
        auditLog.setRetryCount(request.getRetryCount());
        auditLog.setMessage(request.getMessage());
        auditLog.setErrorMessage(request.getErrorMessage());
        auditLog.setDeviceInfo(request.getDeviceInfo());
        auditLog.setNetworkInfo(request.getNetworkInfo());
        auditLog.setDownloadInfo(request.getDownloadInfo());
        auditLog.setUploadInfo(request.getUploadInfo());
        auditLog.setEventData(request.getEventData());
        auditLog.setEventTime(Instant.now());

        return auditLogRepository.save(auditLog);
    }

    public List<ExamClientAuditLog> getByUjian(String idUjian, int limit) throws IOException {
        return auditLogRepository.findByUjianId(idUjian, limit);
    }

    public List<ExamClientAuditLog> getBySession(String sessionId, int limit) throws IOException {
        return auditLogRepository.findBySessionId(sessionId, limit);
    }

    public List<ExamClientAuditLog> getByPeserta(String idPeserta, int limit) throws IOException {
        return auditLogRepository.findByPesertaId(idPeserta, limit);
    }

    private String resolveStudyProgramId(String requestStudyProgramId, String currentUserStudyProgramId) {
        if (requestStudyProgramId != null && !requestStudyProgramId.trim().isEmpty()) {
            return requestStudyProgramId;
        }
        return currentUserStudyProgramId;
    }
}
