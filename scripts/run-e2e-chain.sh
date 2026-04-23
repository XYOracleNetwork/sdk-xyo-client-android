#!/usr/bin/env bash
# Run Kotlin protocol e2e tests against a real XL1 chain started from the
# published @xyo-network/xl1-cli npm package.
#
# Usage: scripts/run-e2e-chain.sh [gradle-args...]
#   default gradle args: :protocol:testDebugUnitTest --tests "*.rpc.e2e.*"
#
# Env overrides:
#   XL1_E2E_PORT         - API port for the CLI chain (default 18081)
#   XL1_CLI_VERSION      - npm version of @xyo-network/xl1-cli (default 1.20.24)
#   XL1_E2E_STARTUP_SECS - startup timeout in seconds (default 240)

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT="${XL1_E2E_PORT:-18081}"
XL1_CLI_VERSION="${XL1_CLI_VERSION:-1.20.24}"
STARTUP_SECS="${XL1_E2E_STARTUP_SECS:-240}"
CHAIN_DIR="$(mktemp -d -t xl1-cli-e2e.XXXXXX)"
SERVER_LOG="$(mktemp -t xl1-cli-e2e.XXXXXX.log)"
SERVER_PID=""
RPC_PORT=""

JAVA_HOME_DEFAULT="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
GENESIS_MNEMONIC="test test test test test test test test test test test junk"

cleanup() {
  if [[ -n "$SERVER_PID" ]] && kill -0 "$SERVER_PID" 2>/dev/null; then
    kill "$SERVER_PID" 2>/dev/null || true
    wait "$SERVER_PID" 2>/dev/null || true
  fi
  if [[ -f "$SERVER_LOG" ]]; then
    echo "---- server log (${SERVER_LOG}) ----"
    tail -n 100 "$SERVER_LOG" || true
    rm -f "$SERVER_LOG"
  fi
  rm -rf "$CHAIN_DIR"
}
trap cleanup EXIT INT TERM

echo "starting XL1 CLI ${XL1_CLI_VERSION} on port ${PORT}"

(
  cd "$CHAIN_DIR"
  export XL1_API__PORT="${PORT}"
  export XL1_LOG_LEVEL="warn"
  npm exec --yes --package="@xyo-network/xl1-cli@${XL1_CLI_VERSION}" -- \
    xl1 start
) >"$SERVER_LOG" 2>&1 &
SERVER_PID=$!

rpc_ready() {
  local port="$1"
  /usr/bin/curl -sS \
    -H 'Content-Type: application/json' \
    --data '{"jsonrpc":"2.0","id":1,"method":"blockViewer_currentBlock","params":[]}' \
    "http://127.0.0.1:${port}/rpc" 2>/dev/null | rg -q '"result"'
}

for _ in $(seq 1 $((STARTUP_SECS * 10))); do
  if rpc_ready "$PORT"; then
    RPC_PORT="$PORT"
    break
  fi
  if [[ "$PORT" != "8080" ]] && rpc_ready "8080"; then
    RPC_PORT="8080"
    break
  fi
  if ! kill -0 "$SERVER_PID" 2>/dev/null; then
    echo "xl1-cli exited before becoming ready" >&2
    cat "$SERVER_LOG" >&2
    exit 1
  fi
  sleep 0.1
done

if [[ -z "$RPC_PORT" ]]; then
  echo "xl1-cli did not answer JSON-RPC within ${STARTUP_SECS}s" >&2
  cat "$SERVER_LOG" >&2
  exit 1
fi

export XL1_E2E_RPC_URL="http://127.0.0.1:${RPC_PORT}/rpc"
export XL1_E2E_GENESIS_MNEMONIC="$GENESIS_MNEMONIC"
export JAVA_HOME="${JAVA_HOME:-$JAVA_HOME_DEFAULT}"
export PATH="$JAVA_HOME/bin:$PATH"

echo "XL1_E2E_RPC_URL=$XL1_E2E_RPC_URL"

GRADLE_ARGS=("$@")
if [[ ${#GRADLE_ARGS[@]} -eq 0 ]]; then
  GRADLE_ARGS=(":protocol:testDebugUnitTest" "--tests" "*.rpc.e2e.*")
fi

cd "$ROOT"
./gradlew "${GRADLE_ARGS[@]}"
