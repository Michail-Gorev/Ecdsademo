package ru.gorevmichael.math.domain.models

import io.github.gatrongdev.kbignum.math.KBigInteger

open class CurveConfig(
    open val a: KBigInteger,
    open val b: KBigInteger,
    open val p: KBigInteger
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