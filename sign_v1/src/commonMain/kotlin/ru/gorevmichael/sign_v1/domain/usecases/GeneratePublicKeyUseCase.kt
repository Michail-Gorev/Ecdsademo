package ru.gorevmichael.sign_v1.domain.usecases

import io.github.gatrongdev.kbignum.math.KBigInteger
import ru.gorevmichael.math.domain.models.Point

class GeneratePublicKeyUseCase {
    operator fun invoke(privateKey: KBigInteger, gp: Point): Point {
        return gp.multiply(privateKey)
    }
}