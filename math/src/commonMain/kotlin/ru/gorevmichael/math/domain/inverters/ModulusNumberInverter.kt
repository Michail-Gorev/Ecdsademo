package ru.gorevmichael.math.domain.inverters

import com.ionspin.kotlin.bignum.integer.BigInteger
import ru.gorevmichael.core.interfaces.NumberInverter

/**
 * Класс для получения числа, обратного по модулю, используя расширенный алгоритм Евклида
 */
class ModulusNumberInverter(private val modulus: BigInteger) : NumberInverter {

    override fun inverse(value: BigInteger): BigInteger {
        var a = value % modulus
        if (a < BigInteger.ZERO) {
            a += modulus
        }

        var m = modulus
        var y = BigInteger.ZERO
        var x = BigInteger.ONE

        if (m == BigInteger.ONE) {
            return BigInteger.ZERO
        }

        while (a > BigInteger.ONE) {
            val q = a / m
            var t = m

            m = a % m
            a = t
            t = y

            y = x - q * y
            x = t
        }

        if (x < BigInteger.ZERO) {
            x += modulus
        }

        return x
    }

    override fun checkAreInverse(
        n1: BigInteger,
        n2: BigInteger
    ): Boolean {
        val product = (n1 * n2) % modulus
        val normalizedProduct = if (product < BigInteger.ZERO) {
            product + modulus
        } else {
            product
        }
        return normalizedProduct == BigInteger.ONE
    }
}