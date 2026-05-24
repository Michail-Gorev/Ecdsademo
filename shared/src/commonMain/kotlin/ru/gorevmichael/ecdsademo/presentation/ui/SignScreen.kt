package ru.gorevmichael.ecdsademo.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.gorevmichael.ecdsademo.presentation.viewmodels.SignScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignScreen(
    viewModel: SignScreenViewModel = viewModel { SignScreenViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ECDSA Signing",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = uiState.message,
            onValueChange = { viewModel.onMessageChanged(it) },
            label = { Text("Сообщение для подписи") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите сообщение") }
        )

        OutlinedTextField(
            value = uiState.privateKey,
            onValueChange = { viewModel.onPrivateKeyChanged(it) },
            label = { Text("Приватный ключ (десятичный формат)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите приватный ключ (десятичный формат)") }
        )

        Button(
            onClick = { viewModel.generateSignature() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Сгенерировать подпись")
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        uiState.signatureJson?.let { json ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Подпись (JSON)",
                            style = MaterialTheme.typography.labelLarge
                        )
                        TextButton(onClick = {
                            clipboardManager.setText(AnnotatedString(json))
                        }) {
                            Text("Скопировать")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.05f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = json,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        uiState.publicKey?.let { json ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Публичный ключ (JSON)",
                            style = MaterialTheme.typography.labelLarge
                        )
                        TextButton(onClick = {
                            clipboardManager.setText(AnnotatedString(json))
                        }) {
                            Text("Скопировать")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.05f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = json,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
