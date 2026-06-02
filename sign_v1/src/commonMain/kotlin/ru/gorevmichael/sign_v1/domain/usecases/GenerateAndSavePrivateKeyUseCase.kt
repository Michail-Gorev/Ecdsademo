package ru.gorevmichael.sign_v1.domain.usecases

import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.toKBigInteger
import ru.gorevmichael.files.domain.usecases.SaveJsonUseCase
import kotlin.random.Random
//TODO использовать
class GenerateAndSavePrivateKeyUseCase {
    operator fun invoke(): KBigInteger {
        val seed = 7891341234
        val order = Random(seed).nextInt(11, 2019)
        val privateKey = Random(seed).nextInt(233, 823567274).toKBigInteger()
            .multiply("47".toKBigInteger().pow(order))
        //TODO объединить и вынести пути
        val saveJsonUseCase: SaveJsonUseCase = SaveJsonUseCase(
            path = "private_key.json",
            content = privateKey.toString()
        )
        try {
            saveJsonUseCase()
        } catch (e: Exception) {
            println("Error saving private key: ${e.message}")
            e.printStackTrace()
        }
        return privateKey
    }
}