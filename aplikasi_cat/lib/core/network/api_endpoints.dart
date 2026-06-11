class ApiEndpoints {
  const ApiEndpoints._();

  static const authSignin = '/auth/signin';

  static const ujian = '/ujian';

  static const ujianSessionResumeOrStart = '/ujian-session/resume-or-start';
  static const ujianSessionSubmit = '/ujian-session/submit';

  static String ujianSessionValidateStart(String idUjian, String idPeserta) {
    return '/ujian-session/validate-start/$idUjian/$idPeserta';
  }

  static String ujianSessionProgress(String idUjian, String idPeserta) {
    return '/ujian-session/progress/$idUjian/$idPeserta';
  }

  static String ujianSessionActive(String idUjian, String idPeserta) {
    return '/ujian-session/active/$idUjian/$idPeserta';
  }

  static const recordViolation = '/cheat-detection/record-violation';
}
