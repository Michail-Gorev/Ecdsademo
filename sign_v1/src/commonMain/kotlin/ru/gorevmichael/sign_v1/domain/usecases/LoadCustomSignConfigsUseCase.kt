package ru.gorevmichael.sign_v1.domain.usecases

import io.github.gatrongdev.kbignum.math.KBigInteger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorevmichael.files.domain.platformSpeciefic.InternalStorage
import ru.gorevmichael.math.domain.models.CurveConfig
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign_v1.domain.models.SignConfig

class LoadCustomSignConfigsUseCase {
    operator fun invoke(): Flow<List<Pair<String, SignConfig>>> {
        return InternalStorage.list("sign_configs").map { paths ->
            paths.filter { it.endsWith(".json") }.mapNotNull { path ->
                try {
                    val content = InternalStorage.load(path)?.content ?: return@mapNotNull null
                    parseConfig(content)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    private fun parseConfig(json: String): Pair<String, SignConfig> {
        val name = getValue(json, "name")
        val a = getValue(json, "a")
        val b = getValue(json, "b")
        val p = getValue(json, "p")
        val x = getValue(json, "x")
        val y = getValue(json, "y")
        val order = getValue(json, "order")

        val curveConfig = CurveConfig(
            KBigInteger.fromString(a),
            KBigInteger.fromString(b),
            KBigInteger.fromString(p)
        )
        val genPoint = Point(
            KBigInteger.fromString(x),
            KBigInteger.fromString(y),
            curveConfig
        )
        
        return name to SignConfig(genPoint, curveConfig, KBigInteger.fromString(order))
    }

    private fun getValue(json: String, key: String): String {
        val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return regex.find(json)?.groupValues?.get(1) ?: throw IllegalArgumentException("Key $key not found")
    }
}
