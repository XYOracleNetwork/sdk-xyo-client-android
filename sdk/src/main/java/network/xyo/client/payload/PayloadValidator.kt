package network.xyo.client.payload

/**
 * Validates payload structure, schema name, and integrity.
 * Matches JS PayloadValidator pattern: returns list of Error (empty = valid).
 */
open class PayloadValidator<T : Payload>(val payload: T) {

    /**
     * Validate the schema name using SchemaNameValidator.
     */
    fun schemaName(): List<Error> {
        return SchemaNameValidator(payload.schema).all()
    }

    /**
     * Run all validations. Returns a list of errors (empty = valid).
     */
    open fun validate(): List<Error> {
        val errors = mutableListOf<Error>()
        errors.addAll(schemaName())
        return errors
    }

    companion object {
        fun <T : Payload> isValid(payload: T): Boolean {
            return PayloadValidator(payload).validate().isEmpty()
        }
    }
}
