import 'dart:ui' as ui;
import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:sensors_plus/sensors_plus.dart';

import 'quash_flutter_sdk_platform_interface.dart';

class QuashFlutterSdk {

  Future<String?> getPlatformVersion() {
    return QuashFlutterSdkPlatform.instance.getPlatformVersion();
  }

  Future<Uint8List> getScreenShot() {
    return QuashFlutterSdkPlatform.instance.getScreenShot();
  }
}

class ScreenShotOnShake {
  late Uint8List screenShot;

  void initialize() {
    print("Initialized screenshot on shake");
    accelerometerEventStream().listen((event) {
      if (event.x.abs() > 10 || event.y.abs() > 10 || event.z.abs() > 10) {
        print("Device was shook!");
        captureScreenShot();
      }
    });
  }

  Future<void> captureScreenShot() async{
    screenShot = await QuashFlutterSdkPlatform.instance.getScreenShot();
  }
}
