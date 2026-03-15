# GH FastPass

Auto-dismiss GitHub mobile's "Verification request approved" dialog.
Hooks the `TwoFactorDialog` state machine via [LSPosed](https://github.com/JingMatrix/LSPosed) and finishes the activity on `FINISHED_APPROVED`. Covers 2FA logins, sudo challenges, and device verifications.

<p align="center">
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Android-10+-3DDC84?style=flat&logo=android&logoColor=white" alt="Android 10+" /></a>
  <a href="https://github.com/LSPosed/LSPosed"><img src="https://img.shields.io/badge/LSPosed_API-100-8F00FF?style=flat" alt="LSPosed API 100" /></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" /></a>
  <a href="https://gradle.org"><img src="https://img.shields.io/badge/Gradle-9.4-02303A?style=flat&logo=gradle&logoColor=white" alt="Gradle" /></a>
</p>

## How it works

Two hooks, no UI. Hook targets are resolved by type signature, not obfuscated names, so it survives routine ProGuard shuffles across app updates.

1. `TwoFactorActivity.onCreate()`:captures a `WeakReference<Activity>`
2. Static state mapper (returns the dialog state enum):when `FINISHED_APPROVED`, posts `activity.finish()` via main handler

## Install

1. Grab the APK from [Releases](../../releases)
2. Enable in LSPosed, scope to `com.github.android`
3. Force-stop GitHub and reopen

No settings, no config.

## Verify

Trigger a 2FA prompt (incognito login on [github.com](https://github.com) is the fastest way), approve it, and the dialog should vanish.

```
adb logcat -s LSPosedContext | grep ghfastpass
```
```
GH FastPass v1.0.0 loaded
Hooks registered for com.github.android
Auto-dismissing verification dialog
```

## Build

```bash
git clone --recurse-submodules https://github.com/hxreborn/gh-fast-pass.git
cd gh-fast-pass
./gradlew buildLibxposed
./gradlew :app:assembleDebug
```

JDK 21, Android SDK.

## License

[GPLv3](LICENSE). See the license file for details.
