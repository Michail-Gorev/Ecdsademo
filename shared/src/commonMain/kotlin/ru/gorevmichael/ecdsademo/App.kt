package ru.gorevmichael.ecdsademo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ru.gorevmichael.core.interfaces.DummyInterface
import ru.gorevmichael.ecdsademo.di.loadFeatureModules
import ru.gorevmichael.ecdsademo.presentation.ui.CreateCustomSignConfigScreen
import ru.gorevmichael.ecdsademo.presentation.ui.SignScreen
import ru.gorevmichael.ecdsademo.presentation.ui.VerifyScreen

enum class Screen {
    Sign, Verify, CreateConfig
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    //TODO инициализацию коина вынести отсюда
    val featuresDI = loadFeatureModules()
    KoinApplication(
        application = {
            modules(featuresDI)
        },
        content = {
            //FIXME переделать с использованием derivedState(?)
            var currentScreen by remember { mutableStateOf(Screen.Sign) }
            //TODO - для тестов, убрать после них
            val supportedFeatures = koinInject<DummyInterface>().dummyMethod()

            MaterialTheme {
                Scaffold(
                    topBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Text(
                                text = "Supported Features: $supportedFeatures"
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen == Screen.Sign,
                                onClick = { currentScreen = Screen.Sign },
                                label = { Text("Генерация подписи") },
                                icon = {
                                    Text(
                                        text = "\uD83E\uDDEC",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            )
                            NavigationBarItem(
                                selected = currentScreen == Screen.Verify,
                                onClick = { currentScreen = Screen.Verify },
                                label = { Text("Проверка подписи") },
                                icon = {
                                    Text(
                                        text = "✔\uFE0F",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            )
                            NavigationBarItem(
                                selected = currentScreen == Screen.CreateConfig,
                                onClick = { currentScreen = Screen.CreateConfig },
                                label = { Text("Создание конфига") },
                                icon = {
                                    Text(
                                        text = "\uD83C\uDFA8",
                                        style = MaterialTheme.typography.titleLarge
                                    )
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
    )
}