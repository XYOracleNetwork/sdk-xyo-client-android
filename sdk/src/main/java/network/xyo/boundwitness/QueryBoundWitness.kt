package network.xyo.boundwitness

open class QueryBoundWitness(query: String, schema: String = Companion.schema): JSONBoundWitness(schema) {

    init {
        this.put("query", query )
    }

    var query: String
        get() {
            return getString("query")
        }

        set(value) {
            this.put("query", value )
        }
}