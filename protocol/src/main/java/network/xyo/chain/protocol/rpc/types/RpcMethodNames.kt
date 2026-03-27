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

    // TransferBalanceViewer
    const val TRANSFER_BALANCE_VIEWER_BALANCE = "transferBalanceViewer_transferBalance"
    const val TRANSFER_BALANCE_VIEWER_HISTORY = "transferBalanceViewer_transferBalanceHistory"
    const val TRANSFER_BALANCE_VIEWER_BALANCES = "transferBalanceViewer_transferBalances"
    const val TRANSFER_BALANCE_VIEWER_PAIR_BALANCE = "transferBalanceViewer_transferPairBalance"
    const val TRANSFER_BALANCE_VIEWER_PAIR_HISTORY = "transferBalanceViewer_transferPairBalanceHistory"
    const val TRANSFER_BALANCE_VIEWER_PAIR_BALANCES = "transferBalanceViewer_transferPairBalances"

    // StakeTotalsViewer
    const val STAKE_TOTALS_VIEWER_ACTIVE = "stakeTotalsViewer_active"
    const val STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKED = "stakeTotalsViewer_activeByStaked"
    const val STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKER = "stakeTotalsViewer_activeByStaker"
    const val STAKE_TOTALS_VIEWER_PENDING = "stakeTotalsViewer_pending"
    const val STAKE_TOTALS_VIEWER_PENDING_BY_STAKER = "stakeTotalsViewer_pendingByStaker"
    const val STAKE_TOTALS_VIEWER_WITHDRAWN = "stakeTotalsViewer_withdrawn"
    const val STAKE_TOTALS_VIEWER_WITHDRAWN_BY_STAKER = "stakeTotalsViewer_withdrawnByStaker"

    // BlockRewardViewer
    const val BLOCK_REWARD_VIEWER_ALLOWED_REWARD = "blockRewardViewer_allowedRewardForBlock"

    // StakeRunner
    const val STAKE_RUNNER_ADD_STAKE = "stakeRunner_addStake"
    const val STAKE_RUNNER_REMOVE_STAKE = "stakeRunner_removeStake"
    const val STAKE_RUNNER_WITHDRAW_STAKE = "stakeRunner_withdrawStake"

    // NetworkStake StepViewer
    const val NETWORK_STAKE_STEP_POSITION_COUNT = "networkStakeStepViewer_positionCount"
    const val NETWORK_STAKE_STEP_POSITIONS = "networkStakeStepViewer_positions"
    const val NETWORK_STAKE_STEP_RANDOMIZER = "networkStakeStepViewer_randomizer"
    const val NETWORK_STAKE_STEP_STAKE = "networkStakeStepViewer_stake"
    const val NETWORK_STAKE_STEP_STAKER_COUNT = "networkStakeStepViewer_stakerCount"
    const val NETWORK_STAKE_STEP_STAKERS = "networkStakeStepViewer_stakers"
    const val NETWORK_STAKE_STEP_WEIGHT = "networkStakeStepViewer_weight"

    // NetworkStake StepRewardsViewer
    const val NETWORK_STAKE_REWARDS_BY_POSITION = "networkStakeStepRewardsViewer_byPosition"
    const val NETWORK_STAKE_REWARDS_BY_STAKER = "networkStakeStepRewardsViewer_byStaker"
    const val NETWORK_STAKE_REWARDS_BY_STEP = "networkStakeStepRewardsViewer_byStep"
    const val NETWORK_STAKE_REWARDS_TOTAL = "networkStakeStepRewardsViewer_total"

    // ForkViewer
    const val FORK_VIEWER_FORK_HISTORY = "forkViewer_forkHistory"
}
