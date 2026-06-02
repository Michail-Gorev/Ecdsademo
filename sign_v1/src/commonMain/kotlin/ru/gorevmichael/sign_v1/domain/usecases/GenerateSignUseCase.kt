package ru.gorevmichael.sign_v1.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign_v1.domain.models.SignConfig
import kotlin.random.Random

class GenerateSignUseCase {
    suspend operator fun invoke(
        message: Any,
        signConfig: SignConfig,
        privateKey: BigInteger
    ): Pair<BigInteger, BigInteger> {
        if (message !is String && message !is BigInteger)  {
            throw IllegalArgumentException("Message must be a string or BigInteger!")
        }
        val n = signConfig.order
        val inverter = ModulusNumberInverter(n)
        val gpPoint = signConfig.generationPoint

        var k: BigInteger
        var rPoint: Point
        var r: BigInteger

        do {
            //FIXME заменить на "честную" генерацию случайного BigInteger (и вынести в модуль math)
            k = Random.nextInt(11, 1239232312).toBigInteger()*"1000000000000000".toBigInteger()
            rPoint = gpPoint.multiply(k)
            r = rPoint.x % n
            // Нормализация (выглядит необходимой)
            if (r < BigInteger.ZERO) {
                r += n
            }
        } while (r == BigInteger.ZERO)

        val kInverse = inverter.inverse(k)

        val messageToEncode: BigInteger = when (message) {
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

        println("messageToEncode: $messageToEncode")
        val s = kInverse * (messageToEncode + r * privateKey) % n

        return Pair(r, s)
    }
}