package guru.bisser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import guru.bisser.entity.Topic

@Composable
fun TopicsScreen(
    topics: List<Topic>,
    error: String?,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(onClick = onAddClick) {
            Text("add topic")
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        topics.forEach { Text(it.name) }
    }
}
