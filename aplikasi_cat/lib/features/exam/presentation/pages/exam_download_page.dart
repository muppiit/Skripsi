import 'package:flutter/material.dart';

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

    if (!mounted) return;
    setState(() {
      _isDownloaded = package != null;
      _downloadProgress = package != null ? 1 : 0;
      _downloadStatus = package != null
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
      _downloadStatus = 'Menyiapkan metadata ujian...';
    });

    try {
      await Future<void>.delayed(const Duration(milliseconds: 250));
      if (!mounted) return;
      setState(() {
        _downloadProgress = 0.35;
        _downloadStatus = 'Menyiapkan daftar soal...';
      });

      final sanitizedUjian = _sanitizeUjianForOffline(_ujian);
      final soalList = _extractSoalList(sanitizedUjian);

      await Future<void>.delayed(const Duration(milliseconds: 250));
      if (!mounted) return;
      setState(() {
        _downloadProgress = 0.65;
        _downloadStatus = 'Menghapus kunci jawaban dari paket lokal...';
      });

      final package = {
        'id': '$_idUjian:$_idPeserta',
        'idUjian': _idUjian,
        'idPeserta': _idPeserta,
        'kodeUjian': _kodeUjian,
        'ujian': sanitizedUjian,
        'soalList': soalList,
        'metadata': {
          'source': 'flutter_mobile',
          'downloadedAt': DateTime.now().toIso8601String(),
          'storage': 'sqlite',
          'totalQuestions': soalList.length,
        },
      };

      await _localDatasource.saveExamPackage(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
        package: package,
      );

      if (!mounted) return;
      setState(() {
        _downloadProgress = 1;
        _downloadStatus = 'Download selesai. Ujian siap dimulai.';
        _isDownloaded = true;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Paket ujian berhasil disimpan ke SQLite'),
          behavior: SnackBarBehavior.floating,
        ),
      );
    } catch (_) {
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
    final tipeSoal = _ujian['tipeSoal']?.toString() ?? '-';
    final statusUjian = _ujian['statusUjian']?.toString() ?? '-';

    return PageShell(
      title: 'Informasi Ujian',
      subtitle: 'Download paket ujian untuk mode offline',
      showBackButton: true,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CircleAvatar(
                    radius: 28,
                    backgroundColor: Theme.of(
                      context,
                    ).colorScheme.primary.withValues(alpha: 0.12),
                    child: Icon(
                      Icons.assignment_outlined,
                      color: Theme.of(context).colorScheme.primary,
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
                              ?.copyWith(fontWeight: FontWeight.w700),
                        ),
                        const SizedBox(height: 4),
                        Text('Kode: ${_kodeUjian ?? '-'}'),
                        if (deskripsi != null && deskripsi.isNotEmpty) ...[
                          const SizedBox(height: 8),
                          Text(
                            deskripsi,
                            style: Theme.of(context).textTheme.bodyMedium,
                          ),
                        ],
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: [
                  _InfoChip(
                    icon: Icons.timer_outlined,
                    label: '$durasiMenit menit',
                  ),
                  _InfoChip(
                    icon: Icons.quiz_outlined,
                    label: '$jumlahSoal soal',
                  ),
                  _InfoChip(icon: Icons.shuffle_outlined, label: tipeSoal),
                  _InfoChip(icon: Icons.circle_outlined, label: statusUjian),
                ],
              ),
              const Divider(height: 32),
              Text(
                _downloadStatus,
                style: Theme.of(
                  context,
                ).textTheme.bodyMedium?.copyWith(fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 10),
              LinearProgressIndicator(
                value: _downloadProgress,
                borderRadius: BorderRadius.circular(999),
              ),
              const SizedBox(height: 10),
              Text(
                _isDownloaded
                    ? 'Data ujian tersimpan lokal. Jawaban dan pelanggaran nanti juga akan disimpan ke SQLite.'
                    : 'Download wajib dilakukan sebelum ujian dimulai agar pengerjaan tetap aman saat koneksi tidak stabil.',
                style: Theme.of(context).textTheme.bodySmall,
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

class _InfoChip extends StatelessWidget {
  const _InfoChip({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Chip(
      avatar: Icon(icon, size: 18),
      label: Text(label),
      backgroundColor: const Color(0xFFF1F5F9),
      side: const BorderSide(color: Color(0xFFE2E8F0)),
    );
  }
}
