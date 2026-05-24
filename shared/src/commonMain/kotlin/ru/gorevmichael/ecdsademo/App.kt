package ru.gorevmichael.ecdsademo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.gorevmichael.ecdsademo.presentation.ui.SignScreen
import ru.gorevmichael.ecdsademo.presentation.ui.VerifyScreen

enum class Screen {
    Sign, Verify
}

@Composable
@Preview
fun App() {
    //FIXME переделать с использованием derivedState(?)
    var currentScreen by remember { mutableStateOf(Screen.Sign) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Sign,
                        onClick = { currentScreen = Screen.Sign },
                        label = { Text("Генерация подписи") },
                        icon = { 
                            Text(text = "\uD83E\uDDEC", style = MaterialTheme.typography.titleLarge)
                        }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Verify,
                        onClick = { currentScreen = Screen.Verify },
                        label = { Text("Проверка подписи") },
                        icon = { 
                            Text(text = "✔\uFE0F", style = MaterialTheme.typography.titleLarge)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.Sign -> SignScreen()
                    Screen.Verify -> VerifyScreen()
                }
            }
        }
    }
}