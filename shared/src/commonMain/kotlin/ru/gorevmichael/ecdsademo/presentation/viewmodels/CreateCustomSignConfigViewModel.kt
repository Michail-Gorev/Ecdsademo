package ru.gorevmichael.ecdsademo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gorevmichael.math.domain.models.CurveConfig
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign_v1.domain.models.SignConfig
import ru.gorevmichael.sign_v1.domain.usecases.SaveCustomSignConfigUseCase

data class CreateCustomSignConfigUiState(
    val name: String = "",
    val a: String = "",
    val b: String = "",
    val p: String = "",
    val gx: String = "",
    val gy: String = "",
    val n: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
)

class CreateCustomSignConfigViewModel(
    private val saveCustomSignConfigUseCase: SaveCustomSignConfigUseCase = SaveCustomSignConfigUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCustomSignConfigUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, isSaved = false, error = null) }
    fun onAChanged(value: String) = _uiState.update { it.copy(a = value, isSaved = false, error = null) }
    fun onBChanged(value: String) = _uiState.update { it.copy(b = value, isSaved = false, error = null) }
    fun onPChanged(value: String) = _uiState.update { it.copy(p = value, isSaved = false, error = null) }
    fun onGxChanged(value: String) = _uiState.update { it.copy(gx = value, isSaved = false, error = null) }
    fun onGyChanged(value: String) = _uiState.update { it.copy(gy = value, isSaved = false, error = null) }
    fun onNChanged(value: String) = _uiState.update { it.copy(n = value, isSaved = false, error = null) }

    fun saveConfig() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank() || state.a.isBlank() || state.b.isBlank() || 
                state.p.isBlank() || state.gx.isBlank() || state.gy.isBlank() || state.n.isBlank()) {
                _uiState.update { it.copy(error = "Все поля должны быть заполнены") }
                return@launch
            }

            try {
                val curveConfig = CurveConfig(
                    a = state.a.toBigInteger(10),
                    b = state.b.toBigInteger(10),
                    p = state.p.toBigInteger(10)
                )
                val generationPoint = Point(
                    x = state.gx.toBigInteger(10),
                    y = state.gy.toBigInteger(10),
                    curveConfig = curveConfig
                )
                val signConfig = SignConfig(
                    generationPoint = generationPoint,
                    curveConfig = curveConfig,
                    order = state.n.toBigInteger(10)
                )

                saveCustomSignConfigUseCase(state.name, signConfig)
                _uiState.update { it.copy(isSaved = true, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка сохранения: ${e.message}") }
            }
        }
    }
}
