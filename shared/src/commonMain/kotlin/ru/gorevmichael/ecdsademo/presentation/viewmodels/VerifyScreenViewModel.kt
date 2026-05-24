package ru.gorevmichael.ecdsademo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.github.gatrongdev.kbignum.math.KBigInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign.data.secp256_k1.Secp256k1SignConfig
import ru.gorevmichael.sign.domain.usecases.VerifySignUseCase

data class VerifyUiState(
    val message: String = "",
    val publicKeyJson: String = "",
    val signatureJson: String = "",
    val isValid: Boolean? = null,
    val error: String? = null
)

class VerifyScreenViewModel(
    private val verifySignUseCase: VerifySignUseCase = VerifySignUseCase(),
    private val signConfig: Secp256k1SignConfig = Secp256k1SignConfig()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyUiState())
    private val _viewModelScope = CoroutineScope(Dispatchers.IO)
    val uiState = _uiState.asStateFlow()

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(message = message, isValid = null, error = null) }
    }

    fun onPublicKeyChanged(publicKeyJson: String) {
        _uiState.update { it.copy(publicKeyJson = publicKeyJson, isValid = null, error = null) }
    }

    fun onSignatureChanged(signatureJson: String) {
        _uiState.update { it.copy(signatureJson = signatureJson, isValid = null, error = null) }
    }

    fun verifySignature() {
        _viewModelScope.launch {
            val state = _uiState.value
            if (state.message.isBlank() || state.publicKeyJson.isBlank() || state.signatureJson.isBlank()) {
                _uiState.update { it.copy(error = "Все поля должны быть заполнены!") }
                return@launch
            }

            try {
                val x = parseKBigInteger(state.publicKeyJson, "x")
                val y = parseKBigInteger(state.publicKeyJson, "y")
                val publicKey = Point(x, y, signConfig.curveConfig)

                val r = parseKBigInteger(state.signatureJson, "r")
                val s = parseKBigInteger(state.signatureJson, "s")

                val isValid = verifySignUseCase(
                    message = state.message,
                    signConfig = signConfig,
                    publicKey = publicKey,
                    signature = Pair(r, s)
                )

                _uiState.update { it.copy(isValid = isValid, error = null) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(error = "Ошибка: ${e.message ?: "Неверный формат"}", isValid = null) }
            }
        }
    }

    private fun parseKBigInteger(json: String, key: String): KBigInteger {
        val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        val match = regex.find(json) ?: throw IllegalArgumentException("Ключ $key не найден")
        return KBigInteger.fromString(match.groupValues[1])
    }
}
