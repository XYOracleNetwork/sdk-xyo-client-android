package network.xyo.chain.protocol.rpc.types

object RpcMethodNames {
    // BlockViewer
    const val BLOCK_VIEWER_BLOCKS_BY_HASH = "blockViewer_blocksByHash"
    const val BLOCK_VIEWER_BLOCKS_BY_NUMBER = "blockViewer_blocksByNumber"
    const val BLOCK_VIEWER_CURRENT_BLOCK = "blockViewer_currentBlock"
    const val BLOCK_VIEWER_PAYLOADS_BY_HASH = "blockViewer_payloadsByHash"

    // TransactionViewer
    const val TX_VIEWER_BY_HASH = "transactionViewer_byHash"

    // AccountBalanceViewer
    const val ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES = "accountBalanceViewer_qualifiedAccountBalances"
    const val ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES = "accountBalanceViewer_qualifiedAccountBalanceHistories"

    // StakeViewer
    const val STAKE_VIEWER_BY_ID = "stakeViewer_stakeById"
    const val STAKE_VIEWER_BY_STAKER = "stakeViewer_stakeByStaker"
    const val STAKE_VIEWER_BY_STAKED = "stakeViewer_stakesByStaked"
    const val STAKE_VIEWER_STAKES_BY_STAKER = "stakeViewer_stakesByStaker"
    const val STAKE_VIEWER_MIN_WITHDRAWAL_BLOCKS = "stakeViewer_minWithdrawalBlocks"
    const val STAKE_VIEWER_REWARDS_CONTRACT = "stakeViewer_rewardsContract"
    const val STAKE_VIEWER_STAKING_TOKEN_ADDRESS = "stakeViewer_stakingTokenAddress"

    // MempoolViewer
    const val MEMPOOL_VIEWER_PENDING_TRANSACTIONS = "mempoolViewer_pendingTransactions"
    const val MEMPOOL_VIEWER_PENDING_BLOCKS = "mempoolViewer_pendingBlocks"

    // MempoolRunner
    const val MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS = "mempoolRunner_submitTransactions"
    const val MEMPOOL_RUNNER_SUBMIT_BLOCKS = "mempoolRunner_submitBlocks"

    // FinalizationViewer
    const val FINALIZATION_VIEWER_HEAD = "finalizationViewer_head"

    // TimeSyncViewer
    const val TIME_SYNC_VIEWER_CONVERT_TIME = "timeSyncViewer_convertTime"
    const val TIME_SYNC_VIEWER_CURRENT_TIME = "timeSyncViewer_currentTime"
    const val TIME_SYNC_VIEWER_CURRENT_TIME_AND_HASH = "timeSyncViewer_currentTimeAndHash"
    const val TIME_SYNC_VIEWER_CURRENT_TIME_PAYLOAD = "timeSyncViewer_currentTimePayload"

    // NetworkStakeViewer
    const val NETWORK_STAKE_VIEWER_ACTIVE = "networkStakeViewer_active"
}
