package network.xyo.boundwitness
import network.xyo.payload.IPayload

class QueryBoundWitnessBuilder(query: IPayload) : AbstractBoundWitnessBuilder<QueryBoundWitness, QueryBoundWitnessBuilder>() {
    private var queryHash: String

    init {
        this.queryHash = query.hash()
    }

    fun query(query: IPayload): QueryBoundWitnessBuilder {
        this.queryHash = query.hash()
        this.payload(query)
        return this
    }

    override fun build(): QueryBoundWitness {
        val bw = QueryBoundWitness(queryHash)
        bw.put("query", queryHash)
        this.setFields(bw)
        return bw
    }
}