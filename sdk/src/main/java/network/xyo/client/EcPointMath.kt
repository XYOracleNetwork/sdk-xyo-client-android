package network.xyo.client

import java.math.BigInteger


/**
 * A helper object to do point arithmetic in a prime field.
 */
object EcPointMath {
    /**
     * Multiples two numbers in a prime field.
     *
     * @param a The first number to multiply.
     * @param b The second number to multiply.
     * @param prime The prime modulus of the field.
     * @return The product of a and b
     */
    fun multiply (a : BigInteger, b : BigInteger, prime: BigInteger) : BigInteger {
        return (a * b) % prime
    }

    /**
     * Divides two numbers in a prime field.
     *
     * @param num The numerator to divide.
     * @param dom The denominator to divide.
     * @param prime The prime modulus of the field.
     * @return The quotient of num and dom
     */
    fun divide (num : BigInteger, dom : BigInteger, prime: BigInteger) : BigInteger {
        val inverseDen = dom.modInverse(prime)
        return multiply(num % prime, inverseDen, prime)
    }

    /**
     * Finds the slop of a point on a curve.
     *
     * @param point The point on the curve to find the tangent slope.
     * @param curve The curve that the point is on.
     * @return The slope of the tangent line at the point
     */
    fun tangent (point: EcPoint, curve: EcCurve) : BigInteger {
        return divide(point.affineX * point.affineX*EcConstants.THREE+curve.a, point.affineY * EcConstants.TWO, curve.p)
    }

    /**
     * Gets the identify of a point. (x, y) -> (CURVE PRIME, 0)
     *
     * @param point The point to get the identity of.
     */
    fun identity (point: EcPoint) : EcPoint {
        return EcPoint(point.curve.p, EcConstants.ZERO, point.curve)
    }

    /**
     * Dots a point on a curve. Finds the line between A and B then finds the 3rd point on the elliptical curve.
     *
     * @param p1 The first point on the curve.
     * @param p2 The second point on the curve.
     * @param m The slope of the scent line.
     * @param curve The curve that the points are on.
     * @return The result of the dotting.
     */
    fun dot (p1: EcPoint, p2: EcPoint, m : BigInteger, curve: EcCurve) : EcPoint {
        val v = (p1.affineY + curve.p - (m * p1.affineX) % curve.p) % curve.p
        val x = (m*m + curve.p - p1.affineX + curve.p - p2.affineX) % curve.p
        val y = (curve.p - (m*x) % curve.p + curve.p - v) % curve.p
        return EcPoint(x, y, curve)
    }

    /**
     * Doubles a point. Adds itself to itself.
     *
     * @param point The point to double.
     * @return point + point
     */
    fun double (point: EcPoint) : EcPoint {
        if (point.affineX == point.curve.p) {
            return point
        }

        return dot(point, point, tangent(point, point.curve), point.curve)
    }
}