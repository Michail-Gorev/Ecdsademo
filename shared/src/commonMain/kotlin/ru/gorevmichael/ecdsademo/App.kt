package ru.gorevmichael.ecdsademo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.gorevmichael.ecdsademo.presentation.ui.CreateCustomSignConfigScreen
import ru.gorevmichael.ecdsademo.presentation.ui.SignScreen
import ru.gorevmichael.ecdsademo.presentation.ui.VerifyScreen

enum class Screen {
    Sign, Verify, CreateConfig
}

@Composable
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
                    NavigationBarItem(
                        selected = currentScreen == Screen.CreateConfig,
                        onClick = { currentScreen = Screen.CreateConfig },
                        label = { Text("Создание конфига") },
                        icon = {
                            Text(text = "\uD83C\uDFA8", style = MaterialTheme.typography.titleLarge)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.Sign -> SignScreen()
                    Screen.Verify -> VerifyScreen()
                    Screen.CreateConfig -> CreateCustomSignConfigScreen()
                }
            }
        }
    }
}