package ru.gorevmichael.math.domain.models

import com.ionspin.kotlin.bignum.integer.BigInteger

open class CurveConfig(
    open val a: BigInteger,
    open val b: BigInteger,
    open val p: BigInteger
) {
    override fun toString(): String {
        return """
            CurveConfig {
                a: $a;
                b: $b;
                p: $p 
            }
        """.trimIndent()
    }
}