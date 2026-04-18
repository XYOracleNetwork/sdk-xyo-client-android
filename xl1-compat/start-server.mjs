#!/usr/bin/env node
// Local JSON-RPC server used by Kotlin live-compatibility tests. Wires
// handler factories from @xyo-network/xl1-rpc to in-memory stub viewers,
// then exposes them via @xyo-network/xl1-rpc-server's createRpcHttpServer.
//
// Matches the pattern in xl1-protocol/test/setup/api-local.ts but is
// self-contained (doesn't depend on that test fixture directory).
//
// Usage:
//   node start-server.mjs [--port 18080]
//   XL1_RPC_PORT=18080 node start-server.mjs
//
// Prints "READY <port>" to stdout once listening — the Kotlin test harness
// parses that to know when to start firing requests.

import { JsonRpcEngineV2 } from '@metamask/json-rpc-engine/v2'
import {
  AllRpcSchemas,
  createRequestSchema,
  requestSchemas,
  rpcMethodHandlersFromBlockViewer,
  rpcMethodHandlersFromFinalizationViewer,
  rpcMethodHandlersFromMempoolRunner,
  rpcMethodHandlersFromMempoolViewer,
  rpcMethodHandlersFromNetworkStakingStepRewardsByPositionViewer,
  rpcMethodHandlersFromTimeSyncViewer,
} from '@xyo-network/xl1-rpc'
import { createRpcHttpServer } from '@xyo-network/xl1-rpc-server'

import {
  StubBlockViewer,
  StubDataLakeViewer,
  StubFinalizationViewer,
  StubMempoolRunner,
  StubMempoolViewer,
  StubRewardsByPositionViewer,
  StubTimeSyncViewer,
} from './stubs.mjs'

function parsePort() {
  const argIdx = process.argv.indexOf('--port')
  if (argIdx !== -1 && process.argv[argIdx + 1]) return Number(process.argv[argIdx + 1])
  if (process.env.XL1_RPC_PORT) return Number(process.env.XL1_RPC_PORT)
  return 18_080
}

// DataLake has no dedicated rpcMethodHandlersFromDataLakeViewer factory in
// the published @xyo-network/xl1-rpc package yet, so we hand-roll the two
// handlers here. The method strings mirror DataLakeViewerRpcMethodName.
function rpcMethodHandlersFromDataLakeViewer(viewer) {
  return {
    dataLakeViewer_get: (params) => viewer.get(...params),
    dataLakeViewer_next: (params) => viewer.next(...params),
  }
}

async function main() {
  const blockViewer = new StubBlockViewer()
  const finalizationViewer = new StubFinalizationViewer()
  const timeSyncViewer = new StubTimeSyncViewer()
  const mempoolViewer = new StubMempoolViewer()
  const mempoolRunner = new StubMempoolRunner(mempoolViewer)
  const dataLakeViewer = new StubDataLakeViewer()
  const rewardsByPositionViewer = new StubRewardsByPositionViewer()

  const handlers = {
    ...rpcMethodHandlersFromBlockViewer(blockViewer),
    ...rpcMethodHandlersFromFinalizationViewer(finalizationViewer),
    ...rpcMethodHandlersFromTimeSyncViewer(timeSyncViewer),
    ...rpcMethodHandlersFromMempoolViewer(mempoolViewer),
    ...rpcMethodHandlersFromMempoolRunner(mempoolRunner),
    ...rpcMethodHandlersFromDataLakeViewer(dataLakeViewer),
    ...rpcMethodHandlersFromNetworkStakingStepRewardsByPositionViewer(rewardsByPositionViewer),
  }

  const middleware = async ({ request }) => {
    const method = request.method
    const handler = handlers[method]
    const schema = AllRpcSchemas[method]

    if (schema === undefined || handler === undefined) {
      throw new Error(`Method not found: ${method}`)
    }

    let requestSchema = requestSchemas[method]
    if (requestSchema === undefined) {
      requestSchema = createRequestSchema(method, schema.params.from)
      requestSchemas[method] = requestSchema
    }

    const parsed = requestSchema.safeParse(request)
    if (!parsed.success) {
      throw new Error(parsed.error.message)
    }

    const { params } = parsed.data
    const result = await handler(params)
    return schema.result.to.parse(result)
  }

  const engine = JsonRpcEngineV2.create({ middleware: [middleware] })
  const port = parsePort()
  const server = await createRpcHttpServer({ engine, port })

  console.log(`READY ${server.port}`)

  const shutdown = async (signal) => {
    console.error(`received ${signal}, shutting down`)
    try {
      await server.close()
      process.exit(0)
    } catch (err) {
      console.error('shutdown error', err)
      process.exit(1)
    }
  }

  process.on('SIGINT', () => shutdown('SIGINT'))
  process.on('SIGTERM', () => shutdown('SIGTERM'))
}

main().catch((err) => {
  console.error(err)
  process.exit(1)
})
