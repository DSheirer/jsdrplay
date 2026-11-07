#!/usr/bin/env bash
# Downloads the pinned SDRplay API installer and extracts only its C headers
# and licence text to build/sdrplay-api. Works on Linux, macOS and Windows
# (Git Bash).
#
# Usage: native/fetch-headers.sh
#
# The installer is a makeself archive: a shell script followed by a tar.gz
# payload. The payload is cut out after the script's header lines and unpacked
# directly, so no part of the installer runs. The libraries and the service in
# the payload are not extracted.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=versions.env
source "$ROOT/native/versions.env"
OUT="${JSDRPLAY_API_DIR:-$ROOT/build/sdrplay-api}"
INSTALLER="$OUT/installer.run"

rm -rf "$OUT"
mkdir -p "$OUT"

echo "=== SDRplay API $SDRPLAY_API_VERSION headers ==="
curl -fsSL --retry 3 -o "$INSTALLER" "$SDRPLAY_INSTALLER_URL"
if command -v sha256sum >/dev/null; then
    echo "$SDRPLAY_INSTALLER_SHA256  $INSTALLER" | sha256sum -c -
else
    echo "$SDRPLAY_INSTALLER_SHA256  $INSTALLER" | shasum -a 256 -c -
fi

# makeself records the length of its script as: offset=`head -n 523 "$1" ...`
LINES="$(grep -a -m1 -oE 'offset=`head -n [0-9]+' "$INSTALLER" | grep -oE '[0-9]+$' || true)"
if [ -z "$LINES" ]; then
    echo "Unable to find the payload offset in $SDRPLAY_INSTALLER_URL" >&2
    exit 1
fi
tail -n +"$((LINES + 1))" "$INSTALLER" | tar -xzf - -C "$OUT" ./inc ./sdrplay_license.txt
rm "$INSTALLER"

HEADER="$OUT/inc/sdrplay_api.h"
HEADER_VERSION="$(sed -n 's/^#define SDRPLAY_API_VERSION *(float)(\([0-9.]*\)).*/\1/p' "$HEADER")"
if [ "$HEADER_VERSION" != "$SDRPLAY_API_VERSION" ]; then
    echo "sdrplay_api.h is version '$HEADER_VERSION', not $SDRPLAY_API_VERSION" >&2
    exit 1
fi
echo "Extracted SDRplay API $HEADER_VERSION headers to $OUT/inc"
