package guru.bisser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
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

// Shared by add and edit; `topics` are the parents the user may pick.
@Composable
fun TopicDialog(
    title: String,
    confirmLabel: String,
    topics: List<Topic>,
    initialName: String,
    initialParentId: UUID?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, parentId: UUID?) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var parent by remember { mutableStateOf(topics.find { it.id == initialParentId }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Topic name") },
                    singleLine = true,
                )
                ParentTopicDropdown(
                    topics = topics,
                    selected = parent,
                    onSelect = { parent = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim(), parent?.id) },
                enabled = name.isNotBlank(),
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

// Starts at the initial parent (empty for a root topic); the "none" item clears the selection so the topic becomes a root.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParentTopicDropdown(
    topics: List<Topic>,
    selected: Topic?,
    onSelect: (Topic?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Parent") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("(none)") },
                onClick = {
                    onSelect(null)
                    expanded = false
                },
            )
            topics.forEach { topic ->
                DropdownMenuItem(
                    text = { Text(topic.name) },
                    onClick = {
                        onSelect(topic)
                        expanded = false
                    },
                )
            }
        }
    }
}
