import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class TokenStorage {
  const TokenStorage({
    FlutterSecureStorage secureStorage = const FlutterSecureStorage(),
  }) : _secureStorage = secureStorage;

  static const _tokenKey = 'auth_token';
  static const _userKey = 'auth_user';

  final FlutterSecureStorage _secureStorage;

  Future<void> saveToken(String token) {
    return _secureStorage.write(key: _tokenKey, value: token);
  }

  Future<String?> getToken() {
    return _secureStorage.read(key: _tokenKey);
  }

  Future<void> saveUserJson(String userJson) {
    return _secureStorage.write(key: _userKey, value: userJson);
  }

  Future<String?> getUserJson() {
    return _secureStorage.read(key: _userKey);
  }

  Future<Map<String, dynamic>?> getUserMap() async {
    final userJson = await getUserJson();
    if (userJson == null || userJson.isEmpty) return null;

    final decoded = jsonDecode(userJson);
    if (decoded is Map<String, dynamic>) return decoded;
    if (decoded is Map) return Map<String, dynamic>.from(decoded);
    return null;
  }

  Future<void> clear() async {
    await Future.wait([
      _secureStorage.delete(key: _tokenKey),
      _secureStorage.delete(key: _userKey),
    ]);
  }
}
