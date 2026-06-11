class ApiException implements Exception {
  const ApiException(this.message, {this.statusCode, this.data});

  final String message;
  final int? statusCode;
  final dynamic data;

  @override
  String toString() {
    if (statusCode == null) return message;
    return '$message ($statusCode)';
  }
}
