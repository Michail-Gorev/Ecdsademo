package ru.gorevmichael.sign.domain.models

import io.github.gatrongdev.kbignum.math.KBigInteger
import ru.gorevmichael.math.domain.models.CurveConfig
import ru.gorevmichael.math.domain.models.Point

open class SignConfig(
    open val generationPoint: Point,
    open val curveConfig: CurveConfig,
    open val order: KBigInteger
)