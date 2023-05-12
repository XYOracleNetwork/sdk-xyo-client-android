package network.xyo.boundwitness
import network.xyo.payload.Payload

class QueryBoundWitnessBuilder : AbstractBoundWitnessBuilder<QueryBoundWitness, QueryBoundWitnessBuilder>() {
    private lateinit var queryHash: String

    override fun createInstance(): QueryBoundWitness {
        return QueryBoundWitness()
    }

    fun query(query: Payload): QueryBoundWitnessBuilder {
        this.queryHash = query.hash()
        this.payload(query)
        return this
    }

    override fun build(): QueryBoundWitness {
        val bw = super.build()
        bw.put("query", queryHash)
        return bw
    }
}