package com.doyatama.university.controller;

import com.doyatama.university.model.ExamClientAuditLog;
import com.doyatama.university.payload.ApiResponse;
import com.doyatama.university.payload.ExamClientAuditLogRequest;
import com.doyatama.university.security.CurrentUser;
import com.doyatama.university.security.UserPrincipal;
import com.doyatama.university.service.ExamClientAuditLogService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exam-client-audit")
public class ExamClientAuditLogController {

    @Autowired
    private ExamClientAuditLogService auditLogService;

    @PostMapping("/log")
    public ResponseEntity<?> recordLog(
            @Valid @RequestBody ExamClientAuditLogRequest request,
            @CurrentUser UserPrincipal currentUser) throws IOException {
        try {
            ExamClientAuditLog auditLog = auditLogService.recordLog(
                    request,
                    currentUser.getId(),
                    currentUser.getSchoolId());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("idLog", auditLog.getIdLog());
            responseData.put("eventType", auditLog.getEventType());
            responseData.put("status", auditLog.getStatus());
            responseData.put("eventTime", auditLog.getEventTime());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Audit log berhasil dicatat");
            response.put("data", responseData);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Terjadi kesalahan sistem: " + e.getMessage()));
        }
    }

    @GetMapping("/ujian/{idUjian}")
    public ResponseEntity<?> getByUjian(
            @PathVariable String idUjian,
            @RequestParam(defaultValue = "1000") int limit) throws IOException {
        List<ExamClientAuditLog> logs = auditLogService.getByUjian(idUjian, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Audit log berhasil diambil");
        response.put("data", logs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getBySession(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1000") int limit) throws IOException {
        List<ExamClientAuditLog> logs = auditLogService.getBySession(sessionId, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Audit log berhasil diambil");
        response.put("data", logs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/peserta/{idPeserta}")
    public ResponseEntity<?> getByPeserta(
            @PathVariable String idPeserta,
            @RequestParam(defaultValue = "1000") int limit) throws IOException {
        List<ExamClientAuditLog> logs = auditLogService.getByPeserta(idPeserta, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Audit log berhasil diambil");
        response.put("data", logs);
        return ResponseEntity.ok(response);
    }
}
