package network.xyo.client.account

import android.os.Build
import androidx.annotation.RequiresApi
import org.spongycastle.jcajce.provider.digest.Keccak
import java.math.BigInteger
import kotlin.experimental.xor

// Secp256k1 curve parameters
val P = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16)
val A = BigInteger.ZERO
val B = BigInteger.valueOf(7)
val N = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16)
val Gx = BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16)
val Gy = BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16)

// Elliptic curve point structure
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
        if ((p.y + q.y).mod(P) == BigInteger.ZERO) return Point(BigInteger.ZERO, BigInteger.ZERO)
        (BigInteger.valueOf(3) * p.x.pow(2) + A).mod(P) * (BigInteger.TWO * p.y).modInverse(P)
    } else {
        (q.y - p.y).mod(P) * (q.x - p.x).modInverse(P)
    }.mod(P)

    val xR = (slope.pow(2) - p.x - q.x).mod(P)
    val yR = (slope * (p.x - xR) - p.y).mod(P)

    return Point(xR, yR)
}

// Point doubling on the elliptic curve
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun pointDouble(p: Point): Point = pointAdd(p, p)

// Recover public key from signature
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun recoverPublicKey(messageHash: BigInteger, r: BigInteger, s: BigInteger, v: Int): Point? {
    val isYEven = (v % 2 == 0)
    val x = r.add(N.multiply(BigInteger.valueOf((v / 2).toLong())))

    if (x >= P) return null

    // Calculate y-coordinate
    val alpha = (x.pow(3) + A * x + B).mod(P)
    val beta = alpha.modPow((P + BigInteger.ONE).shiftRight(2), P)
    val y = if (beta.testBit(0) == isYEven) beta else P - beta

    val rPoint = Point(x, y)

    // Calculate e and r^-1
    val e = messageHash
    val rInv = r.modInverse(N)

    // Public key Q = r^-1 * (s * R - e * G)
    val sR = pointMultiply(s, rPoint)
    val eG = pointMultiply(e, Point(Gx, Gy))

    return pointMultiply(rInv, pointAdd(sR, Point(eG.x, P - eG.y)))
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
    val v = 0

    val messageHashBI = BigInteger(1, messageHash)

    val publicPoint = recoverPublicKey(messageHashBI, r, s, v) ?: return null
    val uncompressedKey =
            publicPoint.x.toByteArray().padStart(32, 0) +
            publicPoint.y.toByteArray().padStart(32, 0)
    return uncompressedKey
}