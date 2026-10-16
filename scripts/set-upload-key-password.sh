#!/usr/bin/env bash
# Changes the Play upload-key password and updates keystore.properties (and its backup copy)
# without the password ever appearing on screen or in shell history.
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PROPS="$PROJECT_DIR/keystore.properties"
KEYTOOL="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}/bin/keytool"

[[ -f "$PROPS" ]] || { echo "keystore.properties not found in $PROJECT_DIR" >&2; exit 1; }
STORE_FILE="$(grep '^storeFile=' "$PROPS" | cut -d= -f2-)"
OLD="$(grep '^storePassword=' "$PROPS" | cut -d= -f2-)"
BACKUP="${STORE_FILE%.jks}.properties"

read -rsp "New upload-key password (at least 8 characters): " NEW; echo
read -rsp "Repeat the new password: " AGAIN; echo
[[ "$NEW" == "$AGAIN" ]] || { echo "Passwords do not match." >&2; exit 1; }
[[ ${#NEW} -ge 8 ]] || { echo "Password must be at least 8 characters." >&2; exit 1; }
[[ "$NEW" != *$'\n'* ]] || { echo "Password cannot contain a line break." >&2; exit 1; }

# PKCS12 keystores share one password for the store and the key, so this changes both.
printf '%s\n%s\n%s\n' "$OLD" "$NEW" "$NEW" | "$KEYTOOL" -storepasswd -keystore "$STORE_FILE" >/dev/null
NEW_PW="$NEW" "$KEYTOOL" -list -keystore "$STORE_FILE" -storepass:env NEW_PW >/dev/null || { echo "Password change could not be verified." >&2; exit 1; }

for file in "$PROPS" "$BACKUP"; do
  [[ -f "$file" ]] || continue
  STORE_LINE="storePassword=$NEW" KEY_LINE="keyPassword=$NEW" python3 - "$file" <<'EOF'
import os, sys
path = sys.argv[1]
lines = open(path).read().splitlines()
out = [os.environ["STORE_LINE"] if l.startswith("storePassword=") else
       os.environ["KEY_LINE"] if l.startswith("keyPassword=") else l for l in lines]
open(path, "w").write("\n".join(out) + "\n")
EOF
  chmod 600 "$file"
done

echo "Upload-key password changed. keystore.properties and $(basename "$BACKUP") are updated."
