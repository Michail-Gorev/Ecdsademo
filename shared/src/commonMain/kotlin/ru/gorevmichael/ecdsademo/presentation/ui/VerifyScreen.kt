package ru.gorevmichael.ecdsademo.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.gorevmichael.ecdsademo.presentation.viewmodels.VerifyScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    viewModel: VerifyScreenViewModel = viewModel { VerifyScreenViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ECDSA Verification",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = uiState.message,
            onValueChange = { viewModel.onMessageChanged(it) },
            label = { Text("Сообщение") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите сообщение") }
        )

        OutlinedTextField(
            value = uiState.publicKeyJson,
            onValueChange = { viewModel.onPublicKeyChanged(it) },
            label = { Text("Публичный ключ (JSON)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("{\"x\": \"...\", \"y\": \"...\"}") },
            maxLines = 5
        )

        OutlinedTextField(
            value = uiState.signatureJson,
            onValueChange = { viewModel.onSignatureChanged(it) },
            label = { Text("Подпись (JSON)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("{\"r\": \"...\", \"s\": \"...\"}") },
            maxLines = 5
        )

        Button(
            onClick = { viewModel.verifySignature() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Проверить подпись")
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        uiState.isValid?.let { isValid ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isValid) "✅ Подпись верна" else "❌ Подпись неверна",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isValid) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
