package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.block.BlockBoundWitness

fun interface BlockValidator {
    fun validate(block: BlockBoundWitness): List<ValidationError>
}

fun validateBlock(block: BlockBoundWitness, validators: List<BlockValidator>): List<ValidationError> {
    return validators.flatMap { it.validate(block) }
}

class BlockCumulativeBalanceValidator : BlockValidator {
    override fun validate(block: BlockBoundWitness): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (block.block < 0) {
            errors.add(ValidationError("INVALID_BLOCK_NUMBER", "Block number must be non-negative"))
        }

        if (block.chain.isBlank()) {
            errors.add(ValidationError("MISSING_CHAIN", "Block must specify a chain"))
        }

        if (block.addresses.isEmpty()) {
            errors.add(ValidationError("MISSING_ADDRESSES", "Block must have at least one signer address"))
        }

        return errors
    }
}
