package ru.gorevmichael.sign_v1.data.secp256_k1

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ru.gorevmichael.math.domain.models.Point

class Secp256k1GenerationPoint(
    override val x: BigInteger = "55066263022277343669578718895168534326250603453777594175500187360389116729240".toBigInteger(10),
    override val y: BigInteger = "32670510020758816978083085130507043184471273380659243275938904335757337482424".toBigInteger(10),
    override val curveConfig: Secp256k1CurveConfig = Secp256k1CurveConfig()
): Point(x, y, curveConfig)