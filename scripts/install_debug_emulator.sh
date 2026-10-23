#!/usr/bin/env bash
# Install the current debug APK on a running emulator/device (avoids stale snapshot APK).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
ADB="$ANDROID_HOME/platform-tools/adb"
PKG=com.smaranam.app
# Early prototype used a different applicationId; both show as "Naamalu" on the launcher.
LEGACY_PKG=com.naamalu.app

# Stale AVD locks make Android Studio think Pixel 8 is already running (wrong PID).
AVD_DIR="${HOME}/.android/avd/Pixel_8.avd"
if [[ -d "$AVD_DIR" ]]; then
  rm -f "$AVD_DIR/hardware-qemu.ini.lock" "$AVD_DIR/multiinstance.lock"
fi

if ! "$ADB" devices | grep -qE '[[:space:]]device$'; then
  echo "No device/emulator connected. Start Pixel_8 from Device Manager, wait for home screen, then re-run:"
  echo "  $0"
  exit 1
fi

cd "$ROOT"
# macOS sometimes duplicates files under build/ and breaks packaging.
find app/build core -name '* 2.*' -delete 2>/dev/null || true

./gradlew :app:assembleDebug --console=plain
if "$ADB" shell pm path "$LEGACY_PKG" >/dev/null 2>&1; then
  echo "Removing legacy launcher app $LEGACY_PKG (0.1.0 prototype — easy to open by mistake)."
  "$ADB" uninstall "$LEGACY_PKG" || true
fi
"$ADB" install -r app/build/outputs/apk/debug/app-debug.apk
"$ADB" shell am start -n "$PKG/.MainActivity"
ver=$("$ADB" shell dumpsys package "$PKG" | grep versionName | head -1 | tr -d ' ')
echo "Installed $PKG ($ver). Open this icon (not a second Naamalu). Settings → footer: 0.3.0-dev · UI v3."
