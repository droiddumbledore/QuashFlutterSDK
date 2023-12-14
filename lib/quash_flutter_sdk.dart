
import 'quash_flutter_sdk_platform_interface.dart';

class QuashFlutterSdk {

  Future<String?> getPlatformVersion() {
    return QuashFlutterSdkPlatform.instance.getPlatformVersion();
  }

  Future<String?> getScreenShot() {
    return QuashFlutterSdkPlatform.instance.getScreenShot();
  }
}
