import 'dart:convert';

import 'package:cryptography/cryptography.dart';
import 'package:flutter/material.dart';

import '../../../../core/device/client_audit_info.dart';
import '../../../../core/routes/app_routes.dart';
import '../../../../shared/widgets/page_shell.dart';
import '../../data/datasources/exam_local_datasource.dart';
import '../../data/datasources/exam_remote_datasource.dart';

class ExamDownloadPage extends StatefulWidget {
  const ExamDownloadPage({super.key});

  @override
  State<ExamDownloadPage> createState() => _ExamDownloadPageState();
}

class _ExamDownloadPageState extends State<ExamDownloadPage> {
  final _localDatasource = ExamLocalDatasource();
  final _remoteDatasource = ExamRemoteDatasource();

  Map<String, dynamic> _routeData = {};
  Map<String, dynamic> _ujian = {};
  String? _idUjian;
  String? _idPeserta;
  String? _kodeUjian;

  bool _isCheckingLocalPackage = true;
  bool _isDownloading = false;
  bool _isStarting = false;
  bool _isDownloaded = false;
  double _downloadProgress = 0;
  String _downloadStatus = 'Menunggu download paket ujian';
  int _downloadFailureCount = 0;
  int _downloadRetryCount = 0;

  static const _downloadSegments = [
    'metadata',
    'ujian',
    'soalList',
    'peserta',
    'timer',
    'security',
    'localState',
  ];

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_routeData.isNotEmpty) return;

    final args = ModalRoute.of(context)?.settings.arguments;
    _routeData = args is Map ? Map<String, dynamic>.from(args) : {};
    _ujian = _routeData['ujian'] is Map
        ? Map<String, dynamic>.from(_routeData['ujian'])
        : <String, dynamic>{};
    _idUjian =
        _routeData['idUjian']?.toString() ?? _ujian['idUjian']?.toString();
    _idPeserta = _routeData['idPeserta']?.toString();
    _kodeUjian = _routeData['kodeUjian']?.toString() ?? _getKodeUjian(_ujian);

    _checkLocalPackage();
  }

  Future<void> _checkLocalPackage() async {
    if (_idUjian == null || _idPeserta == null) {
      setState(() {
        _isCheckingLocalPackage = false;
        _downloadStatus = 'Data ujian atau peserta tidak lengkap';
      });
      return;
    }

    final package = await _localDatasource.getExamPackage(
      idUjian: _idUjian!,
      idPeserta: _idPeserta!,
    );

    final isValidPackage = await _validateOfflinePackage(package);

    if (!mounted) return;
    setState(() {
      _isDownloaded = isValidPackage;
      _downloadProgress = isValidPackage ? 1 : 0;
      _downloadStatus = isValidPackage
          ? 'Paket ujian sudah tersedia di SQLite'
          : 'Paket ujian belum diunduh';
      _isCheckingLocalPackage = false;
    });
  }

  Future<void> _downloadExamPackage() async {
    if (_idUjian == null || _idPeserta == null || _isDownloading) return;

    setState(() {
      _isDownloading = true;
      _isDownloaded = false;
      _downloadProgress = 0.1;
      _downloadStatus = 'Menyiapkan resume download...';
    });

    try {
      _downloadRetryCount += 1;
      await _recordAuditLog(
        eventType: 'DOWNLOAD_STARTED',
        status: 'STARTED',
        message: 'Mulai download paket ujian',
        downloadInfo: {
          'strategy': 'SEGMENTED_CHECKSUM',
          'retryCount': _downloadRetryCount,
          'totalSegments': _downloadSegments.length,
        },
      );

      await Future<void>.delayed(const Duration(milliseconds: 250));
      final sanitizedUjian = _sanitizeUjianForOffline(_ujian);
      final soalList = _extractSoalList(sanitizedUjian);
      final segments = await _createDownloadSegments(
        sanitizedUjian: sanitizedUjian,
        soalList: soalList,
      );
      var package =
          await _localDatasource.getExamPackage(
            idUjian: _idUjian!,
            idPeserta: _idPeserta!,
          ) ??
          await _createInitialPackage(segments);

      if (package['downloadManifest'] is! Map) {
        package = {
          ...package,
          'downloadManifest': await _createDownloadManifest(segments),
        };
      }

      for (var index = 0; index < _downloadSegments.length; index += 1) {
        final segmentName = _downloadSegments[index];
        final progressStart = 0.1 + (index / _downloadSegments.length) * 0.76;
        final progressEnd =
            0.1 + ((index + 1) / _downloadSegments.length) * 0.76;
        final expectedChecksum = await _hashPayload(segments[segmentName]);
        final manifest = Map<String, dynamic>.from(
          package['downloadManifest'] as Map,
        );
        final manifestSegments = Map<String, dynamic>.from(
          manifest['segments'] as Map? ?? {},
        );
        final currentSegment = manifestSegments[segmentName] is Map
            ? Map<String, dynamic>.from(manifestSegments[segmentName] as Map)
            : <String, dynamic>{};
        final currentChecksum = await _hashPayload(package[segmentName]);

        if (currentSegment['status'] == 'VERIFIED' &&
            currentSegment['checksum'] == expectedChecksum &&
            currentChecksum == expectedChecksum) {
          if (!mounted) return;
          setState(() {
            _downloadProgress = progressEnd;
            _downloadStatus =
                'Segmen $segmentName sudah valid, lanjut segmen berikutnya...';
          });
          await Future<void>.delayed(const Duration(milliseconds: 120));
          continue;
        }

        if (!mounted) return;
        setState(() {
          _downloadProgress = progressStart;
          _downloadStatus = 'Mendownload segmen $segmentName...';
        });

        manifest['status'] = 'DOWNLOADING';
        manifestSegments[segmentName] = {
          'status': 'DOWNLOADING',
          'checksum': expectedChecksum,
          'verifiedAt': null,
        };
        package = {
          ...package,
          'downloadManifest': {...manifest, 'segments': manifestSegments},
        };
        await _localDatasource.saveExamPackage(
          idUjian: _idUjian!,
          idPeserta: _idPeserta!,
          package: package,
        );

        await Future<void>.delayed(const Duration(milliseconds: 250));
        package = {...package, segmentName: segments[segmentName]};
        final savedChecksum = await _hashPayload(package[segmentName]);

        if (savedChecksum != expectedChecksum) {
          manifestSegments[segmentName] = {
            'status': 'FAILED',
            'checksum': expectedChecksum,
            'verifiedAt': null,
          };
          package = {
            ...package,
            'downloadManifest': {...manifest, 'segments': manifestSegments},
          };
          await _localDatasource.saveExamPackage(
            idUjian: _idUjian!,
            idPeserta: _idPeserta!,
            package: package,
          );
          await _recordAuditLog(
            eventType: 'DOWNLOAD_SEGMENT_FAILED',
            status: 'FAILED',
            segmentName: segmentName,
            errorMessage: 'Checksum segmen $segmentName tidak cocok',
            downloadInfo: {
              'strategy': 'SEGMENTED_CHECKSUM',
              'checksum': expectedChecksum,
            },
          );
          throw Exception('Checksum segmen $segmentName tidak cocok');
        }

        manifestSegments[segmentName] = {
          'status': 'VERIFIED',
          'checksum': expectedChecksum,
          'verifiedAt': DateTime.now().toIso8601String(),
        };
        package = {
          ...package,
          'downloadManifest': {...manifest, 'segments': manifestSegments},
        };
        await _localDatasource.saveExamPackage(
          idUjian: _idUjian!,
          idPeserta: _idPeserta!,
          package: package,
        );

        if (!mounted) return;
        setState(() {
          _downloadProgress = progressEnd;
          _downloadStatus = 'Segmen $segmentName berhasil diverifikasi';
        });
      }

      if (!mounted) return;
      setState(() {
        _downloadProgress = 0.9;
        _downloadStatus = 'Memverifikasi paket ujian lokal...';
      });

      final finalManifest = Map<String, dynamic>.from(
        package['downloadManifest'] as Map,
      );
      finalManifest['status'] = 'COMPLETE';
      finalManifest['packageChecksum'] = await _hashPackageSegments(package);
      finalManifest['completedAt'] = DateTime.now().toIso8601String();
      package = {...package, 'downloadManifest': finalManifest};

      await _localDatasource.saveExamPackage(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
        package: package,
      );

      final savedPackage = await _localDatasource.getExamPackage(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
      );

      if (!await _validateOfflinePackage(savedPackage)) {
        throw Exception('Paket ujian lokal tidak lengkap');
      }

      if (!mounted) return;
      setState(() {
        _downloadProgress = 1;
        _downloadStatus = 'Download selesai. Ujian siap dimulai.';
        _isDownloaded = true;
      });

      await _recordAuditLog(
        eventType: 'DOWNLOAD_COMPLETED',
        status: 'COMPLETED',
        message: 'Paket ujian berhasil didownload dan diverifikasi',
        downloadInfo: {
          'strategy': 'SEGMENTED_CHECKSUM',
          'totalSegments': _downloadSegments.length,
          'packageChecksum': finalManifest['packageChecksum'],
        },
      );

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Paket ujian berhasil disimpan ke SQLite'),
          behavior: SnackBarBehavior.floating,
        ),
      );
    } catch (error) {
      _downloadFailureCount += 1;
      await _recordAuditLog(
        eventType: 'DOWNLOAD_FAILED',
        status: 'FAILED',
        errorMessage: error.toString(),
        downloadInfo: {
          'strategy': 'SEGMENTED_CHECKSUM',
          'failureCount': _downloadFailureCount,
          'retryCount': _downloadRetryCount,
        },
      );
      if (!mounted) return;
      setState(() {
        _downloadProgress = 0;
        _downloadStatus = 'Download gagal. Coba ulangi.';
        _isDownloaded = false;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Gagal menyimpan paket ujian ke SQLite'),
          behavior: SnackBarBehavior.floating,
          backgroundColor: Theme.of(context).colorScheme.error,
        ),
      );
    } finally {
      if (mounted) setState(() => _isDownloading = false);
    }
  }

  Future<void> _startExam() async {
    if (_idUjian == null ||
        _idPeserta == null ||
        _kodeUjian == null ||
        _isStarting) {
      return;
    }

    setState(() => _isStarting = true);

    try {
      final sessionResponse = await _remoteDatasource.resumeOrStartSession(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
        kodeUjian: _kodeUjian!,
      );
      final sessionContext = _extractMap(sessionResponse['content']) ?? {};
      final sessionId = sessionContext['sessionId']?.toString();

      if (sessionId == null || sessionId.isEmpty) {
        _showError('Session ujian gagal dibuat.');
        return;
      }

      final existingPackage = await _localDatasource.getExamPackage(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
      );

      if (!await _validateOfflinePackage(existingPackage)) {
        _showError('Paket ujian lokal belum lengkap. Download ulang ujian.');
        setState(() => _isDownloaded = false);
        return;
      }

      await _localDatasource.saveExamPackage(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
        package: {
          ...(existingPackage ?? {}),
          'sessionId': sessionId,
          'sessionContext': sessionContext,
          'startedAt': DateTime.now().toIso8601String(),
        },
      );

      if (!mounted) return;

      Navigator.pushNamed(
        context,
        AppRoutes.examWork,
        arguments: {
          ..._routeData,
          'sessionId': sessionId,
          'sessionContext': sessionContext,
          'offlineReady': true,
        },
      );
    } catch (_) {
      if (!mounted) return;
      _showError('Gagal memulai session ujian. Periksa koneksi backend.');
    } finally {
      if (mounted) setState(() => _isStarting = false);
    }
  }

  Future<void> _recordAuditLog({
    required String eventType,
    required String status,
    String? segmentName,
    String? message,
    String? errorMessage,
    Map<String, dynamic> downloadInfo = const {},
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
        'sessionId': _routeData['sessionId']?.toString(),
        'studyProgramId':
            _routeData['studyProgramId']?.toString() ??
            _routeData['study_program_id']?.toString(),
        'eventType': eventType,
        'platform': 'ANDROID',
        'clientEventId':
            '$_idUjian::$_idPeserta::${_routeData['sessionId'] ?? 'no-session'}::$eventType::${segmentName ?? 'general'}::${DateTime.now().millisecondsSinceEpoch}',
        'segmentName': segmentName,
        'status': status,
        'failureCount': _downloadFailureCount,
        'retryCount': _downloadRetryCount,
        'message': message,
        'errorMessage': errorMessage,
        'deviceInfo': deviceInfo,
        'networkInfo': networkInfo,
        'downloadInfo': downloadInfo,
        'eventData': eventData,
      });
    } catch (_) {
      // Audit log must never block the exam flow.
    }
  }

  Future<Map<String, dynamic>> _createDownloadSegments({
    required Map<String, dynamic> sanitizedUjian,
    required List<dynamic> soalList,
  }) async {
    return {
      'metadata': {
        'id': '$_idUjian:$_idPeserta',
        'version': 2,
        'strategy': 'SEGMENTED_CHECKSUM',
        'source': 'flutter_mobile',
        'downloadedAt': DateTime.now().toIso8601String(),
        'storage': 'sqlite',
        'idUjian': _idUjian,
        'idPeserta': _idPeserta,
        'kodeUjian': _kodeUjian,
        'totalQuestions': soalList.length,
      },
      'ujian': sanitizedUjian,
      'soalList': soalList,
      'peserta': {'idPeserta': _idPeserta, 'kodeUjian': _kodeUjian},
      'timer': {
        'durasiMenit': sanitizedUjian['durasiMenit'],
        'initialTimeLeft': ((sanitizedUjian['durasiMenit'] as num?) ?? 0) * 60,
      },
      'security': {
        'preventCheating': sanitizedUjian['preventCheating'],
        'screenshotBlocked': true,
      },
      'localState': {
        'jawaban': <String, dynamic>{},
        'currentSoal': 0,
        'violations': <dynamic>[],
        'syncStatus': 'LOCAL_READY',
      },
    };
  }

  Future<Map<String, dynamic>> _createInitialPackage(
    Map<String, dynamic> segments,
  ) async {
    final metadata = Map<String, dynamic>.from(segments['metadata'] as Map);
    return {
      'id': metadata['id'],
      'version': metadata['version'],
      'idUjian': _idUjian,
      'idPeserta': _idPeserta,
      'kodeUjian': _kodeUjian,
      'downloadedAt': metadata['downloadedAt'],
      'downloadManifest': await _createDownloadManifest(segments),
    };
  }

  Future<Map<String, dynamic>> _createDownloadManifest(
    Map<String, dynamic> segments,
  ) async {
    final manifestSegments = <String, dynamic>{};

    for (final segmentName in _downloadSegments) {
      manifestSegments[segmentName] = {
        'status': 'PENDING',
        'checksum': await _hashPayload(segments[segmentName]),
        'verifiedAt': null,
      };
    }

    return {
      'version': 1,
      'strategy': 'SEGMENTED_CHECKSUM',
      'status': 'PENDING',
      'segments': manifestSegments,
      'packageChecksum': null,
      'completedAt': null,
    };
  }

  Future<bool> _validateOfflinePackage(Map<String, dynamic>? package) async {
    if (package == null || package['downloadManifest'] is! Map) return false;

    final manifest = Map<String, dynamic>.from(
      package['downloadManifest'] as Map,
    );
    if (manifest['status'] != 'COMPLETE') return false;

    final manifestSegments = manifest['segments'] is Map
        ? Map<String, dynamic>.from(manifest['segments'] as Map)
        : <String, dynamic>{};

    for (final segmentName in _downloadSegments) {
      final segment = manifestSegments[segmentName] is Map
          ? Map<String, dynamic>.from(manifestSegments[segmentName] as Map)
          : <String, dynamic>{};
      if (segment['status'] != 'VERIFIED') return false;

      final checksum = await _hashPayload(package[segmentName]);
      if (checksum != segment['checksum']) return false;
    }

    final packageChecksum = await _hashPackageSegments(package);
    return packageChecksum == manifest['packageChecksum'];
  }

  Future<String> _hashPackageSegments(Map<String, dynamic> package) {
    return _hashPayload({
      for (final segmentName in _downloadSegments)
        segmentName: package[segmentName],
    });
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

  Map<String, dynamic> _sanitizeUjianForOffline(Map<String, dynamic> ujian) {
    final sanitized = _sanitizeValue(ujian);
    if (sanitized is Map<String, dynamic>) return sanitized;
    return {};
  }

  dynamic _sanitizeValue(dynamic value) {
    const sensitiveKeys = {
      'jawabanBenar',
      'kunciJawaban',
      'correctAnswer',
      'correctAnswers',
      'answerKey',
      'pembahasan',
    };

    if (value is Map) {
      final result = <String, dynamic>{};
      for (final entry in value.entries) {
        final key = entry.key.toString();
        if (sensitiveKeys.contains(key)) continue;
        result[key] = _sanitizeValue(entry.value);
      }
      return result;
    }

    if (value is List) {
      return value.map(_sanitizeValue).toList();
    }

    return value;
  }

  List<dynamic> _extractSoalList(Map<String, dynamic> ujian) {
    final candidates = [
      ujian['bankSoalList'],
      ujian['soalList'],
      ujian['questions'],
    ];

    for (final candidate in candidates) {
      if (candidate is List) return candidate;
    }
    return [];
  }

  String? _getKodeUjian(Map<String, dynamic> ujian) {
    final pengaturan = ujian['pengaturan'];
    if (pengaturan is Map) return pengaturan['kodeUjian']?.toString();
    return null;
  }

  Map<String, dynamic>? _extractMap(dynamic value) {
    if (value is Map<String, dynamic>) return value;
    if (value is Map) return Map<String, dynamic>.from(value);
    return null;
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        behavior: SnackBarBehavior.floating,
        backgroundColor: Theme.of(context).colorScheme.error,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final namaUjian = _ujian['namaUjian']?.toString() ?? 'Ujian CAT';
    final deskripsi = _ujian['deskripsi']?.toString();
    final durasiMenit = _ujian['durasiMenit']?.toString() ?? '-';
    final jumlahSoal = _ujian['jumlahSoal']?.toString() ?? '-';
    final statusUjian = _ujian['statusUjian']?.toString() ?? '-';

    return PageShell(
      title: 'Informasi Ujian',
      subtitle: 'Download paket ujian untuk mode offline',
      showBackButton: true,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Container(
                padding: const EdgeInsets.all(18),
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [Color(0xFF2563EB), Color(0xFF1D4ED8)],
                  ),
                  borderRadius: BorderRadius.circular(24),
                ),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      width: 54,
                      height: 54,
                      decoration: BoxDecoration(
                        color: Colors.white.withValues(alpha: 0.18),
                        borderRadius: BorderRadius.circular(18),
                      ),
                      child: const Icon(
                        Icons.assignment_outlined,
                        color: Colors.white,
                        size: 30,
                      ),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            namaUjian,
                            style: Theme.of(context).textTheme.titleLarge
                                ?.copyWith(
                                  color: Colors.white,
                                  fontWeight: FontWeight.w800,
                                ),
                          ),
                          const SizedBox(height: 6),
                          Text(
                            'Kode: ${_kodeUjian ?? '-'}',
                            style: const TextStyle(
                              color: Color(0xFFDBEAFE),
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                          if (deskripsi != null && deskripsi.isNotEmpty) ...[
                            const SizedBox(height: 8),
                            Text(
                              deskripsi,
                              style: const TextStyle(color: Color(0xFFEFF6FF)),
                            ),
                          ],
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 18),
              Row(
                children: [
                  Expanded(
                    child: _InfoTile(
                      label: 'Durasi',
                      value: '$durasiMenit mnt',
                    ),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: _InfoTile(label: 'Soal', value: jumlahSoal),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: _InfoTile(label: 'Status', value: statusUjian),
                  ),
                ],
              ),
              const SizedBox(height: 18),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFFF8FAFC),
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: const Color(0xFFE2E8F0)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Row(
                      children: [
                        Icon(
                          _isDownloaded
                              ? Icons.check_circle_outline
                              : Icons.download_for_offline_outlined,
                          color: _isDownloaded
                              ? const Color(0xFF16A34A)
                              : Theme.of(context).colorScheme.primary,
                        ),
                        const SizedBox(width: 10),
                        Expanded(
                          child: Text(
                            _downloadStatus,
                            style: Theme.of(context).textTheme.bodyMedium
                                ?.copyWith(fontWeight: FontWeight.w700),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 14),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(999),
                      child: LinearProgressIndicator(
                        value: _downloadProgress,
                        minHeight: 9,
                        backgroundColor: const Color(0xFFE2E8F0),
                      ),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      _isDownloaded
                          ? 'Data ujian tersimpan lokal. Jawaban dan pelanggaran akan tetap tersimpan saat koneksi tidak stabil.'
                          : 'Download wajib dilakukan sebelum ujian dimulai agar pengerjaan tetap aman saat koneksi tidak stabil.',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: const Color(0xFF64748B),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              FilledButton.icon(
                onPressed:
                    _isCheckingLocalPackage || _isDownloading || _isStarting
                    ? null
                    : (_isDownloaded ? _startExam : _downloadExamPackage),
                icon: _isDownloading || _isStarting
                    ? const SizedBox(
                        width: 18,
                        height: 18,
                        child: CircularProgressIndicator(
                          strokeWidth: 2.2,
                          color: Colors.white,
                        ),
                      )
                    : Icon(
                        _isDownloaded
                            ? Icons.play_arrow_outlined
                            : Icons.download_for_offline_outlined,
                      ),
                label: Text(
                  _isCheckingLocalPackage
                      ? 'Memeriksa Data Lokal...'
                      : _isDownloading
                      ? 'Mendownload...'
                      : _isStarting
                      ? 'Memulai Session...'
                      : _isDownloaded
                      ? 'Mulai Ujian'
                      : 'Download Ujian',
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _InfoTile extends StatelessWidget {
  const _InfoTile({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFFE2E8F0)),
      ),
      child: Column(
        children: [
          Text(
            label,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: Theme.of(context).textTheme.labelSmall?.copyWith(
              color: const Color(0xFF64748B),
              fontWeight: FontWeight.w700,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            value,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: Theme.of(
              context,
            ).textTheme.bodyMedium?.copyWith(fontWeight: FontWeight.w800),
          ),
        ],
      ),
    );
  }
}
