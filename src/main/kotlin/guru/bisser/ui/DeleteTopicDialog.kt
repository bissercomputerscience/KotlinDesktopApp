package guru.bisser.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import guru.bisser.entity.Topic

@Composable
fun DeleteTopicDialog(
    topic: Topic,
    descendantCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete topic") },
        text = {
            Text(
                if (descendantCount == 0) {
                    "Delete \"${topic.name}\"?"
                } else {
                    "Delete \"${topic.name}\" and its $descendantCount subtopic(s)?"
                },
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
