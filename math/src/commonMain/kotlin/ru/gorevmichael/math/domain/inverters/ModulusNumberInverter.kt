package ru.gorevmichael.math.domain.inverters

import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.div
import io.github.gatrongdev.kbignum.math.minus
import io.github.gatrongdev.kbignum.math.plus
import io.github.gatrongdev.kbignum.math.rem
import io.github.gatrongdev.kbignum.math.times
import ru.gorevmichael.core.interfaces.NumberInverter

/**
 * Класс для получения числа, обратного по модулю, используя расширенный алгоритм Евклида
 */
class ModulusNumberInverter(private val modulus: KBigInteger) : NumberInverter {

    override fun inverse(value: KBigInteger): KBigInteger {
        var a = value % modulus
        if (a < KBigInteger.ZERO) {
            a += modulus
        }

        var m = modulus
        var y = KBigInteger.ZERO
        var x = KBigInteger.ONE

        if (m == KBigInteger.ONE) {
            return KBigInteger.ZERO
        }

        while (a > KBigInteger.ONE) {
            val q = a / m
            var t = m

            m = a % m
            a = t
            t = y

            y = x - q * y
            x = t
        }

        if (x < KBigInteger.ZERO) {
            x += modulus
        }

        return x
    }

    override fun checkAreInverse(
        n1: KBigInteger,
        n2: KBigInteger
    ): Boolean {
        val product = (n1 * n2) % modulus
        val normalizedProduct = if (product < KBigInteger.ZERO) {
            product + modulus
        } else {
            product
        }
        return normalizedProduct == KBigInteger.ONE
    }
}