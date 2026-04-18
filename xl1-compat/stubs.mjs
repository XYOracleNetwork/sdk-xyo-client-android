// Minimal in-memory stubs for XL1 viewers/runners. Deterministic enough that
// Kotlin live tests can assert exact values. Shape-compatible with the JS
// handler factories — duck-typed to the methods each factory requires.
//
// Deliberately self-contained (no import of xl1-protocol's test fixture file)
// so this compat harness doesn't depend on an internal test path that may
// move or change.

const MS_PER_BLOCK = 60_000
const BASE_EPOCH = 1_700_000_000_000
export const HEAD_BLOCK_NUMBER = 200_000
export const CHAIN_ID = 'c5fe2e6f6841cbab12d8c0618be2df8c6156cc44'
export const STUB_ADDRESS = 'f93c0cff2245a7776792efeb4b229044cea4ec06'
export const STUB_SIGNATURE = '0'.repeat(128)

const hashForBlock = (n) => n.toString(16).padStart(64, '0')
const dataHashForBlock = (n) => `d${n.toString(16).padStart(63, '0')}`
const epochForBlock = (n) => BASE_EPOCH + n * MS_PER_BLOCK

function createSyntheticBlock(blockNumber) {
  const bw = {
    $epoch: epochForBlock(blockNumber),
    $signatures: [STUB_SIGNATURE],
    addresses: [STUB_ADDRESS],
    block: blockNumber,
    chain: CHAIN_ID,
    payload_hashes: [],
    payload_schemas: [],
    previous: blockNumber > 0 ? hashForBlock(blockNumber - 1) : null,
    previous_hashes: [blockNumber > 0 ? hashForBlock(blockNumber - 1) : null],
    schema: 'network.xyo.boundwitness',
    step_hashes: [],
    _hash: hashForBlock(blockNumber),
    _dataHash: dataHashForBlock(blockNumber),
  }
  // SignedHydratedBlockWithHashMeta is a tuple [bw, payloads]
  return [bw, []]
}

export class StubBlockViewer {
  moniker = 'BlockViewer'

  async blocksByHash(hash, limit = 50) {
    const start = Number.parseInt(hash, 16)
    if (Number.isNaN(start)) return []
    const out = []
    for (let i = 0; i < limit && start - i >= 0; i++) out.push(createSyntheticBlock(start - i))
    return out
  }

  async blocksByNumber(blockNumber, limit = 50) {
    const out = []
    for (let i = 0; i < limit && blockNumber - i >= 0; i++) out.push(createSyntheticBlock(blockNumber - i))
    return out
  }

  async currentBlock() {
    return createSyntheticBlock(HEAD_BLOCK_NUMBER)
  }

  async payloadsByHash(_hashes) {
    return []
  }
}

export class StubFinalizationViewer {
  moniker = 'FinalizationViewer'

  async head() {
    // Stub head is the block N-6 (simple "finalized" offset)
    return createSyntheticBlock(HEAD_BLOCK_NUMBER - 6)
  }
}

// Stable reference values used by all TimeSync responses so tests can assert.
// Domain values are TimeDomain: 'xl1' | 'epoch' | 'ethereum'.
export const TIME_SYNC_EPOCH_MS = BASE_EPOCH + HEAD_BLOCK_NUMBER * MS_PER_BLOCK
export const TIME_SYNC_XL1_BLOCK = HEAD_BLOCK_NUMBER
export const TIME_SYNC_ETHEREUM_BLOCK = 1_000_000
const ETH_MS_PER_BLOCK = 12_000
const ETH_BASE_EPOCH = TIME_SYNC_EPOCH_MS - TIME_SYNC_ETHEREUM_BLOCK * ETH_MS_PER_BLOCK

function valueForDomain(domain) {
  if (domain === 'epoch') return TIME_SYNC_EPOCH_MS
  if (domain === 'xl1') return TIME_SYNC_XL1_BLOCK
  if (domain === 'ethereum') return TIME_SYNC_ETHEREUM_BLOCK
  throw new Error(`unknown TimeDomain ${domain}`)
}

export class StubTimeSyncViewer {
  moniker = 'TimeSyncViewer'

  async convertTime(from, to, value) {
    if (from === to) return value
    // Everything funnels through epoch as the common denominator.
    let epoch
    if (from === 'epoch') epoch = value
    else if (from === 'xl1') epoch = BASE_EPOCH + value * MS_PER_BLOCK
    else if (from === 'ethereum') epoch = ETH_BASE_EPOCH + value * ETH_MS_PER_BLOCK
    else throw new Error(`unknown from domain ${from}`)

    if (to === 'epoch') return epoch
    if (to === 'xl1') return Math.floor((epoch - BASE_EPOCH) / MS_PER_BLOCK)
    if (to === 'ethereum') return Math.floor((epoch - ETH_BASE_EPOCH) / ETH_MS_PER_BLOCK)
    throw new Error(`unknown to domain ${to}`)
  }

  async currentTime(domain) {
    return [domain, valueForDomain(domain)]
  }

  async currentTimeAndHash(domain) {
    return [valueForDomain(domain), hashForBlock(TIME_SYNC_XL1_BLOCK)]
  }

  async currentTimePayload() {
    return {
      schema: 'network.xyo.timestamp',
      epoch: TIME_SYNC_EPOCH_MS,
      xl1: TIME_SYNC_XL1_BLOCK,
      ethereum: TIME_SYNC_ETHEREUM_BLOCK,
    }
  }
}

export class StubMempoolViewer {
  moniker = 'MempoolViewer'
  _pendingTransactions = []
  _pendingBlocks = []

  async pendingTransactions(_options) {
    return this._pendingTransactions
  }

  async pendingBlocks(_options) {
    return this._pendingBlocks
  }
}

export class StubDataLakeViewer {
  moniker = 'DataLakeViewer'

  // Preloaded deterministic payloads so `get` has something to return for
  // a known set of hashes. The hash-to-payload map is a stand-in for real
  // storage — live tests assert exact output for the two seeded hashes.
  _store = new Map([
    [
      '5d185be39c900cd03ba18d4bfeb91ae1d00400749b19bbb7651ffe15771bfc97',
      { schema: 'network.xyo.test', value: 'hello' },
    ],
    [
      '770b6ca959389c0da23ac969d1d6288c8e96542ce43570a652a2b1f5e4c9759d',
      { schema: 'network.xyo.id', salt: '42' },
    ],
  ])

  async get(hashes) {
    if (!Array.isArray(hashes)) return []
    return hashes.map((h) => this._store.get(h)).filter(Boolean)
  }

  async next(_options) {
    // Return both seeded payloads in insertion order.
    return [...this._store.values()]
  }
}

// Deterministic rewards data. JS wire format sends bigint as 0x-hex string.
// The stub returns bigint and relies on the rpc-server's schema transform
// (result.to: BigIntToHexPipe) to stringify.
function rewardsDomain(baseEarned, baseClaimed) {
  const earned = BigInt(baseEarned)
  const claimed = BigInt(baseClaimed)
  const bonus = earned / 10n
  const unclaimed = earned - claimed
  const total = earned + bonus
  return { bonus, claimed, earned, total, unclaimed }
}

function withKeyedTranche(key, value) {
  // The rewards viewers return Record<K, bigint>. For stub purposes, each
  // tranche is a single-entry map so the Kotlin test can read it back.
  return { [key]: value }
}

class StubRewardsViewer {
  constructor(key) {
    this._key = key
    this._values = rewardsDomain(1_000_000_000_000_000_000n, 400_000_000_000_000_000n)
  }
  async bonus(_opts) { return withKeyedTranche(this._key, this._values.bonus) }
  async claimed(_opts) { return withKeyedTranche(this._key, this._values.claimed) }
  async earned(_opts) { return withKeyedTranche(this._key, this._values.earned) }
  async total(_opts) { return withKeyedTranche(this._key, this._values.total) }
  async unclaimed(_opts) { return withKeyedTranche(this._key, this._values.unclaimed) }
}

// Numeric-key dimensions (position, step, "total"). Key is coerced to string
// when JSON-serialized so the wire form is { "42": "0x..." }.
export class StubRewardsByPositionViewer extends StubRewardsViewer {
  moniker = 'NetworkStakeStepRewardsByPositionViewer'
  constructor() { super(42) }
}
export class StubRewardsByStepViewer extends StubRewardsViewer {
  moniker = 'NetworkStakeStepRewardsByStepViewer'
  constructor() { super(17) }
}
export class StubRewardsTotalViewer extends StubRewardsViewer {
  moniker = 'NetworkStakeStepRewardsTotalViewer'
  constructor() { super(0) }
}
export class StubRewardsByStakerViewer extends StubRewardsViewer {
  moniker = 'NetworkStakeStepRewardsByStakerViewer'
  constructor() { super(STUB_ADDRESS) }
}

export class StubMempoolRunner {
  moniker = 'MempoolRunner'

  constructor(mempoolViewer) {
    this.viewer = mempoolViewer
  }

  async submitTransactions(transactions) {
    // Derive a fake hash per transaction: sha-like `tx` + index + length
    const hashes = transactions.map((tx, i) => {
      const idx = i.toString(16).padStart(4, '0')
      const base = (tx?.[0]?._hash ?? hashForBlock(0)).toString()
      return (`${base.slice(0, 60)}${idx}`).padStart(64, '0').slice(-64)
    })
    this.viewer._pendingTransactions.push(...transactions)
    return hashes
  }

  async submitBlocks(blocks) {
    const hashes = blocks.map((block, i) => {
      const idx = i.toString(16).padStart(4, '0')
      const base = (block?.[0]?._hash ?? hashForBlock(0)).toString()
      return (`${base.slice(0, 60)}${idx}`).padStart(64, '0').slice(-64)
    })
    this.viewer._pendingBlocks.push(...blocks)
    return hashes
  }
}
