package ru.gorevmichael.ecdsademo.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.gorevmichael.ecdsademo.presentation.viewmodels.CreateCustomSignConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomSignConfigScreen(
    viewModel: CreateCustomSignConfigViewModel = viewModel { CreateCustomSignConfigViewModel() },
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Новая конфигурация",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChanged(it) },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text("Параметры кривой (y² = x³ + ax + b mod p)", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = uiState.a,
            onValueChange = { viewModel.onAChanged(it) },
            label = { Text("Параметр a") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.b,
            onValueChange = { viewModel.onBChanged(it) },
            label = { Text("Параметр b") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.p,
            onValueChange = { viewModel.onPChanged(it) },
            label = { Text("Модуль p") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text("Базовая точка G(x, y)", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = uiState.gx,
            onValueChange = { viewModel.onGxChanged(it) },
            label = { Text("Координата x") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.gy,
            onValueChange = { viewModel.onGyChanged(it) },
            label = { Text("Координата y") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text("Порядок группы n", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = uiState.n,
            onValueChange = { viewModel.onNChanged(it) },
            label = { Text("Порядок n") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = { viewModel.saveConfig() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Сохранить")
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
