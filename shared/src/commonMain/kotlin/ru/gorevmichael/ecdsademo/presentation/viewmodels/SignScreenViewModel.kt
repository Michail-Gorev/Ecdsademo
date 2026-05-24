package ru.gorevmichael.ecdsademo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.github.gatrongdev.kbignum.math.KBigInteger
import io.github.gatrongdev.kbignum.math.toKBigInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gorevmichael.sign.data.secp256_k1.Secp256k1SignConfig
import ru.gorevmichael.sign.domain.usecases.GeneratePublicKeyUseCase
import ru.gorevmichael.sign.domain.usecases.GenerateSignUseCase

data class SignUiState(
    val message: String = "",
    val privateKey: String = "",
    val publicKey: String? = null,
    val signatureJson: String? = null,
    val error: String? = null
)

class SignScreenViewModel(
    private val generateSignUseCase: GenerateSignUseCase = GenerateSignUseCase(),
    private val generatePublicKeyUseCase: GeneratePublicKeyUseCase = GeneratePublicKeyUseCase(),
    private val signConfig: Secp256k1SignConfig = Secp256k1SignConfig()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUiState())
    private val _viewModelScope = CoroutineScope(Dispatchers.IO)
    val uiState = _uiState.asStateFlow()

    fun onMessageChanged(message: String) {
        _viewModelScope.launch {
            _uiState.update { it.copy(message = message, error = null) }
        }
    }

    fun onPrivateKeyChanged(privateKey: String) {
        _viewModelScope.launch {
            _uiState.update { it.copy(privateKey = privateKey, error = null) }
        }
    }

    fun generateSignature() {
        _viewModelScope.launch {
            val state = _uiState.value
            if (state.message.isBlank() || state.privateKey.isBlank()) {
                _uiState.update { it.copy(error = "Все поля должны быть заполнены!") }
                return@launch
            }

            try {
                val pk = KBigInteger.fromString(state.privateKey)
                val (r, s) = generateSignUseCase(
                    message = state.message,
                    signConfig = signConfig,
                    privateKey = pk
                )
                generatePublicKey()

                val json = """
                {
                  "r": "${r}",
                  "s": "${s}"
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
        _viewModelScope.launch {
            val privateKey = _uiState.value.privateKey
            require(privateKey.isNotBlank()) { "Приватный ключ не может быть пустым!" }
            val pubKey =
                generatePublicKeyUseCase(privateKey.toKBigInteger(), signConfig.generationPoint)
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
