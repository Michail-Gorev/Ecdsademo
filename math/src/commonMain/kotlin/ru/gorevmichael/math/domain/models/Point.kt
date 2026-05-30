package ru.gorevmichael.math.domain.models

import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.div
import io.github.gatrongdev.kbignum.math.minus
import io.github.gatrongdev.kbignum.math.plus
import io.github.gatrongdev.kbignum.math.rem
import io.github.gatrongdev.kbignum.math.times
import io.github.gatrongdev.kbignum.math.toKBigInteger
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter

/**
 * Класс, описывающий точку и операции над ней
 */
open class Point(
    x: KBigInteger,
    y: KBigInteger,
    curveConfig: CurveConfig
) {
    init {
        val left = y.pow(2).mod(curveConfig.p)
        val right = (x.pow(3) + curveConfig.a * x + curveConfig.b).mod(curveConfig.p)

        if (left != right) {
            throw IllegalArgumentException("Invalid point with coordinates ($x, $y) and curve config $curveConfig")
        }
    }

    open val x: KBigInteger = x
    open val y: KBigInteger = y
    open val curveConfig: CurveConfig = curveConfig

    private val inverter = ModulusNumberInverter(curveConfig.p)

    private fun KBigInteger.modPositive(m: KBigInteger): KBigInteger {
        val rem = this % m
        return if (rem < KBigInteger.ZERO) rem + m else rem
    }

    fun add(point: Point): Point {
        val p = curveConfig.p

        if (this == point) {
            if (this.y == KBigInteger.ZERO) {
                throw IllegalArgumentException("Cannot double point with y=0")
            }
            val slopeNum = (3.toKBigInteger() * this.x.pow(2) + curveConfig.a).modPositive(p)
            val slopeDen = (2.toKBigInteger() * this.y).modPositive(p)
            val slope = slopeNum * inverter.inverse(slopeDen) % p
            val slopeNorm = slope.modPositive(p)

            val x3 = (slopeNorm.pow(2) - 2.toKBigInteger() * this.x).modPositive(p)
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

    fun multiply(times: KBigInteger): Point {
        if (times == KBigInteger.ZERO) {
            throw IllegalArgumentException("Cannot multiply by zero")
        }
        if (times == KBigInteger.ONE) {
            return this
        }

        var result: Point? = null
        var current = this
        var exp = times

        while (exp > KBigInteger.ZERO) {
            if (exp % 2.toKBigInteger() == KBigInteger.ONE) {
                result = result?.add(current) ?: current
            }
            current = current.add(current)
            exp /= 2.toKBigInteger()
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