import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'quash_flutter_sdk_method_channel.dart';

abstract class QuashFlutterSdkPlatform extends PlatformInterface {
  /// Constructs a QuashFlutterSdkPlatform.
  QuashFlutterSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static QuashFlutterSdkPlatform _instance = MethodChannelQuashFlutterSdk();

  /// The default instance of [QuashFlutterSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelQuashFlutterSdk].
  static QuashFlutterSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [QuashFlutterSdkPlatform] when
  /// they register themselves.
  static set instance(QuashFlutterSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getScreenShot(){
    throw UnimplementedError('getScreenShot() has not been implemented');
  }
}
