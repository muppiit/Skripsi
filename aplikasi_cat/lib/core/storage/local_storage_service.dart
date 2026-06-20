import 'dart:convert';
import 'dart:math';

import 'package:cryptography/cryptography.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';

import 'local_storage_keys.dart';

class LocalStorageService {
  const LocalStorageService._();

  static const _databaseName = 'cat_offline_exam.db';
  static const _databaseVersion = 1;
  static const _encryptionVersion = 1;
  static const _encryptionKeyName = 'cat_offline_exam_key_v1';
  static const _secureStorage = FlutterSecureStorage();
  static final _algorithm = AesGcm.with256bits();

  static Database? _database;
  static SecretKey? _secretKey;

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
    final encryptedPayload = await _encodePayload(value);
    await db.insert(table, {
      'key': key,
      if (idUjian != null) 'id_ujian': idUjian,
      if (idPeserta != null) 'id_peserta': idPeserta,
      'payload': encryptedPayload,
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

    final decodedRows = <Map<String, dynamic>>[];
    for (final row in rows) {
      final decoded = await _decodePayload(row['payload']);
      if (decoded != null) decodedRows.add(decoded);
    }

    return decodedRows;
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

  static Future<String> _encodePayload(Map<String, dynamic> payload) async {
    final secretKey = await _getSecretKey();
    final nonce = _createNonce();
    final plaintext = utf8.encode(jsonEncode(payload));
    final secretBox = await _algorithm.encrypt(
      plaintext,
      secretKey: secretKey,
      nonce: nonce,
    );

    return jsonEncode({
      'encrypted': true,
      'encryptionVersion': _encryptionVersion,
      'algorithm': 'AES-GCM',
      'nonce': base64Encode(secretBox.nonce),
      'cipherText': base64Encode(secretBox.cipherText),
      'mac': base64Encode(secretBox.mac.bytes),
    });
  }

  static Future<Map<String, dynamic>?> _decodePayload(Object? payload) async {
    if (payload == null) return null;
    final decoded = jsonDecode(payload.toString());
    if (decoded is Map && decoded['encrypted'] == true) {
      final secretKey = await _getSecretKey();
      final secretBox = SecretBox(
        base64Decode(decoded['cipherText'].toString()),
        nonce: base64Decode(decoded['nonce'].toString()),
        mac: Mac(base64Decode(decoded['mac'].toString())),
      );
      final decrypted = await _algorithm.decrypt(
        secretBox,
        secretKey: secretKey,
      );
      final decryptedJson = jsonDecode(utf8.decode(decrypted));
      if (decryptedJson is Map<String, dynamic>) return decryptedJson;
      if (decryptedJson is Map) return Map<String, dynamic>.from(decryptedJson);
      return null;
    }

    if (decoded is Map<String, dynamic>) return decoded;
    if (decoded is Map) return Map<String, dynamic>.from(decoded);
    return null;
  }

  static Future<SecretKey> _getSecretKey() async {
    if (_secretKey != null) return _secretKey!;

    var encodedKey = await _secureStorage.read(key: _encryptionKeyName);
    if (encodedKey == null || encodedKey.isEmpty) {
      final keyBytes = List<int>.generate(
        32,
        (_) => Random.secure().nextInt(256),
      );
      encodedKey = base64Encode(keyBytes);
      await _secureStorage.write(key: _encryptionKeyName, value: encodedKey);
    }

    _secretKey = SecretKey(base64Decode(encodedKey));
    return _secretKey!;
  }

  static List<int> _createNonce() {
    return List<int>.generate(12, (_) => Random.secure().nextInt(256));
  }
}
