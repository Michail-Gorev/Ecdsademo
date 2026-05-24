package ru.gorevmichael.sign.domain.usecases

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256
import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.or
import io.github.gatrongdev.kbignum.math.rem
import io.github.gatrongdev.kbignum.math.times
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign.domain.models.SignConfig

class VerifySignUseCase {
    suspend operator fun invoke(
        message: Any,
        signConfig: SignConfig,
        publicKey: Point,
        signature: Pair<KBigInteger, KBigInteger>
    ): Boolean {
        if (message !is String && message !is KBigInteger)  {
            throw IllegalArgumentException("Message must be a string or KBigInteger!")
        }
        val n = signConfig.order
        val (r, s) = signature

        val messageToDecode: KBigInteger = when (message) {
            is KBigInteger -> message
            is String -> {
                val hashBytes = CryptographyProvider.Default.get(SHA256).hasher().hash(message.encodeToByteArray())
                var result = KBigInteger.ZERO
                for (byte in hashBytes) {
                    result = (result shl 8) or KBigInteger.fromInt(byte.toInt() and 0xFF)
                }
                result
            }
            else -> throw IllegalArgumentException("Unsupported message type")
        }

        val inverter = ModulusNumberInverter(n)
        val inverseS = inverter.inverse(s)

        val u = messageToDecode * inverseS % n
        val v = r * inverseS % n

        val cPoint = signConfig.generationPoint.multiply(u).add(publicKey.multiply(v))

        return cPoint.x == r
    }
}
