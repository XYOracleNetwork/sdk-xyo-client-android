package network.xyo.ec

import java.math.BigInteger
import java.security.spec.ECPoint

class EcPoint (x : BigInteger, y : BigInteger, val curve: EcCurve): ECPoint(x, y) {

    /**
     * Adds a point to this point.
     *
     * @param other The point to add to this point.
     * @return The sum of the two points.
     */
    operator fun plus (other: EcPoint) : EcPoint {
        return curve.add(this, other)
    }

    /**
     * Finds the product of this point and a number. (dotting the curve multiple times)
     *
     * @param n The number to multiply the point by.
     * @return The product of the point and the number.
     */
    operator fun times(n: BigInteger): EcPoint {
        return curve.multiply(this, n)
    }
}