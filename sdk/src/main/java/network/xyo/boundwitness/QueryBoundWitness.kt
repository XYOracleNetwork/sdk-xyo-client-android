package network.xyo.boundwitness

open class QueryBoundWitness(schema: String = Companion.schema): BoundWitness(schema) {

    var query: String
        get() {
            return getString("query")
        }

        set(value) {
            this.put("query", value )
        }
}