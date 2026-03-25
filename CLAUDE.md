# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android SDK for XYO Protocol 2.0. Pure Kotlin, two modules:

- **`sdk`** — Core SDK (`network.xyo.client`): accounts/wallets, bound witnesses, payloads, witnesses, node communication, archivist integration, Android data storage via protobuf DataStore
- **`protocol`** — XYO Chain Protocol (`network.xyo.chain.protocol`): XL1 token denominations, transactions, RPC framework, block/mempool/stake runners, chain viewers

The `protocol` module depends on `sdk`.

## Build Commands

```bash
./gradlew build              # Full build (compile + test)
./gradlew test               # Run all unit tests
./gradlew :sdk:test          # Test SDK module only
./gradlew :protocol:test     # Test protocol module only
./gradlew :sdk:assemble      # Compile SDK without tests
./gradlew clean              # Clean build artifacts
```

Instrumentation tests require a connected device/emulator:
```bash
./gradlew :sdk:connectedAndroidTest
```

## Build Configuration

- **Gradle**: 8.9.3 (use `./gradlew` wrapper)
- **Kotlin**: 2.3.20, JVM target 17
- **Android**: compileSdk 36, minSdk 23, targetSdk 36
- **Dependency versions**: Centralized in `gradle/libs.versions.toml`
- **Versioning**: Single version across both modules, defined in `sdk/build.gradle.kts` (`Major`, `Minor`, `Patch` vals)
- **Publishing**: JitPack (configured in `jitpack.yml`, uses OpenJDK 18)

## Key Libraries

| Purpose | Library |
|---------|---------|
| JSON serialization | Moshi 1.15.2 + KSP codegen |
| HTTP | OkHttp 5.3.2 |
| Cryptography | BouncyCastle (jdk18on), SECP256k1 |
| HD Wallets | hdwallet (Figure tech) |
| Big numbers | Kotlin BigNum |
| Protobuf storage | protobuf-javalite (lite runtime) |
| Async | Kotlin Coroutines 1.10.2 |
| Testing | JUnit 5 (Jupiter) with mannodermaus Android adapter |

## Architecture

**Witness system**: `XyoWitness<T>` (abstract) → concrete witnesses (location, system info). `XyoPanel` orchestrates witnesses, collects observations, and reports to XYO nodes.

**BoundWitness**: Core protocol data structure. Built via `BoundWitnessBuilder` (builder pattern). `QueryBoundWitness` variant for node queries.

**Account/Wallet**: SECP256k1 elliptic curve signing. `Account` handles key management and signing; `Wallet` extends with HD wallet derivation. Keys stored via protobuf-backed Android DataStore.

**Node communication**: `NodeClient` for HTTP communication with XYO nodes. `ArchivistWrapper` for payload storage operations.

**Protocol module**: XL1 denomination classes (`AttoXL1` through `XL1`), transaction framework with fee calculation, RPC transport layer for chain interaction.

## Testing

- **Unit tests**: `src/test/java/` — standard JVM tests
- **Instrumentation tests**: `sdk/src/androidTest/java/` — require Android device/emulator, 20 test files covering bound witnesses, accounts, payloads, node client, panel
- **Framework**: JUnit 5 with `de.mannodermaus.junit5` Android adapter
- **Test runner**: `AndroidJUnit5Builder` configured as instrumentation runner builder

## Code Generation

- **Moshi codegen** via KSP — generates JSON adapters for model classes
- **Protobuf** — two `.proto` files in `sdk/src/main/java/network/xyo/client/proto/` generate data store classes (lite runtime, package `network.xyo.data`)
