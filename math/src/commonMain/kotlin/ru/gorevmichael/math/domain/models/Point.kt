package ru.gorevmichael.math.domain.models

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter

/**
 * Класс, описывающий точку и операции над ней
 */
open class Point(
    x: BigInteger,
    y: BigInteger,
    curveConfig: CurveConfig
) {
    init {
        val left = y.pow(2).mod(curveConfig.p)
        val right = (x.pow(3) + curveConfig.a * x + curveConfig.b).mod(curveConfig.p)

        if (left != right) {
            throw IllegalArgumentException("Invalid point with coordinates ($x, $y) and curve config $curveConfig")
        }
    }

    open val x: BigInteger = x
    open val y: BigInteger = y
    open val curveConfig: CurveConfig = curveConfig

    private val inverter = ModulusNumberInverter(curveConfig.p)

    private fun BigInteger.modPositive(m: BigInteger): BigInteger {
        val rem = this % m
        return if (rem < BigInteger.ZERO) rem + m else rem
    }

    fun add(point: Point): Point {
        val p = curveConfig.p

        if (this == point) {
            if (this.y == BigInteger.ZERO) {
                throw IllegalArgumentException("Cannot double point with y=0")
            }
            val slopeNum = (3.toBigInteger() * this.x.pow(2) + curveConfig.a).modPositive(p)
            val slopeDen = (2.toBigInteger() * this.y).modPositive(p)
            val slope = slopeNum * inverter.inverse(slopeDen) % p
            val slopeNorm = slope.modPositive(p)

            val x3 = (slopeNorm.pow(2) - 2.toBigInteger() * this.x).modPositive(p)
            val y3 = (slopeNorm * (this.x - x3) - this.y).modPositive(p)

            return Point(x3, y3, curveConfig)
        }

        if (this.x == point.x) {
            throw IllegalArgumentException("Points have same x - result is point at infinity")
        }

        val slopeNum = (point.y - this.y).modPositive(p)
        val slopeDen = (point.x - this.x).modPositive(p)
        val slope = slopeNum * inverter.inverse(slopeDen) % p
        val slopeNorm = slope.modPositive(p)

        val x3 = (slopeNorm.pow(2) - this.x - point.x).modPositive(p)
        val y3 = (slopeNorm * (this.x - x3) - this.y).modPositive(p)

        return Point(x3, y3, curveConfig)
    }

    fun multiply(times: BigInteger): Point {
        if (times == BigInteger.ZERO) {
            throw IllegalArgumentException("Cannot multiply by zero")
        }
        if (times == BigInteger.ONE) {
            return this
        }

        var result: Point? = null
        var current = this
        var exp = times

        while (exp > BigInteger.ZERO) {
            if (exp % 2.toBigInteger() == BigInteger.ONE) {
                result = result?.add(current) ?: current
            }
            current = current.add(current)
            exp /= 2.toBigInteger()
        }

        return result ?: throw IllegalStateException("Multiplication failed")
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Point) return false
        if (this === other) return true
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + curveConfig.hashCode()
        return result
    }
}