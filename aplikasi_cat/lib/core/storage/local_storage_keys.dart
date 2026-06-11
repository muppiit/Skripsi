class LocalStorageKeys {
  const LocalStorageKeys._();

  static const examPackagesTable = 'exam_packages';
  static const answerDraftsTable = 'answer_drafts';
  static const violationLogsTable = 'violation_logs';
  static const finalSubmissionsTable = 'final_submissions';

  static String examPackage(String idUjian, String idPeserta) {
    return '$idUjian:$idPeserta';
  }

  static String answerDraft(String idUjian, String idPeserta) {
    return '$idUjian:$idPeserta';
  }

  static String finalSubmission(String idUjian, String idPeserta) {
    return '$idUjian:$idPeserta';
  }
}
