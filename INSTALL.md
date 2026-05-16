# Swing Music Android — Install

Build output: `app/build/outputs/apk/debug/app-debug.apk` (~54 MB).
Copies at project root: `swing-music-android-vX.Y.apk`.

## Install on device

```bash
# 1. Plug in device with USB debugging enabled, or run an emulator.
~/android-sdk/platform-tools/adb devices

# 2. Install.
~/android-sdk/platform-tools/adb install -r "swing-music-android-v0.3.apk"
```

Or transfer the APK to the device and install via the Files app (allow installation from unknown sources).

## Rebuild

```bash
cd "swing music app"
ANDROID_HOME=/home/volt/android-sdk JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :app:assembleDebug
```

## Pair to your Swing Music server

1. Open the app. Login flow appears.
2. On your server's web UI, go to **Settings → Pair device**. Scan the QR code with the app, or enter server URL + credentials manually.
