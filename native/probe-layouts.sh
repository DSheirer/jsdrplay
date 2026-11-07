#!/usr/bin/env bash
# Fetches the pinned SDRplay API headers, checks that Native.java binds every
# function they declare, and compiles and runs layout-probe to record how this
# platform's C compiler lays out the API structures. The Gradle tests compare
# the result, build/sdrplay-api/layout.properties, with Layouts.java.
#
# Usage: native/probe-layouts.sh [x64|ARM64]
#
# Runs on Linux, macOS and Windows (Git Bash, with Visual Studio). On Windows
# the argument selects the MSVC target architecture; it defaults to x64.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
API_DIR="$ROOT/build/sdrplay-api"
BUILD_DIR="$ROOT/build/layout-probe"

"$ROOT/native/fetch-headers.sh"
"$ROOT/native/check-bindings.sh" "$API_DIR/inc/sdrplay_api.h"

echo "=== layout probe ==="
rm -rf "$BUILD_DIR"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*)
        # cmake.exe is a Windows program: give it C:/... paths, not /c/... ones.
        NATIVE_ROOT="$(cd "$ROOT" && pwd -W)"
        CMAKE_ARGS=(-A "${1:-x64}")
        PROBE="$BUILD_DIR/Release/layout-probe.exe"
        ;;
    *)
        NATIVE_ROOT="$ROOT"
        CMAKE_ARGS=(-DCMAKE_BUILD_TYPE=Release)
        PROBE="$BUILD_DIR/layout-probe"
        ;;
esac
cmake -S "$NATIVE_ROOT/native" -B "$NATIVE_ROOT/build/layout-probe" \
    -DSDRPLAY_INCLUDE_DIR="$NATIVE_ROOT/build/sdrplay-api/inc" "${CMAKE_ARGS[@]}" >/dev/null
cmake --build "$NATIVE_ROOT/build/layout-probe" --config Release >/dev/null
# MSVC's runtime prints CRLF line endings.
"$PROBE" | tr -d '\r' > "$API_DIR/layout.properties"
echo "Wrote $(wc -l < "$API_DIR/layout.properties" | tr -d ' ') entries to $API_DIR/layout.properties"
