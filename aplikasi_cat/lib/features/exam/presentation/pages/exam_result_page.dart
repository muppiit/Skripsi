import 'package:flutter/material.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../shared/widgets/page_shell.dart';
import '../../data/datasources/exam_local_datasource.dart';
import '../../data/datasources/exam_remote_datasource.dart';

enum FinalUploadStatus { idle, uploading, success, failed }

class ExamResultPage extends StatefulWidget {
  const ExamResultPage({super.key});

  @override
  State<ExamResultPage> createState() => _ExamResultPageState();
}

class _ExamResultPageState extends State<ExamResultPage> {
  final _localDatasource = ExamLocalDatasource();
  final _remoteDatasource = ExamRemoteDatasource();

  Map<String, dynamic> _routeData = {};
  Map<String, dynamic>? _submission;
  Map<String, dynamic>? _serverResult;
  String? _idUjian;
  String? _idPeserta;

  FinalUploadStatus _status = FinalUploadStatus.idle;
  double _progress = 0;
  String _message = 'Menunggu upload hasil ujian';

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_routeData.isNotEmpty) return;

    final args = ModalRoute.of(context)?.settings.arguments;
    _routeData = args is Map ? Map<String, dynamic>.from(args) : {};
    _idUjian = _routeData['idUjian']?.toString();
    _idPeserta = _routeData['idPeserta']?.toString();

    _loadSubmission();
  }

  Future<void> _loadSubmission() async {
    if (_idUjian == null || _idPeserta == null) {
      setState(() {
        _status = FinalUploadStatus.failed;
        _message = 'Data ujian atau peserta tidak lengkap.';
      });
      return;
    }

    final submission = await _localDatasource.getFinalSubmission(
      idUjian: _idUjian!,
      idPeserta: _idPeserta!,
    );

    if (!mounted) return;
    setState(() {
      _submission = submission;
      _message = submission == null
          ? 'Tidak ada data upload lokal yang ditemukan.'
          : 'Data ujian tersimpan lokal dan siap diupload.';
    });

    if (submission != null) {
      await Future<void>.delayed(const Duration(milliseconds: 300));
      if (mounted) _uploadSubmission();
    }
  }

  Future<void> _uploadSubmission() async {
    if (_submission == null ||
        _idUjian == null ||
        _idPeserta == null ||
        _status == FinalUploadStatus.uploading) {
      return;
    }

    final sessionId = _submission!['sessionId']?.toString();
    if (sessionId == null || sessionId.isEmpty) {
      setState(() {
        _status = FinalUploadStatus.failed;
        _message =
            'Session ID tidak ditemukan. Mulai ulang ujian diperlukan untuk upload.';
      });
      return;
    }

    setState(() {
      _status = FinalUploadStatus.uploading;
      _progress = 0.1;
      _message = 'Menyiapkan data upload...';
    });

    try {
      final violations = _normalizeViolations(
        _extractViolationList(_submission!['violations']),
        fallbackSessionId: sessionId,
      );
      var failedViolationUploads = 0;

      if (violations.isNotEmpty) {
        setState(() {
          _progress = 0.3;
          _message = 'Mengupload ${violations.length} log pelanggaran...';
        });

        for (final violation in violations) {
          try {
            await _remoteDatasource.recordViolation(violation);
          } catch (_) {
            failedViolationUploads++;
          }
        }
      }

      setState(() {
        _progress = 0.7;
        _message = 'Mengupload jawaban akhir...';
      });

      final response = await _remoteDatasource.submitUjian(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
        sessionId: sessionId,
        answers: _extractMap(_submission!['answers']) ?? {},
        isAutoSubmit: _submission!['isAutoSubmit'] == true,
        finalTimeRemaining:
            int.tryParse('${_submission!['finalTimeRemaining'] ?? 0}') ?? 0,
      );

      await _localDatasource.removeFinalSubmission(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
      );

      if (!mounted) return;
      setState(() {
        _serverResult = _extractMap(response['data']) ?? response;
        _status = FinalUploadStatus.success;
        _progress = 1;
        _message = failedViolationUploads > 0
            ? 'Jawaban berhasil diupload. $failedViolationUploads log pelanggaran gagal dikirim.'
            : 'Upload berhasil. Hasil ujian sudah tersimpan di server.';
      });
    } on ApiException catch (error) {
      if (!mounted) return;
      setState(() {
        _status = FinalUploadStatus.failed;
        _progress = 0;
        _message = error.message;
      });
    } catch (_) {
      if (!mounted) return;
      setState(() {
        _status = FinalUploadStatus.failed;
        _progress = 0;
        _message =
            'Upload gagal. Data tetap tersimpan lokal dan bisa diupload ulang.';
      });
    }
  }

  Map<String, dynamic>? _extractMap(dynamic value) {
    if (value is Map<String, dynamic>) return value;
    if (value is Map) return Map<String, dynamic>.from(value);
    return null;
  }

  List<Map<String, dynamic>> _extractViolationList(dynamic value) {
    if (value is! List) return [];
    return value.map(_extractMap).whereType<Map<String, dynamic>>().toList();
  }

  List<Map<String, dynamic>> _normalizeViolations(
    List<Map<String, dynamic>> violations, {
    required String fallbackSessionId,
  }) {
    return violations
        .map((violation) {
          final typeViolation =
              violation['typeViolation'] ??
              violation['violationType'] ??
              violation['type'] ??
              'APP_BACKGROUND';
          final detectedAt =
              violation['detectedAt'] ??
              violation['timestamp'] ??
              DateTime.now().toIso8601String();

          return {
            ...violation,
            'sessionId': violation['sessionId'] ?? fallbackSessionId,
            'idUjian': violation['idUjian'] ?? _idUjian,
            'idPeserta': violation['idPeserta'] ?? _idPeserta,
            'typeViolation': typeViolation,
            'severity': violation['severity'] ?? 'MEDIUM',
            'timestamp': detectedAt,
            'detectionTimestamp':
                violation['detectionTimestamp'] ??
                DateTime.now().millisecondsSinceEpoch,
            'frontendEvents': {
              'platform': 'flutter_android',
              'source': 'mobile_offline',
              ...(_extractMap(violation['frontendEvents']) ?? {}),
            },
          };
        })
        .where(
          (violation) =>
              violation['sessionId'] != null &&
              violation['idUjian'] != null &&
              violation['idPeserta'] != null &&
              violation['typeViolation'] != null,
        )
        .toList();
  }

  Color _statusColor(BuildContext context) {
    switch (_status) {
      case FinalUploadStatus.success:
        return Colors.green;
      case FinalUploadStatus.failed:
        return Theme.of(context).colorScheme.error;
      case FinalUploadStatus.uploading:
        return Theme.of(context).colorScheme.primary;
      case FinalUploadStatus.idle:
        return Colors.orange;
    }
  }

  IconData _statusIcon() {
    switch (_status) {
      case FinalUploadStatus.success:
        return Icons.check_circle_outline;
      case FinalUploadStatus.failed:
        return Icons.error_outline;
      case FinalUploadStatus.uploading:
        return Icons.cloud_upload_outlined;
      case FinalUploadStatus.idle:
        return Icons.pending_actions_outlined;
    }
  }

  @override
  Widget build(BuildContext context) {
    final answeredCount = _extractMap(_submission?['answers'])?.length ?? 0;
    final violationCount = _extractViolationList(
      _submission?['violations'],
    ).length;

    return PageShell(
      title: 'Ujian Selesai',
      subtitle: 'Upload jawaban ke server',
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Icon(_statusIcon(), size: 72, color: _statusColor(context)),
              const SizedBox(height: 16),
              Text(
                _status == FinalUploadStatus.success
                    ? 'Upload Berhasil'
                    : 'Data Ujian Tersimpan Lokal',
                textAlign: TextAlign.center,
                style: Theme.of(
                  context,
                ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700),
              ),
              const SizedBox(height: 8),
              Text(_message, textAlign: TextAlign.center),
              const SizedBox(height: 20),
              LinearProgressIndicator(
                value: _status == FinalUploadStatus.uploading
                    ? _progress
                    : null,
                borderRadius: BorderRadius.circular(999),
              ),
              const SizedBox(height: 20),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                alignment: WrapAlignment.center,
                children: [
                  Chip(
                    avatar: const Icon(Icons.edit_note_outlined, size: 18),
                    label: Text('$answeredCount jawaban'),
                  ),
                  Chip(
                    avatar: const Icon(Icons.warning_amber_outlined, size: 18),
                    label: Text('$violationCount pelanggaran'),
                  ),
                ],
              ),
              if (_serverResult != null) ...[
                const Divider(height: 32),
                _ResultRow(
                  label: 'Skor',
                  value: '${_serverResult!['totalSkor'] ?? '-'}',
                ),
                _ResultRow(
                  label: 'Persentase',
                  value: '${_serverResult!['persentase'] ?? '-'}%',
                ),
                _ResultRow(
                  label: 'Status',
                  value: _serverResult!['lulus'] == true
                      ? 'Lulus'
                      : 'Tidak Lulus',
                ),
              ],
              const SizedBox(height: 20),
              if (_submission != null && _status != FinalUploadStatus.success)
                FilledButton.icon(
                  onPressed: _status == FinalUploadStatus.uploading
                      ? null
                      : _uploadSubmission,
                  icon: _status == FinalUploadStatus.uploading
                      ? const SizedBox(
                          width: 18,
                          height: 18,
                          child: CircularProgressIndicator(
                            strokeWidth: 2.2,
                            color: Colors.white,
                          ),
                        )
                      : const Icon(Icons.cloud_upload_outlined),
                  label: Text(
                    _status == FinalUploadStatus.uploading
                        ? 'Mengupload...'
                        : 'Upload Jawaban',
                  ),
                ),
              if (_status == FinalUploadStatus.success)
                FilledButton.icon(
                  onPressed: () {
                    Navigator.popUntil(context, (route) => route.isFirst);
                  },
                  icon: const Icon(Icons.home_outlined),
                  label: const Text('Kembali ke Awal'),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

class _ResultRow extends StatelessWidget {
  const _ResultRow({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Expanded(child: Text(label)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.w700)),
        ],
      ),
    );
  }
}
