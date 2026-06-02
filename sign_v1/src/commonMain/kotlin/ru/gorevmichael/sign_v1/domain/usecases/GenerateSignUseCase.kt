package ru.gorevmichael.sign_v1.domain.usecases

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256
import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.or
import io.github.gatrongdev.kbignum.math.plus
import io.github.gatrongdev.kbignum.math.rem
import io.github.gatrongdev.kbignum.math.times
import io.github.gatrongdev.kbignum.math.toKBigInteger
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign_v1.domain.models.SignConfig
import kotlin.random.Random

class GenerateSignUseCase {
    suspend operator fun invoke(
        message: Any,
        signConfig: SignConfig,
        privateKey: KBigInteger
    ): Pair<KBigInteger, KBigInteger> {
        if (message !is String && message !is KBigInteger)  {
            throw IllegalArgumentException("Message must be a string or KBigInteger!")
        }
        val n = signConfig.order
        val inverter = ModulusNumberInverter(n)
        val gpPoint = signConfig.generationPoint

        var k: KBigInteger
        var rPoint: Point
        var r: KBigInteger

        do {
            //FIXME заменить на "честную" генерацию случайного KBigInteger (и вынести в модуль math)
            k = Random.nextInt(11, 1239232312).toKBigInteger()*"1000000000000000".toKBigInteger()
            rPoint = gpPoint.multiply(k)
            r = rPoint.x % n
            // Нормализация (выглядит необходимой)
            if (r < KBigInteger.ZERO) {
                r += n
            }
        } while (r == KBigInteger.ZERO)

        val kInverse = inverter.inverse(k)

        val messageToEncode: KBigInteger = when (message) {
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

        println("messageToEncode: $messageToEncode")
        val s = kInverse * (messageToEncode + r * privateKey) % n

        return Pair(r, s)
    }
}