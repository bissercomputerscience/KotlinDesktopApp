package guru.bisser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import guru.bisser.guru.bisser.config.DatabaseConfig
import java.sql.DriverManager


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kotlin Learning App"
    ) {
        App()
    }
}

@Composable
fun App() {
    MaterialTheme {
        Text("Hello Kotlin!")
    }
}       