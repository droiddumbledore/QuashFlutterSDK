import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:quash_flutter_sdk/quash_flutter_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _quashFlutterSdkPlugin = QuashFlutterSdk();
  bool visible = false;
  ScreenShotOnShake screenShotOnShake = ScreenShotOnShake();

  @override
  void initState() {
    super.initState();
    initPlatformState();
    screenShotOnShake.initialize();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _quashFlutterSdkPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Running on: $_platformVersion\n'),
            const Text('Shake the device first'),
            const Text('then tap on view screenshot to see the screenshot'),
            const SizedBox(
              height: 10,
            ),
            OutlinedButton(
                onPressed: () {
                  setState(() {
                    visible = true;
                  });
                },
                child: const Text('View Screenshot')),
            const SizedBox(
              height: 10,
            ),
            visible
                ? screenShotOnShake.screenShot.isNotEmpty
                ? Center(
                child: Container(
                    decoration: BoxDecoration(
                      border: Border.all(),
                    ),
                    child: Image.memory(
                      screenShotOnShake.screenShot,
                      height: MediaQuery.of(context).size.height * 0.5,
                    )))
                : const Center(
              child: Text('Device was not shaken'),
            )
                : Container()
          ],
        ),
      ),
    );
  }
}


