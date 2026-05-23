package ru.gorevmichael.sign.domain.usecases

import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.rem
import io.github.gatrongdev.kbignum.math.times
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign.domain.models.SignConfig

class VerifySignUseCase {
    operator fun invoke(
        message: KBigInteger,
        signConfig: SignConfig,
        publicKey: Point,
        signature: Pair<KBigInteger, KBigInteger>
    ): Boolean {
        val n = signConfig.order
        val (r, s) = signature

        val inverter = ModulusNumberInverter(n)
        val gpPoint = signConfig.generationPoint
        val inverseS = inverter.inverse(s)

        val u = message * inverseS % n
        val v = r * inverseS % n

        val cPoint = gpPoint.multiply(u).add(publicKey.multiply(v))

        return cPoint.x == r
    }
}