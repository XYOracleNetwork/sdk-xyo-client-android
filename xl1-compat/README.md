# xl1-compat

JS-side compatibility harness for the Kotlin SDK. Two responsibilities:

1. **`generate-vectors.mjs`** — produces `jsCompatVectors.json`, a frozen set of
   cryptographic and hashing test vectors computed by the JS SDK. The Kotlin
   test suite (`JsVector*Test` classes under `sdk/src/test/` and
   `protocol/src/test/`) loads this file as a resource and asserts that the
   Kotlin implementations produce bit-identical results.

2. **`start-server.mjs`** — (Phase 2) spins up a local JSON-RPC server backed
   by the JS XL1 protocol SDK so Kotlin can exercise its RPC transport
   against a real peer.

## Usage

```bash
cd xl1-compat
npm install
npm run generate   # writes sdk/src/test/resources/jsCompatVectors.json
```

Regenerating the file is a deliberate act — if the output differs from the
committed version, a JS-side behaviour changed and the Kotlin side must be
checked for compatibility before the new file is committed.

## Pinned versions

- `@xyo-network/*` at 5.3.30 (JS SDK baseline)
- `@xyo-network/xl1-*` at 1.26.34 (XL1 baseline)

When bumping Kotlin SDK compatibility to a newer JS release, bump these
versions, regenerate, and expect vector-diff review.
