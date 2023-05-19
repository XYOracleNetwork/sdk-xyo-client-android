package network.xyo.ec

import java.math.BigInteger
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec


/**
 * A cryptographic elliptical curve to preform cryptography on.
 */
class EcCurve(val spec: ECParameterSpec) {
    /**
     * The prime modulus of the curve.
     */
    val p : BigInteger
        get() {
            val fpField = (spec.curve.field as ECFieldFp)
            return fpField.p
        }

    /**
     * The prime order of the curve.
     */
    val n : BigInteger
    get() {
        return spec.order
    }

    /**
     * The a coefficient of the curve.
     */
    val a : BigInteger
    get() {
        return spec.curve.a
    }

    /**
     * The b coefficient of the curve.
     */
    val b : BigInteger
    get() {
        return spec.curve.b
    }

    /**
     * X cord of the generator point -> G.
     */
    val x : BigInteger
    get() {
        return spec.generator.affineX
    }

    /**
     * Y cord of the generator point -> G.
     */
    val y : BigInteger
    get() {
        return spec.generator.affineY
    }

    /**
     * The generator point of the curve.
     */
    val g : EcPoint
        get() = EcPoint(x, y, this)

    /**
     * The identify of the curve.
     *
     * (PRIME MODULUS, 0)
     */
    val identity : EcPoint
        get() = EcPointMath.identity(g)

    /**
     * Adds two points that belong to the curve.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The sum of the two points.
     */
    fun add (p1 : EcPoint, p2: EcPoint) : EcPoint {
        if (p1.affineX == p) {
            return p2
        } else if (p2.affineX == p) {
            return p1
        }

        if (p1.affineX == p2.affineX) {
            if (p1.affineY == p2.affineY) {
                return EcPointMath.double(p1)
            }
            return EcPointMath.identity(p1)
        }

        val m = EcPointMath.divide(p1.affineY + p - p2.affineY, p1.affineX + p - p2.affineX, p)
        return EcPointMath.dot(p1, p2, m, this)
    }

    /**
     * Finds the product of a point on the curve. (Scalar multiplication)
     *
     * @param g The generator point to start at.
     * @param n The number of times to dot the curve from g.
     * @return The point ended up on the curve.
     */
    fun multiply (g : EcPoint, n : BigInteger) : EcPoint {
        var r = identity
        var q = g
        var m = n

        while (m != EcConstants.ZERO) {


            if (m and EcConstants.ONE != 0.toBigInteger()) {
                r = add(r, q)
            }

            m = m shr 1

            if (m != 0.toBigInteger()) {
                q = EcPointMath.double(q)
            }

        }

        return r
    }

    /**
     * Adds two points that belong to the curve.
     *
     * @param point The point to add to the g point.
     * @return The sum of the two points.
     */
    operator fun plus (point : EcPoint) : EcPoint {
        return add(g, point)
    }

    /**
     * Finds the product of a point on the curve and its generator point. (Scalar multiplication)
     *
     * @param n The number of times to dot the curve from g.
     * @return The product of the point.
     */
    operator fun times(n : BigInteger) : EcPoint {
        return multiply(g, n)
    }
}