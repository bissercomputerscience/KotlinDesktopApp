package guru.bisser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import guru.bisser.entity.Topic

@Composable
fun StatsScreen(
    topics: List<Topic>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Stats", style = MaterialTheme.typography.titleLarge)
        Text("Topics: ${topics.size}")
    }
}
