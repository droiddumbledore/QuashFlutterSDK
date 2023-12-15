
import 'dart:typed_data';

import 'quash_flutter_sdk_platform_interface.dart';

class QuashFlutterSdk {

  Future<String?> getPlatformVersion() {
    return QuashFlutterSdkPlatform.instance.getPlatformVersion();
  }

  Future<Uint8List> getScreenShot() {
    return QuashFlutterSdkPlatform.instance.getScreenShot();
  }
}
