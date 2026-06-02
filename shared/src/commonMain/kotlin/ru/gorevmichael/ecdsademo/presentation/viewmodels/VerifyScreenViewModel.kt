package ru.gorevmichael.ecdsademo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign_v1.data.secp256_k1.Secp256k1SignConfig
import ru.gorevmichael.sign_v1.domain.models.SignConfig
import ru.gorevmichael.sign_v1.domain.usecases.LoadCustomSignConfigsUseCase
import ru.gorevmichael.sign_v1.domain.usecases.VerifySignUseCase

data class VerifyUiState(
    val message: String = "",
    val publicKeyJson: String = "",
    val signatureJson: String = "",
    val isValid: Boolean? = null,
    val error: String? = null,
    val configs: List<Pair<String, SignConfig>> = emptyList(),
    val selectedConfig: Pair<String, SignConfig> = "secp256k1" to Secp256k1SignConfig()
)

class VerifyScreenViewModel(
    private val verifySignUseCase: VerifySignUseCase = VerifySignUseCase(),
    private val loadCustomSignConfigsUseCase: LoadCustomSignConfigsUseCase = LoadCustomSignConfigsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCustomSignConfigsUseCase().collect { configs ->
                val defaultConfig = "secp256k1" to Secp256k1SignConfig()
                _uiState.update {
                    it.copy(
                        configs = listOf(defaultConfig) + configs
                    )
                }
            }
        }
    }

    fun onConfigSelected(config: Pair<String, SignConfig>) {
        _uiState.update { it.copy(selectedConfig = config, isValid = null, error = null) }
    }

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
        viewModelScope.launch {
            val state = _uiState.value
            if (state.message.isBlank() || state.publicKeyJson.isBlank() || state.signatureJson.isBlank()) {
                _uiState.update { it.copy(error = "Все поля должны быть заполнены!") }
                return@launch
            }

            try {
                val x = parseKBigInteger(state.publicKeyJson, "x")
                val y = parseKBigInteger(state.publicKeyJson, "y")
                val publicKey = Point(x, y, state.selectedConfig.second.curveConfig)

                val r = parseKBigInteger(state.signatureJson, "r")
                val s = parseKBigInteger(state.signatureJson, "s")

                val isValid = verifySignUseCase(
                    message = state.message,
                    signConfig = state.selectedConfig.second,
                    publicKey = publicKey,
                    signature = Pair(r, s)
                )

                _uiState.update { it.copy(isValid = isValid, error = null) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        error = "Ошибка: ${e.message ?: "Неверный формат"}",
                        isValid = null
                    )
                }
            }
        }
    }

    private fun parseKBigInteger(json: String, key: String): BigInteger {
        val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        val match = regex.find(json) ?: throw IllegalArgumentException("Ключ $key не найден")
        return match.groupValues[1].toBigInteger(10)
    }
}
