package ru.gorevmichael.ecdsademo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.toKBigInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gorevmichael.sign_v1.data.secp256_k1.Secp256k1SignConfig
import ru.gorevmichael.sign_v1.domain.models.SignConfig
import ru.gorevmichael.sign_v1.domain.usecases.GeneratePublicKeyUseCase
import ru.gorevmichael.sign_v1.domain.usecases.GenerateSignUseCase
import ru.gorevmichael.sign_v1.domain.usecases.LoadCustomSignConfigsUseCase

data class SignUiState(
    val message: String = "",
    val privateKey: String = "",
    val publicKey: String? = null,
    val signatureJson: String? = null,
    val error: String? = null,
    val configs: List<Pair<String, SignConfig>> = emptyList(),
    val selectedConfig: Pair<String, SignConfig> = "secp256k1" to Secp256k1SignConfig()
)

class SignScreenViewModel(
    private val generateSignUseCase: GenerateSignUseCase = GenerateSignUseCase(),
    private val generatePublicKeyUseCase: GeneratePublicKeyUseCase = GeneratePublicKeyUseCase(),
    private val loadCustomSignConfigsUseCase: LoadCustomSignConfigsUseCase = LoadCustomSignConfigsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUiState())
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
        _uiState.update { it.copy(selectedConfig = config, error = null) }
    }

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(message = message, error = null) }
    }

    fun onPrivateKeyChanged(privateKey: String) {
        _uiState.update { it.copy(privateKey = privateKey, error = null) }
    }

    fun generateSignature() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.message.isBlank() || state.privateKey.isBlank()) {
                _uiState.update { it.copy(error = "Все поля должны быть заполнены!") }
                return@launch
            }

            try {
                val pk = KBigInteger.fromString(state.privateKey)
                val (r, s) = generateSignUseCase(
                    message = state.message,
                    signConfig = state.selectedConfig.second,
                    privateKey = pk
                )
                generatePublicKey()

                val json = """
                {
                  "r": "$r",
                  "s": "$s"
                }
            """.trimIndent()

                _uiState.update { it.copy(signatureJson = json, error = null) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(error = "Error: ${e.message ?: "Некорректный приватный ключ!"}") }
            }
        }
    }

    private fun generatePublicKey() {
        viewModelScope.launch {
            val state = _uiState.value
            val privateKey = state.privateKey
            require(privateKey.isNotBlank()) { "Приватный ключ не может быть пустым!" }
            val pubKey =
                generatePublicKeyUseCase(
                    privateKey.toKBigInteger(),
                    state.selectedConfig.second.generationPoint
                )
            val json = """
                {
                  "x": "${pubKey.x}",
                  "y": "${pubKey.y}"
                }
            """.trimIndent()
            _uiState.update {
                it.copy(publicKey = json)
            }
        }
    }
}
