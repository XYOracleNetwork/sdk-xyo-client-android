package network.xyo.client.boundwitness

open class QueryBoundWitness(schema: String = BoundWitness.schema): BoundWitness(schema) {

    var query: String
        get() {
            return getString("query")
        }

        set(value) {
            this.put("query", value )
        }
}