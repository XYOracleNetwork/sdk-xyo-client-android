package network.xyo.chain.protocol.model

typealias LocalSequence = String
typealias QualifiedSequence = String

object SequenceValidator {
    private val hexRegex = Regex("^[0-9a-f]+$")

    fun isValidLocalSequence(value: String): Boolean =
        hexRegex.matches(value.lowercase())

    fun isValidQualifiedSequence(value: String): Boolean =
        hexRegex.matches(value.lowercase())
}
