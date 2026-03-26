package network.xyo.client.huri

import network.xyo.client.payload.Payload

/**
 * Hash-based Universal Resource Identifier (HURI), matching JS @xyo-network/huri.
 *
 * A HURI is a way to reference a payload by its hash, optionally with
 * an archivist address for resolution.
 *
 * Format: `<hash>` or `<archivistAddress>/<hash>`
 */
class Huri(val href: String) {

    /**
     * The hash portion of the HURI.
     */
    val hash: String
        get() {
            val parts = href.split("/")
            return parts.last()
        }

    /**
     * The optional archivist address portion of the HURI.
     */
    val archivistAddress: String?
        get() {
            val parts = href.split("/")
            return if (parts.size > 1) parts.first() else null
        }

    override fun toString(): String = href

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Huri) return false
        return href == other.href
    }

    override fun hashCode(): Int = href.hashCode()

    companion object {
        /**
         * Create a HURI from a hash.
         */
        fun fromHash(hash: String): Huri = Huri(hash)

        /**
         * Create a HURI from an archivist address and hash.
         */
        fun fromArchivistAndHash(archivistAddress: String, hash: String): Huri =
            Huri("$archivistAddress/$hash")
    }
}
