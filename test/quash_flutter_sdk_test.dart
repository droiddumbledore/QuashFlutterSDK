import 'package:flutter_test/flutter_test.dart';
import 'package:quash_flutter_sdk/quash_flutter_sdk.dart';
import 'package:quash_flutter_sdk/quash_flutter_sdk_platform_interface.dart';
import 'package:quash_flutter_sdk/quash_flutter_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockQuashFlutterSdkPlatform
    with MockPlatformInterfaceMixin
    implements QuashFlutterSdkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> getScreenShot() {
    // TODO: implement getScreenShot
    throw UnimplementedError();
  }
}

void main() {
  final QuashFlutterSdkPlatform initialPlatform = QuashFlutterSdkPlatform.instance;

  test('$MethodChannelQuashFlutterSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelQuashFlutterSdk>());
  });

  test('getPlatformVersion', () async {
    QuashFlutterSdk quashFlutterSdkPlugin = QuashFlutterSdk();
    MockQuashFlutterSdkPlatform fakePlatform = MockQuashFlutterSdkPlatform();
    QuashFlutterSdkPlatform.instance = fakePlatform;

    expect(await quashFlutterSdkPlugin.getPlatformVersion(), '42');
  });
}
