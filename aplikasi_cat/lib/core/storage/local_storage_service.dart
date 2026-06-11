import 'dart:convert';

import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';

import 'local_storage_keys.dart';

class LocalStorageService {
  const LocalStorageService._();

  static const _databaseName = 'cat_offline_exam.db';
  static const _databaseVersion = 1;

  static Database? _database;

  static Future<void> init() async {
    _database ??= await _openDatabase();
  }

  static Future<Database> get database async {
    if (_database != null) return _database!;
    await init();
    return _database!;
  }

  static Future<Database> _openDatabase() async {
    final databasePath = await getDatabasesPath();
    final path = p.join(databasePath, _databaseName);

    return openDatabase(
      path,
      version: _databaseVersion,
      onCreate: (db, version) async {
        await _createTables(db);
      },
    );
  }

  static Future<void> _createTables(Database db) async {
    await db.execute('''
      CREATE TABLE ${LocalStorageKeys.examPackagesTable} (
        key TEXT PRIMARY KEY,
        id_ujian TEXT NOT NULL,
        id_peserta TEXT NOT NULL,
        payload TEXT NOT NULL,
        updated_at TEXT NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE ${LocalStorageKeys.answerDraftsTable} (
        key TEXT PRIMARY KEY,
        id_ujian TEXT NOT NULL,
        id_peserta TEXT NOT NULL,
        payload TEXT NOT NULL,
        updated_at TEXT NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE ${LocalStorageKeys.violationLogsTable} (
        key TEXT PRIMARY KEY,
        id_ujian TEXT,
        id_peserta TEXT,
        payload TEXT NOT NULL,
        updated_at TEXT NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE ${LocalStorageKeys.finalSubmissionsTable} (
        key TEXT PRIMARY KEY,
        id_ujian TEXT NOT NULL,
        id_peserta TEXT NOT NULL,
        payload TEXT NOT NULL,
        updated_at TEXT NOT NULL
      )
    ''');
  }

  static Future<void> upsertMap({
    required String table,
    required String key,
    required Map<String, dynamic> value,
    String? idUjian,
    String? idPeserta,
  }) async {
    final db = await database;
    await db.insert(table, {
      'key': key,
      if (idUjian != null) 'id_ujian': idUjian,
      if (idPeserta != null) 'id_peserta': idPeserta,
      'payload': jsonEncode(value),
      'updated_at': DateTime.now().toIso8601String(),
    }, conflictAlgorithm: ConflictAlgorithm.replace);
  }

  static Future<Map<String, dynamic>?> readMap({
    required String table,
    required String key,
  }) async {
    final db = await database;
    final rows = await db.query(
      table,
      where: 'key = ?',
      whereArgs: [key],
      limit: 1,
    );

    if (rows.isEmpty) return null;
    return _decodePayload(rows.first['payload']);
  }

  static Future<List<Map<String, dynamic>>> readAllMaps({
    required String table,
    String? idUjian,
    String? idPeserta,
  }) async {
    final db = await database;
    final whereParts = <String>[];
    final whereArgs = <Object?>[];

    if (idUjian != null) {
      whereParts.add('id_ujian = ?');
      whereArgs.add(idUjian);
    }

    if (idPeserta != null) {
      whereParts.add('id_peserta = ?');
      whereArgs.add(idPeserta);
    }

    final rows = await db.query(
      table,
      where: whereParts.isEmpty ? null : whereParts.join(' AND '),
      whereArgs: whereArgs.isEmpty ? null : whereArgs,
      orderBy: 'updated_at ASC',
    );

    return rows
        .map((row) => _decodePayload(row['payload']))
        .whereType<Map<String, dynamic>>()
        .toList();
  }

  static Future<void> deleteByKey({
    required String table,
    required String key,
  }) async {
    final db = await database;
    await db.delete(table, where: 'key = ?', whereArgs: [key]);
  }

  static Future<void> deleteByExamParticipant({
    required String table,
    required String idUjian,
    required String idPeserta,
  }) async {
    final db = await database;
    await db.delete(
      table,
      where: 'id_ujian = ? AND id_peserta = ?',
      whereArgs: [idUjian, idPeserta],
    );
  }

  static Map<String, dynamic>? _decodePayload(Object? payload) {
    if (payload == null) return null;
    final decoded = jsonDecode(payload.toString());
    if (decoded is Map<String, dynamic>) return decoded;
    if (decoded is Map) return Map<String, dynamic>.from(decoded);
    return null;
  }
}
