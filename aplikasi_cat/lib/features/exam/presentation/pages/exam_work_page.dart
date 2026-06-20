import 'dart:async';

import 'package:flutter/material.dart';

import '../../../../core/routes/app_routes.dart';
import '../../data/datasources/exam_local_datasource.dart';

class ExamWorkPage extends StatefulWidget {
  const ExamWorkPage({super.key});

  @override
  State<ExamWorkPage> createState() => _ExamWorkPageState();
}

class _ExamWorkPageState extends State<ExamWorkPage>
    with WidgetsBindingObserver {
  final _localDatasource = ExamLocalDatasource();

  Map<String, dynamic> _routeData = {};
  Map<String, dynamic> _package = {};
  Map<String, dynamic> _ujian = {};
  List<Map<String, dynamic>> _soalList = [];
  Map<String, dynamic> _answers = {};

  String? _idUjian;
  String? _idPeserta;
  String? _sessionId;

  bool _isLoading = true;
  bool _isSaving = false;
  bool _isSubmitted = false;
  int _currentIndex = 0;
  int _violationCount = 0;
  int _timeRemaining = 0;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_routeData.isNotEmpty) return;

    final args = ModalRoute.of(context)?.settings.arguments;
    _routeData = args is Map ? Map<String, dynamic>.from(args) : {};
    _idUjian = _routeData['idUjian']?.toString();
    _idPeserta = _routeData['idPeserta']?.toString();
    _loadOfflineExam();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (!_isLoading && !_isSubmitted && state == AppLifecycleState.paused) {
      _recordBackgroundViolation();
    }
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _loadOfflineExam() async {
    if (_idUjian == null || _idPeserta == null) {
      setState(() => _isLoading = false);
      return;
    }

    final package = await _localDatasource.getExamPackage(
      idUjian: _idUjian!,
      idPeserta: _idPeserta!,
    );
    final draft = await _localDatasource.getAnswerDraft(
      idUjian: _idUjian!,
      idPeserta: _idPeserta!,
    );

    if (!mounted) return;

    final ujian = _extractMap(package?['ujian']) ?? {};
    final soalList = _extractSoalList(package?['soalList']);
    final draftAnswers = _extractMap(draft?['answers']) ?? {};
    final draftIndex = int.tryParse('${draft?['currentSoalIndex'] ?? 0}') ?? 0;
    final durationMinutes = int.tryParse('${ujian['durasiMenit'] ?? 0}') ?? 0;
    final draftTimeRemaining = int.tryParse('${draft?['timeRemaining'] ?? 0}');

    setState(() {
      _package = package ?? {};
      _ujian = ujian;
      _soalList = soalList;
      _answers = draftAnswers;
      _currentIndex = draftIndex.clamp(
        0,
        soalList.isEmpty ? 0 : soalList.length - 1,
      );
      _timeRemaining = draftTimeRemaining != null && draftTimeRemaining > 0
          ? draftTimeRemaining
          : durationMinutes * 60;
      _sessionId =
          package?['sessionId']?.toString() ??
          _routeData['sessionId']?.toString();
      _isLoading = false;
    });

    _startTimer();
  }

  void _startTimer() {
    _timer?.cancel();
    if (_timeRemaining <= 0) return;

    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (!mounted || _isSubmitted) {
        timer.cancel();
        return;
      }

      if (_timeRemaining <= 1) {
        timer.cancel();
        _timeRemaining = 0;
        _submitExam(isAutoSubmit: true);
        return;
      }

      setState(() => _timeRemaining--);
    });
  }

  Future<void> _saveDraft() async {
    if (_idUjian == null || _idPeserta == null) return;

    setState(() => _isSaving = true);
    try {
      await _localDatasource.saveAnswerDraft(
        idUjian: _idUjian!,
        idPeserta: _idPeserta!,
        answers: _answers,
        currentSoalIndex: _currentIndex,
        timeRemaining: _timeRemaining,
      );
    } finally {
      if (mounted) setState(() => _isSaving = false);
    }
  }

  Future<void> _setAnswer(String questionId, dynamic answer) async {
    setState(() {
      if (answer == null ||
          (answer is String && answer.trim().isEmpty) ||
          (answer is List && answer.isEmpty) ||
          (answer is Map && answer.isEmpty)) {
        _answers.remove(questionId);
      } else {
        _answers[questionId] = answer;
      }
    });

    await _saveDraft();
  }

  Future<void> _recordBackgroundViolation() async {
    if (_idUjian == null || _idPeserta == null) return;

    final now = DateTime.now().toIso8601String();
    final violation = {
      'id': 'violation:$_idUjian:$_idPeserta:APP_BACKGROUND:$now',
      'idUjian': _idUjian,
      'idPeserta': _idPeserta,
      'sessionId': _sessionId,
      'typeViolation': 'APP_BACKGROUND',
      'severity': 'MEDIUM',
      'detectedAt': now,
      'details': 'Aplikasi masuk background saat ujian berlangsung',
      'evidence': {
        'currentSoalIndex': _currentIndex,
        'timeRemaining': _timeRemaining,
      },
    };

    await _localDatasource.addViolationLog(violation);

    if (!mounted) return;
    setState(() => _violationCount++);

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Pelanggaran tercatat: aplikasi keluar dari layar ujian'),
        behavior: SnackBarBehavior.floating,
      ),
    );
  }

  Future<void> _submitExam({bool isAutoSubmit = false}) async {
    if (_idUjian == null || _idPeserta == null || _isSubmitted) return;

    setState(() => _isSubmitted = true);
    _timer?.cancel();
    await _saveDraft();

    final violations = await _localDatasource.getViolationLogs(
      idUjian: _idUjian,
      idPeserta: _idPeserta,
    );

    await _localDatasource.saveFinalSubmission(
      idUjian: _idUjian!,
      idPeserta: _idPeserta!,
      submission: {
        'idUjian': _idUjian,
        'idPeserta': _idPeserta,
        'sessionId': _sessionId,
        'answers': _answers,
        'isAutoSubmit': isAutoSubmit,
        'finalTimeRemaining': _timeRemaining,
        'submittedAt': DateTime.now().toIso8601String(),
        'metadata': {
          'source': 'flutter_mobile',
          'offlineMode': true,
          'currentSoalIndex': _currentIndex,
          'totalQuestions': _soalList.length,
        },
        'violations': violations,
      },
    );

    if (!mounted) return;
    Navigator.pushReplacementNamed(
      context,
      AppRoutes.examResult,
      arguments: {
        ..._routeData,
        'answers': _answers,
        'violations': violations,
        'finalTimeRemaining': _timeRemaining,
      },
    );
  }

  void _goToQuestion(int index) {
    setState(() => _currentIndex = index);
    _saveDraft();
  }

  Map<String, dynamic>? _extractMap(dynamic value) {
    if (value is Map<String, dynamic>) return value;
    if (value is Map) return Map<String, dynamic>.from(value);
    return null;
  }

  List<Map<String, dynamic>> _extractSoalList(dynamic value) {
    if (value is! List) return [];
    return value.map(_extractMap).whereType<Map<String, dynamic>>().toList();
  }

  String _formatTime(int seconds) {
    final minutes = seconds ~/ 60;
    final second = seconds % 60;
    return '${minutes.toString().padLeft(2, '0')}:${second.toString().padLeft(2, '0')}';
  }

  String _questionId(Map<String, dynamic> soal) {
    return soal['idBankSoal']?.toString() ??
        soal['id']?.toString() ??
        'question_$_currentIndex';
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (_package.isEmpty || _soalList.isEmpty) {
      return Scaffold(
        appBar: AppBar(title: const Text('Pengerjaan Ujian')),
        body: const Center(
          child: Padding(
            padding: EdgeInsets.all(24),
            child: Text(
              'Paket ujian lokal tidak ditemukan atau tidak berisi soal.',
            ),
          ),
        ),
      );
    }

    final currentSoal = _soalList[_currentIndex];
    final questionId = _questionId(currentSoal);
    final answeredCount = _answers.length;

    return PopScope(
      canPop: false,
      child: Scaffold(
        backgroundColor: const Color(0xFFF8FAFC),
        appBar: AppBar(
          automaticallyImplyLeading: false,
          backgroundColor: Colors.white,
          title: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                _ujian['namaUjian']?.toString() ?? 'Pengerjaan Ujian',
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(fontWeight: FontWeight.w800),
              ),
              Text(
                'Soal ${_currentIndex + 1}/${_soalList.length}',
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ),
          actions: [
            Padding(
              padding: const EdgeInsets.only(right: 12),
              child: Center(
                child: Chip(
                  avatar: const Icon(Icons.timer_outlined, size: 18),
                  label: Text(_formatTime(_timeRemaining)),
                  backgroundColor: const Color(0xFFEEF2FF),
                  side: const BorderSide(color: Color(0xFFC7D2FE)),
                ),
              ),
            ),
          ],
        ),
        body: SafeArea(
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 14, 16, 10),
                child: Row(
                  children: [
                    Expanded(
                      child: _StatusTile(
                        label: 'Dijawab',
                        value: '$answeredCount/${_soalList.length}',
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: _StatusTile(
                        label: 'Pelanggaran',
                        value: '$_violationCount',
                        danger: _violationCount > 0,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: AnimatedSwitcher(
                        duration: const Duration(milliseconds: 200),
                        child: _isSaving
                            ? const _StatusTile(
                                key: ValueKey('saving'),
                                label: 'Status',
                                value: 'Simpan',
                              )
                            : const _StatusTile(
                                key: ValueKey('saved'),
                                label: 'Status',
                                value: 'Aman',
                                success: true,
                              ),
                      ),
                    ),
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 0, 16, 10),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(999),
                  child: LinearProgressIndicator(
                    value: (_currentIndex + 1) / _soalList.length,
                    minHeight: 8,
                    backgroundColor: const Color(0xFFE2E8F0),
                  ),
                ),
              ),
              SizedBox(
                height: 56,
                child: ListView.separated(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  scrollDirection: Axis.horizontal,
                  itemCount: _soalList.length,
                  separatorBuilder: (_, _) => const SizedBox(width: 8),
                  itemBuilder: (context, index) {
                    final soal = _soalList[index];
                    final id =
                        soal['idBankSoal']?.toString() ?? 'question_$index';
                    final isAnswered = _answers.containsKey(id);
                    final isActive = index == _currentIndex;

                    return ChoiceChip(
                      selected: isActive,
                      label: Text(
                        isAnswered ? '✓ ${index + 1}' : '${index + 1}',
                      ),
                      onSelected: (_) => _goToQuestion(index),
                    );
                  },
                ),
              ),
              Expanded(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(22),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Container(
                                width: 44,
                                height: 44,
                                decoration: BoxDecoration(
                                  color: const Color(0xFFDBEAFE),
                                  borderRadius: BorderRadius.circular(16),
                                ),
                                child: Center(
                                  child: Text(
                                    '${_currentIndex + 1}',
                                    style: const TextStyle(
                                      color: Color(0xFF1D4ED8),
                                      fontWeight: FontWeight.w800,
                                    ),
                                  ),
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      'Pertanyaan',
                                      style: Theme.of(context)
                                          .textTheme
                                          .labelLarge
                                          ?.copyWith(
                                            color: const Color(0xFF64748B),
                                            fontWeight: FontWeight.w700,
                                          ),
                                    ),
                                    Text(
                                      'Jawab dengan teliti sebelum lanjut',
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodySmall
                                          ?.copyWith(
                                            color: const Color(0xFF64748B),
                                          ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          const Divider(height: 28),
                          Text(
                            currentSoal['pertanyaan']?.toString() ??
                                'Pertanyaan tidak tersedia',
                            style: Theme.of(context).textTheme.titleMedium
                                ?.copyWith(
                                  fontWeight: FontWeight.w800,
                                  height: 1.35,
                                ),
                          ),
                          const SizedBox(height: 22),
                          _buildAnswerInput(currentSoal, questionId),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
                child: Row(
                  children: [
                    Expanded(
                      child: OutlinedButton.icon(
                        onPressed: _currentIndex == 0
                            ? null
                            : () => _goToQuestion(_currentIndex - 1),
                        icon: const Icon(Icons.chevron_left),
                        label: const Text('Sebelumnya'),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: FilledButton.icon(
                        onPressed: _currentIndex == _soalList.length - 1
                            ? () => _confirmSubmit()
                            : () => _goToQuestion(_currentIndex + 1),
                        icon: Icon(
                          _currentIndex == _soalList.length - 1
                              ? Icons.send_outlined
                              : Icons.chevron_right,
                        ),
                        label: Text(
                          _currentIndex == _soalList.length - 1
                              ? 'Kumpulkan'
                              : 'Berikutnya',
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAnswerInput(Map<String, dynamic> soal, String questionId) {
    final jenis = soal['jenisSoal']?.toString().toUpperCase();

    switch (jenis) {
      case 'MULTI':
        return _buildMultiChoice(soal, questionId);
      case 'ISIAN':
        return _buildTextAnswer(questionId);
      case 'COCOK':
        return _buildMatchingAnswer(soal, questionId);
      case 'PG':
      default:
        return _buildSingleChoice(soal, questionId);
    }
  }

  Widget _buildSingleChoice(Map<String, dynamic> soal, String questionId) {
    final opsi = _extractMap(soal['opsi']) ?? {};
    final selected = _answers[questionId]?.toString();

    if (opsi.isEmpty) return const Text('Opsi jawaban tidak tersedia.');

    return Column(
      children: opsi.entries.map<Widget>((entry) {
        final key = entry.key.toString();
        final isSelected = selected == key;
        return Padding(
          padding: const EdgeInsets.only(bottom: 10),
          child: DecoratedBox(
            decoration: _answerDecoration(isSelected),
            child: RadioListTile<String>(
              value: key,
              groupValue: selected,
              onChanged: (value) => _setAnswer(questionId, value),
              title: Text(
                '$key. ${entry.value}',
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(18),
              ),
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildMultiChoice(Map<String, dynamic> soal, String questionId) {
    final opsi = _extractMap(soal['opsi']) ?? {};
    final selected = (_answers[questionId] is List)
        ? List<String>.from(_answers[questionId])
        : <String>[];

    if (opsi.isEmpty) return const Text('Opsi jawaban tidak tersedia.');

    return Column(
      children: opsi.entries.map((entry) {
        final key = entry.key.toString();
        final checked = selected.contains(key);

        return Padding(
          padding: const EdgeInsets.only(bottom: 10),
          child: DecoratedBox(
            decoration: _answerDecoration(checked),
            child: CheckboxListTile(
              value: checked,
              title: Text(
                '$key. ${entry.value}',
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(18),
              ),
              onChanged: (value) {
                final next = [...selected];
                if (value == true && !next.contains(key)) {
                  next.add(key);
                } else {
                  next.remove(key);
                }
                _setAnswer(questionId, next);
              },
            ),
          ),
        );
      }).toList(),
    );
  }

  BoxDecoration _answerDecoration(bool selected) {
    return BoxDecoration(
      color: selected ? const Color(0xFFEFF6FF) : const Color(0xFFF8FAFC),
      borderRadius: BorderRadius.circular(18),
      border: Border.all(
        color: selected ? const Color(0xFF60A5FA) : const Color(0xFFE2E8F0),
        width: selected ? 1.4 : 1,
      ),
    );
  }

  Widget _buildTextAnswer(String questionId) {
    return TextFormField(
      key: ValueKey(questionId),
      initialValue: _answers[questionId]?.toString(),
      minLines: 3,
      maxLines: 5,
      decoration: const InputDecoration(
        labelText: 'Jawaban',
        hintText: 'Tulis jawaban Anda di sini',
      ),
      onChanged: (value) => _setAnswer(questionId, value),
    );
  }

  Widget _buildMatchingAnswer(Map<String, dynamic> soal, String questionId) {
    final pasangan = _extractMap(soal['pasangan']) ?? {};
    final currentAnswer = _extractMap(_answers[questionId]) ?? {};

    if (pasangan.isEmpty) {
      return _buildTextAnswer(questionId);
    }

    return Column(
      children: pasangan.keys.map((key) {
        return Padding(
          padding: const EdgeInsets.only(bottom: 12),
          child: TextFormField(
            key: ValueKey('$questionId-$key'),
            initialValue: currentAnswer[key]?.toString(),
            decoration: InputDecoration(
              labelText: key.toString(),
              hintText: 'Masukkan pasangan jawaban',
            ),
            onChanged: (value) {
              final next = {...currentAnswer, key.toString(): value};
              next.removeWhere((_, item) => item.toString().trim().isEmpty);
              _setAnswer(questionId, next);
            },
          ),
        );
      }).toList(),
    );
  }

  void _confirmSubmit() {
    showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return AlertDialog(
          title: const Text('Kumpulkan Ujian?'),
          content: Text(
            'Jawaban tersimpan: ${_answers.length}/${_soalList.length}. Setelah dikumpulkan, data akhir akan disimpan lokal untuk proses upload.',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Batal'),
            ),
            FilledButton(
              onPressed: () {
                Navigator.pop(context);
                _submitExam();
              },
              child: const Text('Kumpulkan'),
            ),
          ],
        );
      },
    );
  }
}

class _StatusTile extends StatelessWidget {
  const _StatusTile({
    super.key,
    required this.label,
    required this.value,
    this.success = false,
    this.danger = false,
  });

  final String label;
  final String value;
  final bool success;
  final bool danger;

  @override
  Widget build(BuildContext context) {
    final valueColor = danger
        ? const Color(0xFFDC2626)
        : success
        ? const Color(0xFF16A34A)
        : const Color(0xFF1D4ED8);

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
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
          const SizedBox(height: 3),
          Text(
            value,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: valueColor,
              fontWeight: FontWeight.w800,
            ),
          ),
        ],
      ),
    );
  }
}
