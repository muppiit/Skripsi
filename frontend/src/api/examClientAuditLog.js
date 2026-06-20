import request from "../utils/request";

export function recordExamClientAuditLog(data) {
  return request({
    url: "/exam-client-audit/log",
    method: "post",
    data,
  });
}

export function getExamClientAuditLogsByUjian(idUjian, limit = 1000) {
  return request({
    url: `/exam-client-audit/ujian/${idUjian}`,
    method: "get",
    params: { limit },
  });
}

export function getExamClientAuditLogsBySession(sessionId, limit = 1000) {
  return request({
    url: `/exam-client-audit/session/${sessionId}`,
    method: "get",
    params: { limit },
  });
}

export function getExamClientAuditLogsByPeserta(idPeserta, limit = 1000) {
  return request({
    url: `/exam-client-audit/peserta/${idPeserta}`,
    method: "get",
    params: { limit },
  });
}

export default {
  recordExamClientAuditLog,
  getExamClientAuditLogsByUjian,
  getExamClientAuditLogsBySession,
  getExamClientAuditLogsByPeserta,
};
