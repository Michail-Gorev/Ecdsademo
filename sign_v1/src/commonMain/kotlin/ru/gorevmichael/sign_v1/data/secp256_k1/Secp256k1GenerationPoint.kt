package ru.gorevmichael.sign_v1.data.secp256_k1

import io.github.gatrongdev.kbignum.math.KBigInteger
import ru.gorevmichael.math.domain.models.Point

class Secp256k1GenerationPoint(
    override val x: KBigInteger = KBigInteger.fromString("55066263022277343669578718895168534326250603453777594175500187360389116729240"),
    override val y: KBigInteger = KBigInteger.fromString("32670510020758816978083085130507043184471273380659243275938904335757337482424"),
    override val curveConfig: Secp256k1CurveConfig = Secp256k1CurveConfig()
): Point(x, y, curveConfig)