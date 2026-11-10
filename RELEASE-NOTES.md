Access Pause 1.0 saves enabled Android app accessibility services, pauses them, and restores the saved list on the next tap. Includes a Quick Settings tile and an in-app button. Recovery data remains available after failed settings writes.

Android 7.0+ required. One-time ADB setup is required:

```sh
adb shell pm grant app.accesspause android.permission.WRITE_SECURE_SETTINGS
```

Download Access-Pause.apk and follow SETUP.txt. The app has no internet permission or accessibility service. It controls app accessibility services, not every system accessibility option. Banking-app compatibility and manufacturer permission support are not guaranteed.

Validation: nine save/restore and recovery logic scenarios passed; APK signing and manifest verified. Not tested on a physical Android device.

APK SHA-256: `5120d977cb666773329e835c555d9977f7066ae154a1ef63c1916b1f00d738c5`
