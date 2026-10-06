package guru.bisser.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import guru.bisser.entity.Topic
import guru.bisser.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException

@Composable
fun App(topicService: TopicService) {
    
    var showAddTopicDialog by remember { mutableStateOf(false) }
    val topics = remember { mutableStateListOf<Topic>() }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            topics.addAll(withContext(Dispatchers.IO) { topicService.findAll() })
        } catch (e: SQLException) {
            error = "Could not load topics: ${e.message}"
        }
    }

    MaterialTheme {
        Column {
            Text("Hello Kotlin!")
            Button(onClick = { showAddTopicDialog = true }) {
                Text("add topic")
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            topics.forEach { Text(it.name) }
        }

        if (showAddTopicDialog) {
            AddTopicDialog(
                onDismiss = { showAddTopicDialog = false },
                onConfirm = { name ->
                    showAddTopicDialog = false
                    scope.launch {
                        try {
                            topics.add(withContext(Dispatchers.IO) { topicService.create(name) })
                            error = null
                        } catch (e: SQLException) {
                            error = "Could not save topic: ${e.message}"
                        }
                    }
                },
            )
        }
    }
}
