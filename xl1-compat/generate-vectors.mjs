#!/usr/bin/env node
// Generates cross-SDK test vectors for the Kotlin client to verify against.
//
// Output: ../sdk/src/test/resources/jsCompatVectors.json
//
// The vector file is a frozen contract between the JS and Kotlin SDKs at
// specific pinned versions (see package.json). Regenerating it is a deliberate
// act; when output diverges, the Kotlin side must be re-verified.

import { writeFileSync, mkdirSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

import { Account } from '@xyo-network/account'
import { BoundWitnessBuilder } from '@xyo-network/boundwitness-builder'
import { Elliptic } from '@xyo-network/elliptic'
import { ObjectHasher } from '@xyo-network/hash'
import { PayloadBuilder } from '@xyo-network/payload-builder'
import { HDWallet } from '@xyo-network/wallet'
import { AttoXL1ConvertFactor } from '@xyo-network/xl1-protocol-model'

await Elliptic.initialize()

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)
const outputPaths = [
  resolve(__dirname, '../sdk/src/test/resources/jsCompatVectors.json'),
  resolve(__dirname, '../protocol/src/test/resources/jsCompatVectors.json'),
]

// ---------- Account vectors ----------
// Fixed private keys paired with fixed message hashes. Because ECDSA signing
// on secp256k1 is RFC6979-deterministic, every (key, hash) pair yields exactly
// one signature — any cross-SDK divergence is a bug.

const accountPrivateKeys = [
  '7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f',
  '0102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20',
  '1111111111111111111111111111111111111111111111111111111111111111',
  'deadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeef',
  '2222222222222222222222222222222222222222222222222222222222222222',
]

const messageHashes = [
  '4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a',
  '0000000000000000000000000000000000000000000000000000000000000000',
  // sha256("Hello, World!") — representative of a real message hash. The
  // all-ones 32-byte value was deliberately excluded here: it exceeds the
  // secp256k1 curve order n, and BouncyCastle (Kotlin) and libauth (JS)
  // apply the mod-n reduction at different points in RFC6979, producing
  // different-but-individually-valid signatures. Real SHA-256 outputs have
  // ~2^-128 probability of hitting that region, so it's not worth aligning.
  'dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f',
]

const hexToBytes = (hex) => {
  const clean = hex.startsWith('0x') ? hex.slice(2) : hex
  const out = new Uint8Array(clean.length / 2)
  for (let i = 0; i < out.length; i++) {
    out[i] = parseInt(clean.slice(i * 2, i * 2 + 2), 16)
  }
  return out
}

const bytesToHex = (buf) => {
  const arr = buf instanceof Uint8Array ? buf : new Uint8Array(buf)
  return Array.from(arr, b => b.toString(16).padStart(2, '0')).join('')
}

// The JS Account class memoizes instances in a static WeakRef map keyed by
// address. That means Account.fromPrivateKey(k) returns the SAME instance
// across calls, and its _previousHash state is sticky. Between vector phases
// we need a clean slate so previous_hashes in BoundWitness vectors reflect
// fresh accounts, not residue from earlier signing tests.
function resetAccountCache() {
  Account._addressMap = {}
}

async function buildAccountVectors() {
  resetAccountCache()
  const vectors = []
  for (const priv of accountPrivateKeys) {
    resetAccountCache()
    const account = await Account.fromPrivateKey(priv)
    const signatures = []
    // Each hash needs a fresh account instance — Account.sign advances the
    // previousHash chain, which would alter the second signature's context.
    for (const hash of messageHashes) {
      resetAccountCache()
      const fresh = await Account.fromPrivateKey(priv)
      const [sigBuffer] = await fresh.sign(hexToBytes(hash).buffer, undefined)
      signatures.push({
        message_hash: hash,
        signature: bytesToHex(sigBuffer),
      })
    }
    vectors.push({
      private_key: priv,
      public_key_uncompressed: bytesToHex(account.public.bytes),
      address: account.address,
      signatures,
    })
  }
  return vectors
}

// ---------- HDWallet vectors ----------
// BIP-44 Ethereum-style derivation. Two mnemonics, several paths each.

const hdWalletCases = [
  {
    mnemonic: 'later puppy sound rebuild rebuild noise ozone amazing hope broccoli crystal grief',
    paths: [
      "m/44'/60'/0'/0/0",
      "m/44'/60'/0'/0/1",
      "m/44'/60'/0'/0/2",
      "m/44'/60'/0'/0/3",
      "m/44'/60'/0'/0/4",
      "m/44'/60'/0'/0/5",
      "m/44'/60'/0'/0/6",
      "m/44'/60'/0'/0/7",
      "m/44'/60'/0'/0/8",
      "m/44'/60'/0'/0/9",
      "m/44'/60'/0'/1/0",
      "m/44'/60'/1'/0/0",
    ],
  },
  {
    mnemonic: 'abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about',
    paths: [
      "m/44'/60'/0'/0/0",
      "m/44'/60'/0'/0/1",
    ],
  },
]

async function buildHdWalletVectors() {
  const vectors = []
  for (const { mnemonic, paths } of hdWalletCases) {
    const entries = []
    for (const path of paths) {
      const wallet = await HDWallet.fromPhrase(mnemonic, path)
      entries.push({
        path,
        private_key: bytesToHex(wallet.private.bytes),
        public_key_uncompressed: bytesToHex(wallet.public.bytes),
        address: wallet.address,
      })
    }
    vectors.push({ mnemonic, derivations: entries })
  }
  return vectors
}

// ---------- Payload hash vectors ----------
// Each raw payload is emitted as JSON.stringify output so the Kotlin side
// has a concrete string to feed into JSONObject + canonicalization. The
// expected dataHash (sha256 over canonical JSON with both $ and _ meta
// stripped) and hash (only _ stripped) come from the JS ObjectHasher.
//
// The JS side is authoritative for canonicalization: `null` values are
// preserved (both as object field values and inside arrays); only
// `undefined` is stripped (implicitly, by JSON.stringify, on both sides).

const payloadCases = [
  { id: 'simple', payload: { schema: 'network.xyo.test', value: 'hello' } },
  { id: 'unsorted-flat', payload: { z: 1, a: 2, schema: 'network.xyo.test' } },
  { id: 'nested-unsorted', payload: { schema: 'network.xyo.test', nested: { b: 1, a: 2 } } },
  { id: 'with-storage-meta', payload: { _hash: 'abc', schema: 'network.xyo.test', data: 'value' } },
  { id: 'with-client-meta', payload: { $sourceQuery: 'abc', schema: 'network.xyo.test', data: 'value' } },
  { id: 'with-both-meta', payload: { _hash: 'abc', $sourceQuery: 'def', schema: 'network.xyo.test', data: 'value' } },
  { id: 'nested-meta-preserved', payload: { schema: 'network.xyo.test', data: { _nested: 'kept', $nested: 'kept' } } },
  { id: 'array-with-null', payload: { hashes: ['abc', null, 'def'], schema: 'network.xyo.test' } },
  { id: 'top-level-null', payload: { data: 'value', schema: 'network.xyo.test', timestamp: null } },
  { id: 'nested-object-null', payload: { schema: 'network.xyo.test', nested: { a: 1, b: null, c: 3 } } },
  { id: 'mixed-nulls', payload: { a: null, arr: [1, null, 2], b: 'value', nested: { x: null }, schema: 'network.xyo.test' } },
  // Reference fixture from sdk-xyo-client-js ObjectHasher.spec.ts. testUndefined
  // and testNullObject.x values are omitted here because JS JSON.stringify
  // also drops them — the wire JSON Kotlin receives has no representation for
  // `undefined`. Nulls (both standalone and inside nested objects) are kept.
  {
    id: 'object-hasher-reference',
    payload: {
      schema: 'network.xyo.test',
      testArray: [1, 2, 3],
      testBoolean: true,
      testNull: null,
      testNullObject: { t: null },
      testNumber: 5,
      testObject: { t: 1 },
      testSomeNullObject: { s: 1, t: null },
      testString: 'hello there.  this is a pretty long string.  what do you think?',
    },
  },
  // Plugin payload-type vectors — exercise each Kotlin payload.types.* class
  // end-to-end. Kotlin constructs the equivalent data class and dataHash()
  // must match the JS-produced hash.
  { id: 'plugin-address', payload: { schema: 'network.xyo.address', address: '1234567890abcdef1234567890abcdef12345678', name: 'primary-signer' } },
  { id: 'plugin-config', payload: { schema: 'network.xyo.config', config: { timeout: 30, retries: 3, enabled: true, label: 'test-config' } } },
  { id: 'plugin-domain', payload: { schema: 'network.xyo.domain', domain: 'xyo.network', aliases: { primary: 'xyo.network', cdn: 'cdn.xyo.network' } } },
  { id: 'plugin-id', payload: { schema: 'network.xyo.id', salt: 'cross-sdk-id-vector' } },
  { id: 'plugin-schema', payload: { schema: 'network.xyo.schema', definition: { title: 'test', version: 1, fields: ['a', 'b', 'c'] } } },
  { id: 'plugin-value-number', payload: { schema: 'network.xyo.value', value: 42 } },
  { id: 'plugin-value-string', payload: { schema: 'network.xyo.value', value: 'hello-value-plugin' } },
  { id: 'plugin-value-object', payload: { schema: 'network.xyo.value', value: { key: 'nested', n: 99 } } },
  { id: 'array-of-numbers', payload: { numbers: [1, 2, 3, 4, 5], schema: 'network.xyo.test' } },
  { id: 'nested-arrays', payload: { matrix: [[1, 2], [3, 4]], schema: 'network.xyo.test' } },
  { id: 'unicode', payload: { schema: 'network.xyo.test', text: 'héllo wörld 你好' } },
  { id: 'booleans', payload: { bool_true: true, bool_false: false, schema: 'network.xyo.test' } },
  {
    id: 'id-payload-salt-7',
    payload: { salt: '7', schema: 'network.xyo.id' },
  },
  {
    id: 'location',
    payload: {
      schema: 'network.xyo.location.current',
      currentLocation: {
        coords: {
          accuracy: 5,
          altitude: 15,
          altitudeAccuracy: 15,
          heading: 90,
          latitude: 37.7749,
          longitude: -122.4194,
          speed: 2.5,
        },
        timestamp: 1_609_459_200_000,
      },
    },
  },
]

async function buildPayloadHashVectors() {
  const vectors = []
  for (const { id, payload } of payloadCases) {
    // dataHash strips $ and _ meta
    const dataHash = await PayloadBuilder.dataHash(payload)
    // hash strips only _ meta (client-meta $-prefixed fields retained)
    const hash = await ObjectHasher.hash(payload)
    vectors.push({
      id,
      raw_json: JSON.stringify(payload),
      data_hash: dataHash,
      hash,
    })
  }
  return vectors
}

// ---------- BoundWitness vectors ----------
// Full round-trip: build a BW with fixed signers and payloads, record the
// resulting addresses, payload_hashes, payload_schemas, previous_hashes,
// the canonical dataHash, and each signer's signature.

const boundWitnessCases = [
  {
    id: 'single-signer-single-payload',
    signers: [accountPrivateKeys[0]],
    payloads: [{ schema: 'network.xyo.test', value: 'hello' }],
  },
  {
    id: 'single-signer-two-payloads',
    signers: [accountPrivateKeys[0]],
    payloads: [
      { schema: 'network.xyo.test', value: 'hello' },
      { schema: 'network.xyo.id', salt: '42' },
    ],
  },
  {
    id: 'three-signers-two-payloads',
    signers: [accountPrivateKeys[0], accountPrivateKeys[1], accountPrivateKeys[2]],
    payloads: [
      { schema: 'network.xyo.test', value: 'hello' },
      { schema: 'network.xyo.id', salt: '42' },
    ],
  },
]

async function buildBoundWitnessVectors() {
  const vectors = []
  for (const { id, signers: signerKeys, payloads } of boundWitnessCases) {
    resetAccountCache()
    const signers = await Promise.all(signerKeys.map(k => Account.fromPrivateKey(k)))
    const builder = new BoundWitnessBuilder()
    signers.forEach(s => builder.signer(s))
    payloads.forEach(p => builder.payload(p))
    // Capture the dataHash before build() because signing mutates each signer.
    const dataHash = await builder.dataHash()
    const [signed] = await builder.build()
    vectors.push({
      id,
      signer_private_keys: signerKeys,
      payloads_raw_json: payloads.map(p => JSON.stringify(p)),
      addresses: signed.addresses,
      payload_hashes: signed.payload_hashes,
      payload_schemas: signed.payload_schemas,
      previous_hashes: signed.previous_hashes,
      data_hash: dataHash,
      signatures: signed.$signatures,
    })
  }
  return vectors
}

// ---------- XL1 amount vectors ----------
// Pure arithmetic: AttoXL1 is the base unit, every other denomination is
// 10^k atto. The Kotlin side uses BigInteger; JS uses bigint. Vectors are
// round-trip pairs: given an attoValue, what does each denomination read?

const xl1AmountCases = [
  { label: 'zero', atto: 0n },
  { label: 'one-atto', atto: 1n },
  { label: 'one-femto', atto: AttoXL1ConvertFactor.femto },
  { label: 'one-pico', atto: AttoXL1ConvertFactor.pico },
  { label: 'one-nano', atto: AttoXL1ConvertFactor.nano },
  { label: 'one-micro', atto: AttoXL1ConvertFactor.micro },
  { label: 'one-milli', atto: AttoXL1ConvertFactor.milli },
  { label: 'one-xl1', atto: AttoXL1ConvertFactor.xl1 },
  { label: 'ten-xl1', atto: 10n * AttoXL1ConvertFactor.xl1 },
  { label: 'thousand-xl1', atto: 1000n * AttoXL1ConvertFactor.xl1 },
  { label: 'fractional-nano', atto: 12345678900n },
  { label: 'lossy-divide', atto: 999n },
  { label: 'large', atto: (10n ** 30n) },
]

function buildXl1AmountVectors() {
  return xl1AmountCases.map(({ label, atto }) => ({
    label,
    atto: atto.toString(),
    femto: (atto / AttoXL1ConvertFactor.femto).toString(),
    pico: (atto / AttoXL1ConvertFactor.pico).toString(),
    nano: (atto / AttoXL1ConvertFactor.nano).toString(),
    micro: (atto / AttoXL1ConvertFactor.micro).toString(),
    milli: (atto / AttoXL1ConvertFactor.milli).toString(),
    xl1: (atto / AttoXL1ConvertFactor.xl1).toString(),
  }))
}

// ---------- Write it all out ----------

async function main() {
  const output = {
    generated_at: new Date().toISOString(),
    sources: {
      '@xyo-network/account': '5.3.30',
      '@xyo-network/boundwitness-builder': '5.3.30',
      '@xyo-network/hash': '5.3.30',
      '@xyo-network/payload-builder': '5.3.30',
      '@xyo-network/wallet': '5.3.30',
      '@xyo-network/xl1-protocol-model': '1.26.34',
    },
    message_hashes: messageHashes,
    accounts: await buildAccountVectors(),
    hd_wallets: await buildHdWalletVectors(),
    payload_hashes: await buildPayloadHashVectors(),
    bound_witnesses: await buildBoundWitnessVectors(),
    xl1_amounts: buildXl1AmountVectors(),
  }

  const serialized = JSON.stringify(output, null, 2) + '\n'
  for (const outputPath of outputPaths) {
    mkdirSync(dirname(outputPath), { recursive: true })
    writeFileSync(outputPath, serialized)
    console.log(`Wrote ${outputPath}`)
  }
  console.log(`  accounts: ${output.accounts.length}`)
  console.log(`  hd_wallets: ${output.hd_wallets.reduce((n, w) => n + w.derivations.length, 0)} derivations`)
  console.log(`  payload_hashes: ${output.payload_hashes.length}`)
  console.log(`  bound_witnesses: ${output.bound_witnesses.length}`)
  console.log(`  xl1_amounts: ${output.xl1_amounts.length}`)
}

main().catch((err) => {
  console.error(err)
  process.exit(1)
})
