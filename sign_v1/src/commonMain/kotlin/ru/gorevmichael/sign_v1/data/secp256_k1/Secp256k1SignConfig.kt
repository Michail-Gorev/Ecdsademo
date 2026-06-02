package ru.gorevmichael.sign_v1.data.secp256_k1

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ru.gorevmichael.sign_v1.domain.models.SignConfig

class Secp256k1SignConfig(
    override val generationPoint: Secp256k1GenerationPoint = Secp256k1GenerationPoint(),
    override val curveConfig: Secp256k1CurveConfig = Secp256k1CurveConfig(),
    override val order: BigInteger = "115792089237316195423570985008687907852837564279074904382605163141518161494337".toBigInteger(10)
): SignConfig(generationPoint, curveConfig, order) {
    init {
        println("Secp256k1SignConfig initialized")
    }
}