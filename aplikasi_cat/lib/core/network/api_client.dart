import 'dart:convert';

import 'package:dio/dio.dart';

import '../storage/token_storage.dart';
import 'api_config.dart';
import 'api_exception.dart';

class ApiClient {
  ApiClient({Dio? dio, TokenStorage? tokenStorage})
    : _dio = dio ?? Dio(),
      _tokenStorage = tokenStorage ?? const TokenStorage() {
    _dio.options = BaseOptions(
      baseUrl: ApiConfig.baseUrl,
      connectTimeout: ApiConfig.connectTimeout,
      receiveTimeout: ApiConfig.receiveTimeout,
      headers: const {'Content-Type': 'application/json'},
    );

    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await _tokenStorage.getToken();
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          handler.next(options);
        },
        onError: (error, handler) {
          handler.reject(error);
        },
      ),
    );
  }

  final Dio _dio;
  final TokenStorage _tokenStorage;

  Future<Map<String, dynamic>> get(
    String path, {
    Map<String, dynamic>? queryParameters,
  }) async {
    return _request(() => _dio.get(path, queryParameters: queryParameters));
  }

  Future<Map<String, dynamic>> post(
    String path, {
    Map<String, dynamic>? data,
  }) async {
    return _request(() => _dio.post(path, data: data));
  }

  Future<Map<String, dynamic>> put(
    String path, {
    Map<String, dynamic>? data,
  }) async {
    return _request(() => _dio.put(path, data: data));
  }

  Future<Map<String, dynamic>> _request(
    Future<Response<dynamic>> Function() request,
  ) async {
    try {
      final response = await request();
      final responseData = response.data;

      if (responseData is Map<String, dynamic>) return responseData;
      if (responseData is Map) return Map<String, dynamic>.from(responseData);

      return {'data': responseData};
    } on DioException catch (error) {
      final statusCode = error.response?.statusCode;
      final data = error.response?.data;
      final message =
          _extractErrorMessage(data) ??
          error.response?.statusMessage ??
          error.message ??
          'Terjadi kesalahan koneksi';

      throw ApiException(
        _formatErrorMessage(message, statusCode, data),
        statusCode: statusCode,
        data: data,
      );
    }
  }

  String? _extractErrorMessage(dynamic data) {
    if (data is Map) {
      final message =
          data['message'] ??
          data['error'] ??
          data['errorMessage'] ??
          data['detail'] ??
          data['path'];
      if (message != null) return message.toString();
    }
    if (data is String && data.trim().isNotEmpty) {
      final text = data.trim();
      if (!text.startsWith('<!DOCTYPE') && !text.startsWith('<html')) {
        return text;
      }
    }
    return null;
  }

  String _formatErrorMessage(String message, int? statusCode, dynamic data) {
    final statusText = statusCode == null ? '' : ' ($statusCode)';
    if (data == null || data is String) return '$message$statusText';

    try {
      final body = jsonEncode(data);
      if (body.length <= 240) return '$message$statusText: $body';
      return '$message$statusText: ${body.substring(0, 240)}...';
    } catch (_) {
      return '$message$statusText';
    }
  }
}
