#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SDK_DIR="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Library/Android/sdk}}"
ADB="$SDK_DIR/platform-tools/adb"
EMULATOR="$SDK_DIR/emulator/emulator"
AVD_NAME="${1:-Medium_Phone_API_36}"

if [[ ! -x "$ADB" || ! -x "$EMULATOR" ]]; then
  echo "Android SDK tools were not found under: $SDK_DIR" >&2
  echo "Set ANDROID_SDK_ROOT and try again." >&2
  exit 1
fi

if ! "$EMULATOR" -list-avds | grep -Fxq "$AVD_NAME"; then
  echo "AVD '$AVD_NAME' is not installed. Available AVDs:" >&2
  "$EMULATOR" -list-avds >&2
  exit 1
fi

if ! "$ADB" devices | awk 'NR > 1 && $2 == "device" { found=1 } END { exit !found }'; then
  echo "Starting emulator: $AVD_NAME"
  "$EMULATOR" -avd "$AVD_NAME" -no-snapshot-save >/tmp/kemistri-everyday-emulator.log 2>&1 &
fi

echo "Waiting for Android to finish booting…"
"$ADB" wait-for-device
until [[ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
  sleep 1
done

cd "$PROJECT_DIR"
echo "Running JVM tests, building, and running the emulator smoke test…"
./gradlew testDebugUnitTest :app:connectedDebugAndroidTest

echo "Launching Kemistri Everyday…"
"$ADB" shell am start -W -n uk.edbetter.kemdaily/.MainActivity
echo "Smoke test passed. The app is open on the emulator."
