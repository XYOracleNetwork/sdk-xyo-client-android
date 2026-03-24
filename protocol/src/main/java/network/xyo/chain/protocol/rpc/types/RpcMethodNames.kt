package network.xyo.chain.protocol.rpc.types

object RpcMethodNames {
    // BlockViewer
    const val BLOCK_VIEWER_BLOCK_BY_HASH = "blockViewer_blockByHash"
    const val BLOCK_VIEWER_BLOCK_BY_NUMBER = "blockViewer_blockByNumber"
    const val BLOCK_VIEWER_BLOCKS_BY_HASH = "blockViewer_blocksByHash"
    const val BLOCK_VIEWER_BLOCKS_BY_NUMBER = "blockViewer_blocksByNumber"
    const val BLOCK_VIEWER_CURRENT_BLOCK = "blockViewer_currentBlock"
    const val BLOCK_VIEWER_CURRENT_BLOCK_HASH = "blockViewer_currentBlockHash"
    const val BLOCK_VIEWER_CURRENT_BLOCK_NUMBER = "blockViewer_currentBlockNumber"
    const val BLOCK_VIEWER_CHAIN_ID = "blockViewer_chainId"
    const val BLOCK_VIEWER_PAYLOAD_BY_HASH = "blockViewer_payloadByHash"
    const val BLOCK_VIEWER_PAYLOADS_BY_HASH = "blockViewer_payloadsByHash"
    const val BLOCK_VIEWER_RATE = "blockViewer_rate"

    // TransactionViewer
    const val TX_VIEWER_BY_HASH = "transactionViewer_byHash"
    const val TX_VIEWER_BY_BLOCK_HASH_AND_INDEX = "transactionViewer_transactionByBlockHashAndIndex"
    const val TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX = "transactionViewer_transactionByBlockNumberAndIndex"
    const val TX_VIEWER_TX_BY_HASH = "transactionViewer_transactionByHash"

    // AccountBalanceViewer
    const val ACCOUNT_BALANCE_VIEWER_BALANCE = "accountBalanceViewer_accountBalance"
    const val ACCOUNT_BALANCE_VIEWER_BALANCES = "accountBalanceViewer_accountBalances"
    const val ACCOUNT_BALANCE_VIEWER_HISTORY = "accountBalanceViewer_accountBalanceHistory"
    const val ACCOUNT_BALANCE_VIEWER_HISTORIES = "accountBalanceViewer_accountBalanceHistories"
    const val ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES = "accountBalanceViewer_qualifiedAccountBalances"
    const val ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES = "accountBalanceViewer_qualifiedAccountBalanceHistories"

    // StakeViewer
    const val STAKE_VIEWER_BY_ID = "stakeViewer_stakeById"
    const val STAKE_VIEWER_BY_STAKER = "stakeViewer_stakeByStaker"
    const val STAKE_VIEWER_BY_STAKED = "stakeViewer_stakesByStaked"
    const val STAKE_VIEWER_STAKES_BY_STAKER = "stakeViewer_stakesByStaker"
    const val STAKE_VIEWER_ACTIVE = "stakeViewer_activeStakes"
    const val STAKE_VIEWER_REMOVED = "stakeViewer_removedStakes"
    const val STAKE_VIEWER_WITHDRAWN = "stakeViewer_withdrawnStakes"

    // StakeTotalsViewer
    const val STAKE_TOTALS_VIEWER_ACTIVE = "stakeTotalsViewer_active"
    const val STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKED = "stakeTotalsViewer_activeByStaked"
    const val STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKER = "stakeTotalsViewer_activeByStaker"
    const val STAKE_TOTALS_VIEWER_PENDING = "stakeTotalsViewer_pending"
    const val STAKE_TOTALS_VIEWER_WITHDRAWN = "stakeTotalsViewer_withdrawn"

    // MempoolViewer
    const val MEMPOOL_VIEWER_PENDING_TRANSACTIONS = "mempoolViewer_pendingTransactions"
    const val MEMPOOL_VIEWER_PENDING_BLOCKS = "mempoolViewer_pendingBlocks"

    // MempoolRunner
    const val MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS = "mempoolRunner_submitTransactions"
    const val MEMPOOL_RUNNER_SUBMIT_BLOCKS = "mempoolRunner_submitBlocks"

    // FinalizationViewer
    const val FINALIZATION_VIEWER_HEAD = "finalizationViewer_head"
    const val FINALIZATION_VIEWER_HEAD_BLOCK = "finalizationViewer_headBlock"
    const val FINALIZATION_VIEWER_HEAD_HASH = "finalizationViewer_headHash"
    const val FINALIZATION_VIEWER_HEAD_NUMBER = "finalizationViewer_headNumber"
    const val FINALIZATION_VIEWER_CHAIN_ID = "finalizationViewer_chainId"

    // TimeSyncViewer
    const val TIME_SYNC_VIEWER_SERVER_TIME = "timeSyncViewer_serverTime"

    // NetworkStakeViewer
    const val NETWORK_STAKE_VIEWER_TOTAL_STAKE = "networkStakeViewer_totalStake"
    const val NETWORK_STAKE_VIEWER_POSITION_COUNT = "networkStakeViewer_positionCount"

    // BlockRewardViewer
    const val BLOCK_REWARD_VIEWER_ALLOWED_REWARD = "blockRewardViewer_allowedRewardForBlock"
}
