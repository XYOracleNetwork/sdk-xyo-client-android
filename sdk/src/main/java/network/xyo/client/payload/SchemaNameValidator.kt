package network.xyo.client.payload

/**
 * Validates XYO schema names, matching JS SchemaNameValidator.
 *
 * Schema names must be:
 * - Lowercase
 * - Dot-separated with 3+ levels (e.g. "network.xyo.example")
 * - Contain only alphanumeric characters, dots, and hyphens
 */
class SchemaNameValidator(val schema: String?) {

    val parts: List<String>?
        get() = schema?.split(".")

    val levels: Int?
        get() = parts?.size

    val isLowercase: Boolean
        get() = schema == schema?.lowercase()

    /**
     * The root domain (first two parts reversed).
     * e.g. "network.xyo.example" -> "xyo.network"
     */
    val rootDomain: String?
        get() {
            val p = parts ?: return null
            if (p.size < 2) return null
            return "${p[1]}.${p[0]}"
        }

    /**
     * Run all static validations. Returns a list of errors (empty = valid).
     */
    fun all(): List<Error> {
        val errors = mutableListOf<Error>()

        if (schema == null || schema.isBlank()) {
            errors.add(Error("schema is missing"))
            return errors
        }

        if (!isLowercase) {
            errors.add(Error("schema must be lowercase: $schema"))
        }

        val lvl = levels
        if (lvl != null && lvl < 3) {
            errors.add(Error("schema must have at least 3 dot-separated levels: $schema"))
        }

        if (!SCHEMA_PATTERN.matches(schema)) {
            errors.add(Error("schema contains invalid characters: $schema"))
        }

        return errors
    }

    companion object {
        private val SCHEMA_PATTERN = Regex("^[a-z0-9][a-z0-9.-]*[a-z0-9]$")

        fun isValid(schema: String?): Boolean {
            return SchemaNameValidator(schema).all().isEmpty()
        }
    }
}
