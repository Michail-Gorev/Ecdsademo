package ru.gorevmichael.sign_v1.data.secp256_k1

import io.github.gatrongdev.kbignum.math.KBigInteger
import ru.gorevmichael.sign_v1.domain.models.SignConfig

class Secp256k1SignConfig(
    override val generationPoint: Secp256k1GenerationPoint = Secp256k1GenerationPoint(),
    override val curveConfig: Secp256k1CurveConfig = Secp256k1CurveConfig(),
    override val order: KBigInteger = KBigInteger.fromString("115792089237316195423570985008687907852837564279074904382605163141518161494337")
): SignConfig(generationPoint, curveConfig, order) {
    init {
        println("Secp256k1SignConfig initialized")
    }
}