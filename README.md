# GH FastPass

Automatically dismiss GitHub Mobile's 2FA verification dialog after approval.

<p align="center">
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Android-10+-3DDC84?style=flat&logo=android&logoColor=white" alt="Android 10+" /></a>
  <img src="https://img.shields.io/badge/LSPosed_API-101-8F00FF?style=flat" alt="LSPosed API 101" />
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" /></a>
  <a href="https://gradle.org"><img src="https://img.shields.io/badge/Gradle-9.4-02303A?style=flat&logo=gradle&logoColor=white" alt="Gradle" /></a>
</p>

## Overview

Every time you approve a 2FA push notification in the GitHub app, a "Verification request approved" dialog blocks the screen until you tap CLOSE. This module removes that friction by hooking the dialog's Compose state machine and finishing the activity as soon as the approval completes.

<table>
<tr><th>Stock</th><th>Patched</th></tr>
<tr>
  <td><img src="https://github.com/user-attachments/assets/ffe91011-521d-455a-9f64-c5d7e5618382" width="280" alt="Stock GitHub 2FA dialog" /></td>
  <td><img src="https://github.com/user-attachments/assets/3a3f9a3d-4391-45de-be03-04e65c515f67" width="280" alt="Patched auto-dismiss" /></td>
</tr>
</table>

## Requirements

- Android 10 (API 29) or higher
- An LSPosed Manager version with API 101 support (required for now)
- GitHub mobile app (`com.github.android`)

> [!NOTE]
> Tested with GitHub Mobile `v1.249.1`. Other versions should work as long as GitHub doesn't restructure the 2FA dialog internals.

## Install

1. Download the APK:

    <a href="../../releases"><img src="https://github.com/user-attachments/assets/d18f850c-e4d2-4e00-8b03-3b0e87e90954" height="60" alt="Get it on GitHub" /></a>
    <a href="http://apps.obtainium.imranr.dev/redirect.html?r=obtainium://app/%7B%22id%22%3A%22eu.hxreborn.ghfastpass%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fhxreborn%2Fgh-fast-pass%22%2C%22author%22%3A%22rafareborn%22%2C%22name%22%3A%22GH%20FastPass%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Afalse%7D%22%7D"><img src="https://github.com/user-attachments/assets/dffe8fb9-c0d1-470b-8d69-6d5b38a8aa2d" height="60" alt="Get it on Obtainium" /></a>

2. Enable the module in LSPosed and scope it to `com.github.android`
3. Force-stop the GitHub app and relaunch

## Build

```bash
git clone --recurse-submodules https://github.com/hxreborn/gh-fast-pass.git
cd gh-fast-pass
./gradlew buildLibxposed
./gradlew assembleRelease
```

Requires JDK 21 and Android SDK. Configure `local.properties`:

```properties
sdk.dir=/path/to/android/sdk

# Optional signing
RELEASE_STORE_FILE=<path/to/keystore.jks>
RELEASE_STORE_PASSWORD=<store_password>
RELEASE_KEY_ALIAS=<key_alias>
RELEASE_KEY_PASSWORD=<key_password>
```

## License

<a href="LICENSE"><img src="https://github.com/user-attachments/assets/b211cf0d-e255-421c-9213-6b6258676013" height="90" alt="GPLv3"></a>

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file for details.
