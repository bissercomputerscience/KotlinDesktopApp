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

@Composable
fun App(
    topicService: TopicService,
    onExit: () -> Unit,
) {
    var currentScreen by remember { mutableStateOf(Screen.TOPICS) }
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
                        onAddClick = { showAddTopicDialog = true },
                        modifier = contentModifier,
                    )

                Screen.STATS -> StatsScreen(topics = topics, modifier = contentModifier)
            }
        }

        if (showAddTopicDialog) {
            AddTopicDialog(
                topics = topics,
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
    }
}
