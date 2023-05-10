package network.xyo.client.boundwitness

import network.xyo.client.address.Account
import network.xyo.client.payload.Payload
import network.xyo.client.payload.PayloadValidationException

abstract class AbstractBoundWitnessBuilder<TBoundWitness: BoundWitness, This: AbstractBoundWitnessBuilder<TBoundWitness, This>> {
    protected var _accounts: MutableList<Account> = mutableListOf()
    protected var _payloads: MutableList<Payload> = mutableListOf()

    abstract fun createInstance(): TBoundWitness

    val payloadHashes: List<String>
        get() {
        return this._payloads.map { payload ->
            payload.hash()
        }
    }

    val payloadSchemas: List<String>
        get() {
            return this._payloads.map { payload ->
                payload.schema
            }
        }

    val _addresses: List<String>
        get() {
            return this._accounts.map { account -> account.address.hex }
        }

    val previousHashes: List<String?>
        get() {
            return this._accounts.map { account -> account.previousHash }
        }

    @Throws(PayloadValidationException::class)
    fun <T: Payload>payload(payload: T): This {
        _payloads.add(payload)
        return this as This
    }

    @Throws(PayloadValidationException::class)
    fun payloads(payloads: Set<Payload>?): This {
        payloads?.forEach { payload ->
            payload(payload)
        }
        return this as This
    }

    @Throws(PayloadValidationException::class)
    fun witness(account: Account?): This {
        if (account != null) {
            _accounts.add(account)
        }
        return this as This
    }

    @Throws(PayloadValidationException::class)
    fun witnesses(accounts: Set<Account>): This {
        accounts.forEach { account ->
            witness(account)
        }
        return this as This
    }

    open fun build(): TBoundWitness {
        val bw = this.createInstance()
        this.setFields(bw)
        return bw
    }

    private fun setFields(bw: TBoundWitness): Pair<TBoundWitness,  Set<Payload>> {
        bw.addresses = this._addresses
        bw.payload_hashes = this.payloadHashes
        bw.payload_schemas = this.payloadSchemas
        bw.previous_hashes = this.previousHashes
        val hash = bw.hash()
        bw._signatures = this._accounts.map {account -> account.sign(hash)}
        return Pair(bw, this._payloads.toSet())
    }
}