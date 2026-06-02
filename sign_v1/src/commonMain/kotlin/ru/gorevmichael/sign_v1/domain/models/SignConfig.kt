package ru.gorevmichael.sign_v1.domain.models

import com.ionspin.kotlin.bignum.integer.BigInteger
import ru.gorevmichael.math.domain.models.CurveConfig
import ru.gorevmichael.math.domain.models.Point

open class SignConfig(
    open val generationPoint: Point,
    open val curveConfig: CurveConfig,
    open val order: BigInteger
)