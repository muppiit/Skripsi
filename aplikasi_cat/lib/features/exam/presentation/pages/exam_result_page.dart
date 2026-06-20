import 'dart:convert';

import 'package:cryptography/cryptography.dart';
import 'package:flutter/material.dart';

import '../../../../core/device/client_audit_info.dart';
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
  int _uploadFailureCount = 0;
  int _uploadRetryCount = 0;

  static const _uploadStatusPending = 'PENDING';
  static const _uploadStatusUploading = 'UPLOADING';
  static const _uploadStatusUploaded = 'UPLOADED';
  static const _uploadStatusVerified = 'VERIFIED';
  static const _uploadStatusComplete = 'COMPLETE';

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
      var workingSubmission = await _ensureUploadManifest(_submission!);
      await _saveWorkingSubmission(workingSubmission);
      _uploadRetryCount += 1;
      await _recordAuditLog(
        eventType: 'UPLOAD_STARTED',
        status: 'STARTED',
        message: 'Mulai upload hasil ujian',
        uploadInfo: {
          'strategy': 'SEGMENTED_UPLOAD_IDEMPOTENCY',
          'retryCount': _uploadRetryCount,
          'totalViolations': _extractViolationList(
            workingSubmission['violations'],
          ).length,
          'answeredCount':
              _extractMap(workingSubmission['answers'])?.length ?? 0,
        },
      );

      final violations = _normalizeViolations(
        _extractViolationList(workingSubmission['violations']),
        fallbackSessionId: sessionId,
      );
      final violationsChecksum = await _hashPayload(violations);
      var uploadManifest = _extractMap(workingSubmission['uploadManifest'])!;
      var manifestSegments = _extractMap(uploadManifest['segments'])!;
      final violationSegment =
          _extractMap(manifestSegments['violations']) ?? {};

      if (violationSegment['status'] != _uploadStatusUploaded ||
          violationSegment['checksum'] != violationsChecksum) {
        setState(() {
          _progress = 0.3;
          _message = 'Mengupload ${violations.length} log pelanggaran...';
        });

        manifestSegments['violations'] = {
          ...violationSegment,
          'status': _uploadStatusUploading,
          'checksum': violationsChecksum,
        };
        uploadManifest = {
          ...uploadManifest,
          'status': _uploadStatusUploading,
          'segments': manifestSegments,
        };
        workingSubmission = {
          ...workingSubmission,
          'uploadManifest': uploadManifest,
        };
        await _saveWorkingSubmission(workingSubmission);

        for (final violation in violations) {
          await _remoteDatasource.recordViolation({
            ...violation,
            'clientEventId':
                violation['clientEventId'] ??
                violation['id'] ??
                '${workingSubmission['idempotencyKey']}::violation::${violation['typeViolation'] ?? 'UNKNOWN'}::${violation['timestamp'] ?? violation['detectedAt'] ?? ''}',
            'uploadIdempotencyKey': workingSubmission['idempotencyKey'],
          });
        }

        manifestSegments['violations'] = {
          'status': _uploadStatusUploaded,
          'checksum': violationsChecksum,
          'uploadedAt': DateTime.now().toIso8601String(),
        };
        uploadManifest = {...uploadManifest, 'segments': manifestSegments};
        workingSubmission = {
          ...workingSubmission,
          'uploadManifest': uploadManifest,
        };
        await _saveWorkingSubmission(workingSubmission);
        await _recordAuditLog(
          eventType: 'UPLOAD_SEGMENT_COMPLETED',
          status: 'COMPLETED',
          segmentName: 'violations',
          message: 'Log pelanggaran berhasil diupload',
          uploadInfo: {
            'segmentName': 'violations',
            'totalViolations': violations.length,
            'checksum': violationsChecksum,
          },
        );
      }

      setState(() {
        _progress = 0.7;
        _message = 'Mengupload jawaban akhir...';
      });

      final finalSubmissionPayload = {
        'idUjian': _idUjian!,
        'idPeserta': _idPeserta!,
        'sessionId': sessionId,
        'answers': _extractMap(workingSubmission['answers']) ?? {},
        'isAutoSubmit': workingSubmission['isAutoSubmit'] == true,
        'finalTimeRemaining':
            int.tryParse('${workingSubmission['finalTimeRemaining'] ?? 0}') ??
            0,
        'submittedAt': workingSubmission['submittedAt'],
        'metadata': _extractMap(workingSubmission['metadata']) ?? {},
        'violations': violations,
        'idempotencyKey': workingSubmission['idempotencyKey']?.toString(),
      };
      final finalChecksum = await _hashPayload(finalSubmissionPayload);
      final finalSegment =
          _extractMap(manifestSegments['finalSubmission']) ?? {};
      Map<String, dynamic> response =
          _extractMap(finalSegment['serverResult']) ?? {};

      if (finalSegment['status'] != _uploadStatusVerified ||
          finalSegment['checksum'] != finalChecksum ||
          response.isEmpty) {
        manifestSegments['finalSubmission'] = {
          ...finalSegment,
          'status': _uploadStatusUploading,
          'checksum': finalChecksum,
        };
        uploadManifest = {
          ...uploadManifest,
          'status': _uploadStatusUploading,
          'segments': manifestSegments,
        };
        workingSubmission = {
          ...workingSubmission,
          'uploadManifest': uploadManifest,
        };
        await _saveWorkingSubmission(workingSubmission);

        response = await _remoteDatasource.submitUjian(
          idUjian: _idUjian!,
          idPeserta: _idPeserta!,
          sessionId: sessionId,
          answers: _extractMap(workingSubmission['answers']) ?? {},
          isAutoSubmit: workingSubmission['isAutoSubmit'] == true,
          finalTimeRemaining:
              int.tryParse('${workingSubmission['finalTimeRemaining'] ?? 0}') ??
              0,
          submittedAt: workingSubmission['submittedAt']?.toString(),
          metadata: _extractMap(workingSubmission['metadata']) ?? {},
          violations: violations,
          idempotencyKey: workingSubmission['idempotencyKey']?.toString(),
          uploadManifest: uploadManifest,
        );

        manifestSegments['finalSubmission'] = {
          'status': _uploadStatusVerified,
          'checksum': finalChecksum,
          'uploadedAt': DateTime.now().toIso8601String(),
          'serverResult': response,
        };
        uploadManifest = {...uploadManifest, 'segments': manifestSegments};
        workingSubmission = {
          ...workingSubmission,
          'uploadManifest': uploadManifest,
        };
        await _saveWorkingSubmission(workingSubmission);
        await _recordAuditLog(
          eventType: 'UPLOAD_SEGMENT_COMPLETED',
          status: 'COMPLETED',
          segmentName: 'finalSubmission',
          message: 'Jawaban akhir berhasil diupload',
          uploadInfo: {
            'segmentName': 'finalSubmission',
            'answeredCount':
                _extractMap(workingSubmission['answers'])?.length ?? 0,
            'checksum': finalChecksum,
          },
        );
      }

      uploadManifest = {
        ...uploadManifest,
        'status': _uploadStatusComplete,
        'completedAt': DateTime.now().toIso8601String(),
      };
      workingSubmission = {
        ...workingSubmission,
        'uploadManifest': uploadManifest,
      };
      await _saveWorkingSubmission(workingSubmission);

      await _localDatasource.removeFinalSubmission(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
      );
      await _recordAuditLog(
        eventType: 'UPLOAD_COMPLETED',
        status: 'COMPLETED',
        message: 'Upload hasil ujian selesai',
        uploadInfo: {
          'strategy': 'SEGMENTED_UPLOAD_IDEMPOTENCY',
          'resultId': _extractMap(response['data'])?['idHasilUjian'],
        },
      );

      if (!mounted) return;
      setState(() {
        _serverResult = _extractMap(response['data']) ?? response;
        _status = FinalUploadStatus.success;
        _progress = 1;
        _message = 'Upload berhasil. Hasil ujian sudah tersimpan di server.';
      });
    } on ApiException catch (error) {
      _uploadFailureCount += 1;
      await _recordAuditLog(
        eventType: 'UPLOAD_FAILED',
        status: 'FAILED',
        errorMessage: error.message,
        uploadInfo: {
          'strategy': 'SEGMENTED_UPLOAD_IDEMPOTENCY',
          'failureCount': _uploadFailureCount,
          'retryCount': _uploadRetryCount,
        },
      );
      if (!mounted) return;
      setState(() {
        _status = FinalUploadStatus.failed;
        _progress = 0;
        _message = error.message;
      });
    } catch (error) {
      _uploadFailureCount += 1;
      await _recordAuditLog(
        eventType: 'UPLOAD_FAILED',
        status: 'FAILED',
        errorMessage: error.toString(),
        uploadInfo: {
          'strategy': 'SEGMENTED_UPLOAD_IDEMPOTENCY',
          'failureCount': _uploadFailureCount,
          'retryCount': _uploadRetryCount,
        },
      );
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

  Future<Map<String, dynamic>> _ensureUploadManifest(
    Map<String, dynamic> submission,
  ) async {
    if (submission['idempotencyKey'] == null) {
      submission = {
        ...submission,
        'idempotencyKey': '$_idUjian::$_idPeserta::${submission['sessionId']}',
      };
    }

    if (submission['uploadManifest'] is Map) return submission;

    final violations = _normalizeViolations(
      _extractViolationList(submission['violations']),
      fallbackSessionId: submission['sessionId']?.toString() ?? '',
    );
    final finalSubmission = {
      'idUjian': _idUjian,
      'idPeserta': _idPeserta,
      'sessionId': submission['sessionId'],
      'answers': _extractMap(submission['answers']) ?? {},
      'isAutoSubmit': submission['isAutoSubmit'] == true,
      'finalTimeRemaining':
          int.tryParse('${submission['finalTimeRemaining'] ?? 0}') ?? 0,
      'submittedAt': submission['submittedAt'],
      'metadata': _extractMap(submission['metadata']) ?? {},
      'idempotencyKey': submission['idempotencyKey'],
    };

    return {
      ...submission,
      'violations': violations,
      'uploadManifest': {
        'version': 1,
        'strategy': 'SEGMENTED_UPLOAD_IDEMPOTENCY',
        'idempotencyKey': submission['idempotencyKey'],
        'status': _uploadStatusPending,
        'segments': {
          'violations': {
            'status': _uploadStatusPending,
            'checksum': await _hashPayload(violations),
            'uploadedAt': null,
          },
          'finalSubmission': {
            'status': _uploadStatusPending,
            'checksum': await _hashPayload(finalSubmission),
            'uploadedAt': null,
            'serverResult': null,
          },
        },
        'completedAt': null,
      },
    };
  }

  Future<void> _recordAuditLog({
    required String eventType,
    required String status,
    String? segmentName,
    String? message,
    String? errorMessage,
    Map<String, dynamic> uploadInfo = const {},
    Map<String, dynamic> eventData = const {},
  }) async {
    if (_idUjian == null || _idPeserta == null) return;

    try {
      if (!mounted) return;
      final deviceInfo = ClientAuditInfo.deviceInfo(context);
      final networkInfo = await ClientAuditInfo.networkInfo();

      await _remoteDatasource.recordAuditLog({
        'idUjian': _idUjian,
        'idPeserta': _idPeserta,
        'sessionId':
            _submission?['sessionId']?.toString() ??
            _routeData['sessionId']?.toString(),
        'studyProgramId':
            _routeData['studyProgramId']?.toString() ??
            _routeData['study_program_id']?.toString(),
        'eventType': eventType,
        'platform': 'ANDROID',
        'clientEventId':
            '$_idUjian::$_idPeserta::${_submission?['sessionId'] ?? _routeData['sessionId'] ?? 'no-session'}::$eventType::${segmentName ?? 'general'}::${DateTime.now().millisecondsSinceEpoch}',
        'segmentName': segmentName,
        'status': status,
        'failureCount': _uploadFailureCount,
        'retryCount': _uploadRetryCount,
        'message': message,
        'errorMessage': errorMessage,
        'deviceInfo': deviceInfo,
        'networkInfo': networkInfo,
        'uploadInfo': uploadInfo,
        'eventData': eventData,
      });
    } catch (_) {
      // Audit log must never block final submission retry.
    }
  }

  Future<void> _saveWorkingSubmission(Map<String, dynamic> submission) async {
    await _localDatasource.saveFinalSubmission(
      idUjian: _idUjian!,
      idPeserta: _idPeserta!,
      submission: submission,
    );
    if (mounted) {
      setState(() => _submission = submission);
    }
  }

  Future<String> _hashPayload(dynamic value) async {
    final hash = await Sha256().hash(utf8.encode(_stableJson(value)));
    return hash.bytes
        .map((byte) => byte.toRadixString(16).padLeft(2, '0'))
        .join();
  }

  String _stableJson(dynamic value) {
    if (value == null || value is num || value is bool || value is String) {
      return jsonEncode(value);
    }

    if (value is List) {
      return '[${value.map(_stableJson).join(',')}]';
    }

    if (value is Map) {
      final keys = value.keys.map((key) => key.toString()).toList()..sort();
      return '{${keys.map((key) => '${jsonEncode(key)}:${_stableJson(value[key])}').join(',')}}';
    }

    return jsonEncode(value.toString());
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
