#!/usr/bin/env bash
# Compares the functions in sdrplay_api.h with the bindings in Native.java, so
# a new API function fails the build rather than being silently unavailable.
#
# Usage: native/check-bindings.sh <path-to-sdrplay_api.h>
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HEADER="$1"
BINDINGS="$ROOT/src/main/java/io/github/dsheirer/sdrplay/internal/Native.java"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

# Declarations look like: _SDRPLAY_DLL_QUALIFIER sdrplay_api_ErrT sdrplay_api_Open(void);
# The function pointer typedefs above them start with "typedef" and are skipped.
grep -E '^[[:space:]]*_SDRPLAY_DLL_QUALIFIER[[:space:]]' "$HEADER" \
    | grep -oE 'sdrplay_api_[A-Za-z0-9_]+[[:space:]]*\(' | tr -d ' (' | sort -u > "$WORK/header"
grep -oE '"sdrplay_api_[A-Za-z0-9_]+"' "$BINDINGS" | tr -d '"' | sort -u > "$WORK/bindings"

if [ ! -s "$WORK/header" ]; then
    echo "No functions found in $HEADER" >&2
    exit 1
fi
MISSING="$(comm -23 "$WORK/header" "$WORK/bindings")"
STALE="$(comm -13 "$WORK/header" "$WORK/bindings")"
if [ -n "$MISSING" ] || [ -n "$STALE" ]; then
    [ -z "$MISSING" ] || printf 'Functions in sdrplay_api.h without a binding:\n%s\n' "$MISSING" >&2
    [ -z "$STALE" ] || printf 'Bindings not in sdrplay_api.h:\n%s\n' "$STALE" >&2
    exit 1
fi
echo "Bindings match sdrplay_api.h ($(wc -l < "$WORK/header" | tr -d ' ') functions)"
