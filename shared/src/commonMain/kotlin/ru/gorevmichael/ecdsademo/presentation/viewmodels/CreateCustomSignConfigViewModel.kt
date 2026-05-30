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
import ru.gorevmichael.math.domain.models.CurveConfig
import ru.gorevmichael.math.domain.models.Point
import ru.gorevmichael.sign.domain.models.SignConfig
import ru.gorevmichael.sign.domain.usecases.SaveCustomSignConfigUseCase

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
    private val _viewModelScope = CoroutineScope(Dispatchers.IO)
    val uiState = _uiState.asStateFlow()

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, isSaved = false, error = null) }
    fun onAChanged(value: String) = _uiState.update { it.copy(a = value, isSaved = false, error = null) }
    fun onBChanged(value: String) = _uiState.update { it.copy(b = value, isSaved = false, error = null) }
    fun onPChanged(value: String) = _uiState.update { it.copy(p = value, isSaved = false, error = null) }
    fun onGxChanged(value: String) = _uiState.update { it.copy(gx = value, isSaved = false, error = null) }
    fun onGyChanged(value: String) = _uiState.update { it.copy(gy = value, isSaved = false, error = null) }
    fun onNChanged(value: String) = _uiState.update { it.copy(n = value, isSaved = false, error = null) }

    fun saveConfig() {
        _viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank() || state.a.isBlank() || state.b.isBlank() || 
                state.p.isBlank() || state.gx.isBlank() || state.gy.isBlank() || state.n.isBlank()) {
                _uiState.update { it.copy(error = "Все поля должны быть заполнены") }
                return@launch
            }

            try {
                val curveConfig = CurveConfig(
                    a = KBigInteger.fromString(state.a),
                    b = KBigInteger.fromString(state.b),
                    p = KBigInteger.fromString(state.p)
                )
                val generationPoint = Point(
                    x = KBigInteger.fromString(state.gx),
                    y = KBigInteger.fromString(state.gy),
                    curveConfig = curveConfig
                )
                val signConfig = SignConfig(
                    generationPoint = generationPoint,
                    curveConfig = curveConfig,
                    order = KBigInteger.fromString(state.n)
                )

                saveCustomSignConfigUseCase(state.name, signConfig)
                _uiState.update { it.copy(isSaved = true, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка сохранения: ${e.message}") }
            }
        }
    }
}
