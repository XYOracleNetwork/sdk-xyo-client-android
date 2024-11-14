package network.xyo.client.account

import java.math.BigInteger

private val BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()

fun toBase58(bytes: ByteArray): String {
    var num = bytes.fold(0.toBigInteger()) { acc, byte ->
        (acc shl 8) or (byte.toInt() and 0xFF).toBigInteger()
    }

    val result = StringBuilder()
    while (num > BigInteger.ZERO) {
        val remainder = (num % 58.toBigInteger()).toInt()
        result.insert(0, BASE58_ALPHABET[remainder])
        num /= 58.toBigInteger()
    }

    // Handle leading zero bytes (they are encoded as '1' in Base58)
    for (byte in bytes) {
        if (byte.toInt() == 0) result.insert(0, BASE58_ALPHABET[0])
        else break
    }

    return result.toString()
}

fun toBase58(value: BigInteger): String {
    return toBase58(value.toByteArray())
}