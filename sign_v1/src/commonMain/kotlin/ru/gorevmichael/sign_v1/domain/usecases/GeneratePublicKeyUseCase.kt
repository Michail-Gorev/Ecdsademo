package ru.gorevmichael.sign_v1.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import ru.gorevmichael.math.domain.models.Point

class GeneratePublicKeyUseCase {
    operator fun invoke(privateKey: BigInteger, gp: Point): Point {
        return gp.multiply(privateKey)
    }
}