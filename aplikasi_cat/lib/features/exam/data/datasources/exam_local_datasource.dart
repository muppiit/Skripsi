import '../../../../core/storage/local_storage_keys.dart';
import '../../../../core/storage/local_storage_service.dart';

class ExamLocalDatasource {
  Future<void> saveExamPackage({
    required String idUjian,
    required String idPeserta,
    required Map<String, dynamic> package,
  }) {
    return LocalStorageService.upsertMap(
      table: LocalStorageKeys.examPackagesTable,
      key: LocalStorageKeys.examPackage(idUjian, idPeserta),
      idUjian: idUjian,
      idPeserta: idPeserta,
      value: {
        ...package,
        'idUjian': idUjian,
        'idPeserta': idPeserta,
        'downloadedAt': DateTime.now().toIso8601String(),
      },
    );
  }

  Future<Map<String, dynamic>?> getExamPackage({
    required String idUjian,
    required String idPeserta,
  }) {
    return LocalStorageService.readMap(
      table: LocalStorageKeys.examPackagesTable,
      key: LocalStorageKeys.examPackage(idUjian, idPeserta),
    );
  }

  Future<void> saveAnswerDraft({
    required String idUjian,
    required String idPeserta,
    required Map<String, dynamic> answers,
    int currentSoalIndex = 0,
    int? timeRemaining,
  }) {
    return LocalStorageService.upsertMap(
      table: LocalStorageKeys.answerDraftsTable,
      key: LocalStorageKeys.answerDraft(idUjian, idPeserta),
      idUjian: idUjian,
      idPeserta: idPeserta,
      value: {
        'idUjian': idUjian,
        'idPeserta': idPeserta,
        'answers': answers,
        'currentSoalIndex': currentSoalIndex,
        if (timeRemaining != null) 'timeRemaining': timeRemaining,
        'updatedAt': DateTime.now().toIso8601String(),
      },
    );
  }

  Future<Map<String, dynamic>?> getAnswerDraft({
    required String idUjian,
    required String idPeserta,
  }) {
    return LocalStorageService.readMap(
      table: LocalStorageKeys.answerDraftsTable,
      key: LocalStorageKeys.answerDraft(idUjian, idPeserta),
    );
  }

  Future<void> addViolationLog(Map<String, dynamic> violation) {
    final key =
        violation['id']?.toString() ??
        'violation:${DateTime.now().microsecondsSinceEpoch}';

    return LocalStorageService.upsertMap(
      table: LocalStorageKeys.violationLogsTable,
      key: key,
      idUjian: violation['idUjian']?.toString(),
      idPeserta: violation['idPeserta']?.toString(),
      value: {...violation, 'storedAt': DateTime.now().toIso8601String()},
    );
  }

  Future<List<Map<String, dynamic>>> getViolationLogs({
    String? idUjian,
    String? idPeserta,
  }) {
    return LocalStorageService.readAllMaps(
      table: LocalStorageKeys.violationLogsTable,
      idUjian: idUjian,
      idPeserta: idPeserta,
    );
  }

  Future<void> saveFinalSubmission({
    required String idUjian,
    required String idPeserta,
    required Map<String, dynamic> submission,
  }) {
    return LocalStorageService.upsertMap(
      table: LocalStorageKeys.finalSubmissionsTable,
      key: LocalStorageKeys.finalSubmission(idUjian, idPeserta),
      idUjian: idUjian,
      idPeserta: idPeserta,
      value: {
        ...submission,
        'idUjian': idUjian,
        'idPeserta': idPeserta,
        'storedAt': DateTime.now().toIso8601String(),
      },
    );
  }

  Future<Map<String, dynamic>?> getFinalSubmission({
    required String idUjian,
    required String idPeserta,
  }) {
    return LocalStorageService.readMap(
      table: LocalStorageKeys.finalSubmissionsTable,
      key: LocalStorageKeys.finalSubmission(idUjian, idPeserta),
    );
  }

  Future<void> removeFinalSubmission({
    required String idUjian,
    required String idPeserta,
  }) {
    return LocalStorageService.deleteByKey(
      table: LocalStorageKeys.finalSubmissionsTable,
      key: LocalStorageKeys.finalSubmission(idUjian, idPeserta),
    );
  }
}
