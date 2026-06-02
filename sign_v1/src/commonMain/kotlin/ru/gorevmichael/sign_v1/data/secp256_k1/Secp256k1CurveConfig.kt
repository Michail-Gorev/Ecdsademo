package ru.gorevmichael.sign_v1.data.secp256_k1

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ru.gorevmichael.math.domain.models.CurveConfig

data class Secp256k1CurveConfig(
    override val a: BigInteger = BigInteger.fromInt(0),
    override val b: BigInteger = BigInteger.fromInt(7),
    override val p: BigInteger = "115792089237316195423570985008687907853269984665640564039457584007908834671663".toBigInteger(10)
): CurveConfig(a, b, p)