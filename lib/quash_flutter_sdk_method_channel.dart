import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'quash_flutter_sdk_platform_interface.dart';

/// An implementation of [QuashFlutterSdkPlatform] that uses method channels.
class MethodChannelQuashFlutterSdk extends QuashFlutterSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('quash_flutter_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<Uint8List> getScreenShot() async{
    final screenShot = await methodChannel.invokeMethod<Uint8List>('getScreenShot');
    return screenShot!!;
  }
}
