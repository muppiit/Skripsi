import 'package:flutter/material.dart';

import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/exam/presentation/pages/exam_code_page.dart';
import '../../features/exam/presentation/pages/exam_download_page.dart';
import '../../features/exam/presentation/pages/exam_result_page.dart';
import '../../features/exam/presentation/pages/exam_work_page.dart';

class AppRoutes {
  const AppRoutes._();

  static const login = '/';
  static const examCode = '/exam-code';
  static const examDownload = '/exam-download';
  static const examWork = '/exam-work';
  static const examResult = '/exam-result';

  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    return MaterialPageRoute(
      settings: settings,
      builder: (_) {
        switch (settings.name) {
          case login:
            return const LoginPage();
          case examCode:
            return const ExamCodePage();
          case examDownload:
            return const ExamDownloadPage();
          case examWork:
            return const ExamWorkPage();
          case examResult:
            return const ExamResultPage();
          default:
            return const LoginPage();
        }
      },
    );
  }
}
