import 'package:flutter/material.dart';

import '../../../../core/network/api_exception.dart';
import '../../../../core/routes/app_routes.dart';
import '../../../../core/storage/token_storage.dart';
import '../../../../shared/widgets/page_shell.dart';
import '../../data/datasources/exam_remote_datasource.dart';

class ExamCodePage extends StatefulWidget {
  const ExamCodePage({super.key});

  @override
  State<ExamCodePage> createState() => _ExamCodePageState();
}

class _ExamCodePageState extends State<ExamCodePage> {
  final _formKey = GlobalKey<FormState>();
  final _codeController = TextEditingController();
  final _examRemoteDatasource = ExamRemoteDatasource();
  final _tokenStorage = const TokenStorage();

  bool _isLoading = false;

  @override
  void dispose() {
    _codeController.dispose();
    super.dispose();
  }

  Future<void> _handleSubmitCode() async {
    final isValid = _formKey.currentState?.validate() ?? false;
    if (!isValid || _isLoading) return;

    FocusScope.of(context).unfocus();
    setState(() => _isLoading = true);

    try {
      final user = await _tokenStorage.getUserMap();
      final idPeserta = user?['id']?.toString();
      if (idPeserta == null || idPeserta.isEmpty) {
        _showError('Data login tidak ditemukan. Silakan login ulang.');
        return;
      }

      final kodeUjian = _codeController.text.trim();
      final ujianResponse = await _examRemoteDatasource.getAllUjian();
      final ujian = _findUjianByCode(ujianResponse, kodeUjian);

      if (ujian == null) {
        _showError('Kode ujian tidak ditemukan.');
        return;
      }

      final statusUjian = ujian['statusUjian']?.toString().toUpperCase();
      final isLive = ujian['isLive'] == true;
      final isAllowedStatus =
          statusUjian == 'AKTIF' || statusUjian == 'BERLANGSUNG';
      if (!isAllowedStatus || !isLive) {
        _showError(
          'Ujian belum tersedia. Status: ${statusUjian ?? '-'}, live: ${isLive ? 'Ya' : 'Tidak'}.',
        );
        return;
      }

      final idUjian = ujian['idUjian']?.toString();
      if (idUjian == null || idUjian.isEmpty) {
        _showError('Data ujian tidak valid.');
        return;
      }

      final validationResponse = await _examRemoteDatasource.validateCanStart(
        idUjian: idUjian,
        idPeserta: idPeserta,
      );
      final validation = _extractMap(validationResponse['content']);
      final canStart = validation?['canStart'] == true;

      if (!canStart) {
        _showError(
          validation?['reason']?.toString() ??
              'Peserta belum dapat memulai ujian.',
        );
        return;
      }

      if (!mounted) return;
      Navigator.pushNamed(
        context,
        AppRoutes.examDownload,
        arguments: {
          'ujian': ujian,
          'idUjian': idUjian,
          'idPeserta': idPeserta,
          'kodeUjian': kodeUjian,
          'user': user,
        },
      );
    } on ApiException catch (error) {
      if (!mounted) return;
      _showError(error.message);
    } catch (_) {
      if (!mounted) return;
      _showError('Gagal memeriksa kode ujian. Periksa koneksi backend.');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Map<String, dynamic>? _findUjianByCode(
    Map<String, dynamic> response,
    String kodeUjian,
  ) {
    final content = response['content'];
    if (content is! List) return null;

    for (final item in content) {
      final ujian = _extractMap(item);
      final pengaturan = _extractMap(ujian?['pengaturan']);
      final itemCode = pengaturan?['kodeUjian']?.toString();

      if (itemCode != null &&
          itemCode.toUpperCase() == kodeUjian.toUpperCase()) {
        return ujian;
      }
    }

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
    return PageShell(
      title: 'Kode Ujian',
      subtitle: 'Masukkan kode dari dosen/operator',
      showBackButton: true,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Container(
                  padding: const EdgeInsets.all(18),
                  decoration: BoxDecoration(
                    color: const Color(0xFFEFF6FF),
                    borderRadius: BorderRadius.circular(24),
                    border: Border.all(color: const Color(0xFFBFDBFE)),
                  ),
                  child: Icon(
                    Icons.qr_code_2_outlined,
                    size: 58,
                    color: Theme.of(context).colorScheme.primary,
                  ),
                ),
                const SizedBox(height: 22),
                Text(
                  'Masukkan Kode Ujian',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    fontWeight: FontWeight.w800,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Aplikasi akan memeriksa status ujian, peserta, dan kesiapan sesi sebelum download paket soal.',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: const Color(0xFF64748B),
                  ),
                ),
                const SizedBox(height: 20),
                Container(
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(
                    color: const Color(0xFFF8FAFC),
                    borderRadius: BorderRadius.circular(18),
                    border: Border.all(color: const Color(0xFFE2E8F0)),
                  ),
                  child: const Row(
                    children: [
                      Icon(Icons.verified_user_outlined, size: 20),
                      SizedBox(width: 10),
                      Expanded(
                        child: Text(
                          'Gunakan kode ujian yang diberikan dosen/operator.',
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 24),
                TextFormField(
                  controller: _codeController,
                  enabled: !_isLoading,
                  textCapitalization: TextCapitalization.characters,
                  textInputAction: TextInputAction.done,
                  onFieldSubmitted: (_) => _handleSubmitCode(),
                  decoration: const InputDecoration(
                    labelText: 'Kode Ujian',
                    hintText: 'Contoh: 12345',
                    prefixIcon: Icon(Icons.key_outlined),
                  ),
                  validator: (value) {
                    if (value == null || value.trim().isEmpty) {
                      return 'Kode ujian wajib diisi';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 20),
                FilledButton.icon(
                  onPressed: _isLoading ? null : _handleSubmitCode,
                  icon: _isLoading
                      ? const SizedBox(
                          width: 18,
                          height: 18,
                          child: CircularProgressIndicator(
                            strokeWidth: 2.2,
                            color: Colors.white,
                          ),
                        )
                      : const Icon(Icons.login_outlined),
                  label: Text(_isLoading ? 'Memeriksa...' : 'Lanjutkan'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
