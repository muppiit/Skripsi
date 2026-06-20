import 'dart:io';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/widgets.dart';

class ClientAuditInfo {
  const ClientAuditInfo._();

  static Map<String, dynamic> deviceInfo(BuildContext context) {
    final mediaQuery = MediaQuery.maybeOf(context);
    final view = View.maybeOf(context);

    return {
      'platform': Platform.operatingSystem,
      'platformVersion': Platform.operatingSystemVersion,
      'locale': Platform.localeName,
      'screenWidth': mediaQuery?.size.width,
      'screenHeight': mediaQuery?.size.height,
      'devicePixelRatio': view?.devicePixelRatio,
      'appRuntime': 'flutter',
    };
  }

  static Future<Map<String, dynamic>> networkInfo() async {
    final connectivity = await Connectivity().checkConnectivity();
    return {
      'online': !connectivity.contains(ConnectivityResult.none),
      'connectionTypes': connectivity.map((result) => result.name).toList(),
    };
  }
}
