import 'dart:convert';

import '../../../../core/network/api_client.dart';
import '../../../../core/network/api_endpoints.dart';
import '../../../../core/storage/token_storage.dart';

class AuthRemoteDatasource {
  AuthRemoteDatasource({ApiClient? apiClient, TokenStorage? tokenStorage})
    : _apiClient = apiClient ?? ApiClient(),
      _tokenStorage = tokenStorage ?? const TokenStorage();

  final ApiClient _apiClient;
  final TokenStorage _tokenStorage;

  Future<Map<String, dynamic>> signIn({
    required String usernameOrEmail,
    required String password,
  }) async {
    final response = await _apiClient.post(
      ApiEndpoints.authSignin,
      data: {'usernameOrEmail': usernameOrEmail, 'password': password},
    );

    final token = _extractToken(response);
    if (token != null && token.isNotEmpty) {
      await _tokenStorage.saveToken(token);
    }

    final user = _extractUser(response);
    if (user != null) {
      await _tokenStorage.saveUserJson(jsonEncode(user));
    }

    return response;
  }

  Future<void> signOut() {
    return _tokenStorage.clear();
  }

  String? _extractToken(Map<String, dynamic> response) {
    final candidates = [
      response['token'],
      response['accessToken'],
      response['data'] is Map ? response['data']['token'] : null,
      response['data'] is Map ? response['data']['accessToken'] : null,
      response['content'] is Map ? response['content']['token'] : null,
      response['content'] is Map ? response['content']['accessToken'] : null,
    ];

    for (final candidate in candidates) {
      if (candidate != null) return candidate.toString();
    }
    return null;
  }

  Map<String, dynamic>? _extractUser(Map<String, dynamic> response) {
    final candidates = [
      response['user'],
      response['userSummary'],
      response['data'] is Map ? response['data']['user'] : null,
      response['data'] is Map ? response['data']['userSummary'] : null,
      response['content'] is Map ? response['content']['user'] : null,
      response['content'] is Map ? response['content']['userSummary'] : null,
      response['data'],
      response['content'],
    ];

    for (final candidate in candidates) {
      if (candidate is Map) return Map<String, dynamic>.from(candidate);
    }
    return null;
  }
}
