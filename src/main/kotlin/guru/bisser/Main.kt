package guru.bisser

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import guru.bisser.config.DatabaseConfig
import guru.bisser.repository.TopicRepository
import guru.bisser.service.TopicService
import guru.bisser.ui.App

fun main() {
    val topicRepository = TopicRepository(DatabaseConfig())
    topicRepository.createTableIfNotExists()
    println("Connected to MySQL!")
    val topicService = TopicService(topicRepository)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Kotlin Learning App",
        ) {
            App(topicService)
        }
    }
}
