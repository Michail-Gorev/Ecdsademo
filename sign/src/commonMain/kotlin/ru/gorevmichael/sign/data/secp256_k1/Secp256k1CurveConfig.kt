package ru.gorevmichael.sign.data.secp256_k1

import io.github.gatrongdev.kbignum.math.KBigInteger
import ru.gorevmichael.math.domain.models.CurveConfig

data class Secp256k1CurveConfig(
    override val a: KBigInteger = KBigInteger.fromInt(0),
    override val b: KBigInteger = KBigInteger.fromInt(7),
    override val p: KBigInteger = KBigInteger.fromString("115792089237316195423570985008687907853269984665640564039457584007908834671663")
): CurveConfig(a, b, p)