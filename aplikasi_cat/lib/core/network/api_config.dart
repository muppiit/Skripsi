class ApiConfig {
  const ApiConfig._();

  static const baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8081/api',
  );

  static const connectTimeout = Duration(seconds: 20);
  static const receiveTimeout = Duration(seconds: 60);
}
