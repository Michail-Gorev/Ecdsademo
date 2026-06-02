package ru.gorevmichael.sign_v1.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign_v1.domain.models.SignConfig

class VerifySignUseCase {
    suspend operator fun invoke(
        message: Any,
        signConfig: SignConfig,
        publicKey: Point,
        signature: Pair<BigInteger, BigInteger>
    ): Boolean {
        if (message !is String && message !is BigInteger)  {
            throw IllegalArgumentException("Message must be a string or BigInteger!")
        }
        val n = signConfig.order
        val (r, s) = signature

        val messageToDecode: BigInteger = when (message) {
            is BigInteger -> message
            is String -> {
                val hashBytes = CryptographyProvider.Default.get(SHA256).hasher().hash(message.encodeToByteArray())
                var result = BigInteger.ZERO
                for (byte in hashBytes) {
                    result = (result shl 8) or BigInteger.fromInt(byte.toInt() and 0xFF)
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
