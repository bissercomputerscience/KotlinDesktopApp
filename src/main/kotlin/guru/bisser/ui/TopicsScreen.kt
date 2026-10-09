package guru.bisser.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import guru.bisser.entity.Topic
import java.util.UUID

@Composable
fun TopicsScreen(
    topics: List<Topic>,
    error: String?,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // null means the root level: topics without a parent.
    var currentParentId by remember { mutableStateOf<UUID?>(null) }
    val currentParent = topics.find { it.id == currentParentId }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(onClick = onAddClick) {
            Text("add topic")
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        currentParent?.let { parent ->
            TextButton(onClick = { currentParentId = parent.parentId }) {
                Text("← Back")
            }
            Text(parent.name, style = MaterialTheme.typography.titleMedium)
        }

        topics
            .filter { it.parentId == currentParentId }
            .forEach { topic ->
                Text(
                    text = topic.name,
                    modifier = Modifier.clickable { currentParentId = topic.id },
                )
            }
    }
}
