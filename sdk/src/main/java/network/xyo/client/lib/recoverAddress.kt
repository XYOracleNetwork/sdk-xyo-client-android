package network.xyo.client.lib

import android.os.Build
import androidx.annotation.RequiresApi
import org.spongycastle.jcajce.provider.digest.Keccak
import java.math.BigInteger

data class Point(val x: BigInteger, val y: BigInteger) {
    fun isAtInfinity() = x == BigInteger.ZERO && y == BigInteger.ZERO
}

fun ByteArray.padStart(targetLength: Int, padValue: Byte = 0): ByteArray {
    if (this.size >= targetLength) return this
    val padding = ByteArray(targetLength - this.size) { padValue }
    return padding + this
}

// Double and Add algorithm for point multiplication
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun pointMultiply(k: BigInteger, point: Point): Point {
    var result = Point(BigInteger.ZERO, BigInteger.ZERO) // Infinity point
    var addend = point
    var scalar = k

    while (scalar > BigInteger.ZERO) {
        if (scalar.and(BigInteger.ONE) == BigInteger.ONE) {
            result = pointAdd(result, addend)
        }
        addend = pointDouble(addend)
        scalar = scalar.shiftRight(1)
    }

    return result
}

// Point addition on the elliptic curve
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun pointAdd(p: Point, q: Point): Point {
    if (p.isAtInfinity()) return q
    if (q.isAtInfinity()) return p

    val slope = if (p.x == q.x) {
        if ((p.y + q.y).mod(Secp256k1CurveConstants.p) == BigInteger.ZERO) return Point(BigInteger.ZERO, BigInteger.ZERO)
        (BigInteger.valueOf(3) * p.x.pow(2) + Secp256k1CurveConstants.a).mod(Secp256k1CurveConstants.p) * (BigInteger.TWO * p.y).modInverse(Secp256k1CurveConstants.p)
    } else {
        (q.y - p.y).mod(Secp256k1CurveConstants.p) * (q.x - p.x).modInverse(Secp256k1CurveConstants.p)
    }.mod(Secp256k1CurveConstants.p)

    val xR = (slope.pow(2) - p.x - q.x).mod(Secp256k1CurveConstants.p)
    val yR = (slope * (p.x - xR) - p.y).mod(Secp256k1CurveConstants.p)

    return Point(xR, yR)
}

// Point doubling on the elliptic curve
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun pointDouble(p: Point): Point = pointAdd(p, p)

// Recover public key from signature
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun recoverPublicKey(messageHash: BigInteger, r: BigInteger, s: BigInteger, v: Int): Point? {
    val isYEven = (v % 2 == 0)
    val x = r.add(Secp256k1CurveConstants.n.multiply(BigInteger.valueOf((v / 2).toLong())))

    if (x >= Secp256k1CurveConstants.p) return null

    // Calculate y-coordinate
    val alpha = (x.pow(3) + Secp256k1CurveConstants.a * x + Secp256k1CurveConstants.b).mod(Secp256k1CurveConstants.p)
    val beta = alpha.modPow((Secp256k1CurveConstants.p + BigInteger.ONE).shiftRight(2), Secp256k1CurveConstants.p)
    val y = if (beta.testBit(0) == isYEven) beta else Secp256k1CurveConstants.p - beta

    val rPoint = Point(x, y)

    // Calculate e and r^-1
    val e = messageHash
    val rInv = r.modInverse(Secp256k1CurveConstants.n)

    // Public key Q = r^-1 * (s * R - e * G)
    val sR = pointMultiply(s, rPoint)
    val eG = pointMultiply(e, Secp256k1CurveConstants.g)

    return pointMultiply(rInv, pointAdd(sR, Point(eG.x, Secp256k1CurveConstants.p - eG.y)))
}

private fun toKeccak(bytes: ByteArray): ByteArray {
    val keccak = Keccak.Digest256()
    keccak.update(bytes)
    return keccak.digest()
}

// Convert public key to Ethereum address
fun publicKeyToAddress(publicKey: ByteArray): ByteArray {
    val hash = toKeccak(publicKey.sliceArray(1 until publicKey.size))
    return hash.sliceArray(12 until hash.size)
}

// Main function to recover address
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun recoverPublicKey(messageHash: ByteArray, signature: ByteArray): ByteArray? {
    if (signature.size != 64) return null

    val r = BigInteger(1, signature.copyOfRange(0, 32))
    val s = BigInteger(1, signature.copyOfRange(32, 64))

    val messageHashBI = BigInteger(1, messageHash)

    val publicPoint = recoverPublicKey(messageHashBI, r, s, 0) ?: recoverPublicKey(messageHashBI, r, s, 1) ?: return null
    val uncompressedKey =
            publicPoint.x.toByteArray().padStart(32, 0) +
            publicPoint.y.toByteArray().padStart(32, 0)
    return uncompressedKey
}