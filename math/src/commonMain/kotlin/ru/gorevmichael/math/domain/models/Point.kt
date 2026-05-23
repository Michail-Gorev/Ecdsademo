package ru.gorevmichael.math.domain.models

import io.github.gatrongdev.kbignum.math.KBigInteger
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
        if (y.pow(2) % curveConfig.p !=
            (x.pow(3) + curveConfig.a * x + curveConfig.b) % curveConfig.p
        ) {
            throw IllegalArgumentException("Invalid point with coordinates ($x, $y) and curve config $curveConfig")
        }
    }

    open val x: KBigInteger = x
    open val y: KBigInteger = y
    open val curveConfig: CurveConfig = curveConfig

    private val inverter = ModulusNumberInverter(curveConfig.p)

    fun add(point: Point): Point {
        val p = curveConfig.p
        var slope = if (this == point) {
            (3.toKBigInteger() * point.x.pow(2)) * inverter.inverse(2.toKBigInteger() * point.y) % p
        } else {
            (point.y - this.y) * inverter.inverse(point.x - this.x) % p
        }

        var x = (slope.pow(2) - point.x - this.x) % p
        var y = (slope * (this.x - x) - this.y) % p

        // Нормализация (выглядит необходимой)
        if (x < KBigInteger.ZERO) {
            x += p
        }
        if (y < KBigInteger.ZERO) {
            y += p
        }

        return Point(
            x = x,
            y = y,
            curveConfig = curveConfig
        )
    }

    fun multiply(times: KBigInteger): Point {
        var currentPoint = this
        var currentCoefficient = KBigInteger.ONE

        val previousPoints = mutableListOf<Pair<KBigInteger, Point>>()
        while (currentCoefficient < times) {
            // сохраняем текущую точку в листе предыдущих точек
            previousPoints.add(Pair(currentCoefficient, currentPoint))
            // если можем умножить текущую точку на 2, умножаем
            if (2.toKBigInteger() * currentCoefficient <= times) {
                currentPoint = currentPoint.add(currentPoint)
                currentCoefficient = 2.toKBigInteger() * currentCoefficient
            }
            // если не можем умножить на 2, находим наибольшую подходящую точку, и складываем текущую точку с ней
            else {
                var nextPoint = this
                var nextCoefficient = 1.toKBigInteger()
                for ((previousCoefficient, previousPoint) in previousPoints) {
                    if (previousCoefficient + currentCoefficient <= times) {
                        if (previousPoint.x != currentPoint.x) {
                            nextCoefficient = previousCoefficient
                            nextPoint = previousPoint
                        }
                    }
                }
                currentPoint = currentPoint.add(nextPoint)
                currentCoefficient += nextCoefficient
            }
        }

        return currentPoint
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Point) {
            return false
        }
        if (this === other) {
            return true
        }
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + curveConfig.hashCode()
        result = 31 * result + inverter.hashCode()
        return result
    }
}