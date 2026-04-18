#!/usr/bin/env bash
# Run Kotlin live-RPC compatibility tests against a locally-started JS stub
# server. Starts xl1-compat/start-server.mjs, waits for its READY signal,
# sets XL1_RPC_URL for the Kotlin test JVM, tears down cleanly on exit.
#
# Usage: scripts/run-live-compat.sh [gradle-args...]
#   default gradle args: :protocol:testDebugUnitTest --tests "*.rpc.live.*"
#
# Env overrides:
#   XL1_RPC_PORT  — port the Node server should bind (default 18080)
#   NODE          — node binary to use (default: `node` on PATH)

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT="${XL1_RPC_PORT:-18080}"
NODE="${NODE:-node}"

SERVER_DIR="$ROOT/xl1-compat"
SERVER_LOG="$(mktemp -t xl1-compat-server.XXXXXX.log)"
SERVER_PID=""

cleanup() {
  if [[ -n "$SERVER_PID" ]] && kill -0 "$SERVER_PID" 2>/dev/null; then
    kill "$SERVER_PID" 2>/dev/null || true
    wait "$SERVER_PID" 2>/dev/null || true
  fi
  if [[ -f "$SERVER_LOG" ]]; then
    echo "---- server log (${SERVER_LOG}) ----"
    tail -n 50 "$SERVER_LOG" || true
    rm -f "$SERVER_LOG"
  fi
}
trap cleanup EXIT INT TERM

# Start the server in the background, piping its stdout/stderr to a log file.
echo "starting xl1-compat server on port $PORT"
(cd "$SERVER_DIR" && "$NODE" start-server.mjs --port "$PORT") >"$SERVER_LOG" 2>&1 &
SERVER_PID=$!

# Wait up to 30s for the READY line.
for _ in $(seq 1 300); do
  if grep -q "^READY " "$SERVER_LOG" 2>/dev/null; then
    break
  fi
  if ! kill -0 "$SERVER_PID" 2>/dev/null; then
    echo "server exited before readying" >&2
    cat "$SERVER_LOG" >&2
    exit 1
  fi
  sleep 0.1
done

if ! grep -q "^READY " "$SERVER_LOG" 2>/dev/null; then
  echo "server did not ready within 30s" >&2
  cat "$SERVER_LOG" >&2
  exit 1
fi

export XL1_RPC_URL="http://localhost:${PORT}/rpc"
echo "XL1_RPC_URL=$XL1_RPC_URL"

GRADLE_ARGS=("$@")
if [[ ${#GRADLE_ARGS[@]} -eq 0 ]]; then
  GRADLE_ARGS=(":protocol:testDebugUnitTest" "--tests" "*.rpc.live.*")
fi

cd "$ROOT"
./gradlew "${GRADLE_ARGS[@]}"
