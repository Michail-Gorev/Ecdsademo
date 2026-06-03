package ru.gorevmichael.ecdsademo.presentation.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.gorevmichael.ecdsademo.presentation.viewmodels.VerifyScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    viewModel: VerifyScreenViewModel = viewModel { VerifyScreenViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Проверка подписи",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.selectedConfig.first,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Конфигурация подписи") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.configs.forEach { config ->
                        DropdownMenuItem(
                            text = { Text(config.first) },
                            onClick = {
                                viewModel.onConfigSelected(config)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.message,
                onValueChange = { viewModel.onMessageChanged(it) },
                label = { Text("Сообщение") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Введите сообщение") },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = uiState.publicKeyJson,
                onValueChange = { viewModel.onPublicKeyChanged(it) },
                label = { Text("Публичный ключ (JSON)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("{\"x\": \"...\", \"y\": \"...\"}") },
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = uiState.signatureJson,
                onValueChange = { viewModel.onSignatureChanged(it) },
                label = { Text("Подпись (JSON)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("{\"r\": \"...\", \"s\": \"...\"}") },
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isValid) "✅ Подпись верна" else "❌ Подпись неверна",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (isValid) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}
