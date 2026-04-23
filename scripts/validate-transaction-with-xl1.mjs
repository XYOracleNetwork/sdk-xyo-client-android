#!/usr/bin/env node

import { readFile } from 'node:fs/promises'
import path from 'node:path'
import process from 'node:process'
import { pathToFileURL } from 'node:url'

const [, , inputPath] = process.argv

if (!inputPath) {
  console.error('usage: node scripts/validate-transaction-with-xl1.mjs <input.json>')
  process.exit(1)
}

const xl1Repo = process.env.XL1_PROTOCOL_REPO ?? '/Users/arietrouw/GitHub/XYOracleNetwork/xl1-protocol'
const validationModulePath = path.join(
  xl1Repo,
  'packages/protocol/packages/validation/dist/neutral/index.mjs',
)

const { TransactionTransfersValidatorFactory, validateTransaction } = await import(
  pathToFileURL(validationModulePath).href
)

const input = JSON.parse(await readFile(inputPath, 'utf8'))

const errors = await validateTransaction(
  input.context ?? {},
  input.transaction,
  [TransactionTransfersValidatorFactory()],
)

const normalizedErrors = errors.map(error => ({
  message: error?.message ?? String(error),
}))

process.stdout.write(`${JSON.stringify({ errors: normalizedErrors }, null, 2)}\n`)
