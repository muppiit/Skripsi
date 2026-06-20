import '../../../../core/network/api_client.dart';
import '../../../../core/network/api_endpoints.dart';

class ExamRemoteDatasource {
  ExamRemoteDatasource({ApiClient? apiClient})
    : _apiClient = apiClient ?? ApiClient();

  final ApiClient _apiClient;

  Future<Map<String, dynamic>> getAllUjian({int size = 1000}) {
    return _apiClient.get(ApiEndpoints.ujian, queryParameters: {'size': size});
  }

  Future<Map<String, dynamic>> validateCanStart({
    required String idUjian,
    required String idPeserta,
  }) {
    return _apiClient.get(
      ApiEndpoints.ujianSessionValidateStart(idUjian, idPeserta),
    );
  }

  Future<Map<String, dynamic>> resumeOrStartSession({
    required String idUjian,
    required String idPeserta,
    required String kodeUjian,
  }) {
    return _apiClient.post(
      ApiEndpoints.ujianSessionResumeOrStart,
      data: {
        'idUjian': idUjian,
        'idPeserta': idPeserta,
        'kodeUjian': kodeUjian,
      },
    );
  }

  Future<Map<String, dynamic>> submitUjian({
    required String idUjian,
    required String idPeserta,
    required String sessionId,
    required Map<String, dynamic> answers,
    required bool isAutoSubmit,
    required int finalTimeRemaining,
    String? submittedAt,
    Map<String, dynamic>? metadata,
    List<Map<String, dynamic>>? violations,
    String? idempotencyKey,
    Map<String, dynamic>? uploadManifest,
  }) {
    return _apiClient.post(
      ApiEndpoints.ujianSessionSubmit,
      data: {
        'idUjian': idUjian,
        'idPeserta': idPeserta,
        'sessionId': sessionId,
        'answers': answers,
        'isAutoSubmit': isAutoSubmit,
        'finalTimeRemaining': finalTimeRemaining,
        if (submittedAt != null) 'submittedAt': submittedAt,
        if (metadata != null) 'metadata': metadata,
        if (violations != null) 'violations': violations,
        if (idempotencyKey != null) 'idempotencyKey': idempotencyKey,
        if (uploadManifest != null) 'uploadManifest': uploadManifest,
      },
    );
  }

  Future<Map<String, dynamic>> recordViolation(Map<String, dynamic> violation) {
    return _apiClient.post(ApiEndpoints.recordViolation, data: violation);
  }

  Future<Map<String, dynamic>> recordAuditLog(Map<String, dynamic> auditLog) {
    return _apiClient.post(ApiEndpoints.examClientAuditLog, data: auditLog);
  }
}
