package guru.bisser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import guru.bisser.entity.Topic
import guru.bisser.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.util.UUID

@Composable
fun App(
    topicService: TopicService,
    onExit: () -> Unit,
) {
    var currentScreen by remember { mutableStateOf(Screen.TOPICS) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var addTopicParentId by remember { mutableStateOf<UUID?>(null) }
    var editingTopic by remember { mutableStateOf<Topic?>(null) }
    var deletingTopic by remember { mutableStateOf<Topic?>(null) }
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
        Row(modifier = Modifier.fillMaxSize()) {
            // Menu: fixed width on the left.
            Column(
                modifier =
                    Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Screen.entries.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        selected = screen == currentScreen,
                        onClick = { currentScreen = screen },
                    )
                }

                // Pushes Exit to the bottom of the menu.
                Spacer(Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Exit") },
                    selected = false,
                    onClick = onExit,
                )
            }

            VerticalDivider()

            // Main content: shows the screen picked in the menu.
            val contentModifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            when (currentScreen) {
                Screen.TOPICS ->
                    TopicsScreen(
                        topics = topics,
                        error = error,
                        onAddClick = { parentId ->
                            addTopicParentId = parentId
                            showAddTopicDialog = true
                        },
                        onEditClick = { editingTopic = it },
                        onDeleteClick = { deletingTopic = it },
                        modifier = contentModifier,
                    )

                Screen.STATS -> StatsScreen(topics = topics, modifier = contentModifier)
            }
        }

        if (showAddTopicDialog) {
            TopicDialog(
                title = "New topic",
                confirmLabel = "Add",
                topics = topics,
                initialName = "",
                initialParentId = addTopicParentId,
                onDismiss = { showAddTopicDialog = false },
                onConfirm = { name, parentId ->
                    showAddTopicDialog = false
                    scope.launch {
                        try {
                            topics.add(withContext(Dispatchers.IO) { topicService.create(name, parentId) })
                            error = null
                        } catch (e: SQLException) {
                            error = "Could not save topic: ${e.message}"
                        }
                    }
                },
            )
        }

        editingTopic?.let { topic ->
            TopicDialog(
                title = "Edit topic",
                confirmLabel = "Save",
                topics = topicService.allowedParents(topic, topics),
                initialName = topic.name,
                initialParentId = topic.parentId,
                onDismiss = { editingTopic = null },
                onConfirm = { name, parentId ->
                    editingTopic = null
                    scope.launch {
                        try {
                            val updated = withContext(Dispatchers.IO) { topicService.update(topic.id, name, parentId) }
                            topics[topics.indexOfFirst { it.id == updated.id }] = updated
                            error = null
                        } catch (e: SQLException) {
                            error = "Could not save topic: ${e.message}"
                        } catch (e: IllegalArgumentException) {
                            error = "Could not save topic: ${e.message}"
                        }
                    }
                },
            )
        }

        deletingTopic?.let { topic ->
            DeleteTopicDialog(
                topic = topic,
                descendantCount = topicService.subtree(topic, topics).size - 1,
                onDismiss = { deletingTopic = null },
                onConfirm = {
                    deletingTopic = null
                    scope.launch {
                        try {
                            val deleted = withContext(Dispatchers.IO) { topicService.delete(topic.id) }
                            topics.removeAll { it.id in deleted }
                            error = null
                        } catch (e: SQLException) {
                            error = "Could not delete topic: ${e.message}"
                        }
                    }
                },
            )
        }
    }
}
