package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.Checkbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todolistapp.ui.theme.ToDoListAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ToDoApp()
                }
            }
        }
    }
}

@Composable
fun ToDoApp() {
    var task by remember { mutableStateOf("") }
    var assignee by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<Triple<String, String, Boolean>>()) }

    var isEditing by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = task,
            onValueChange = { task = it },
            label = { Text("Nama Tugas") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = assignee,
            onValueChange = { assignee = it },
            label = { Text("Nama Pekerja") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (task.isNotBlank() && assignee.isNotBlank()) {
                    if (isEditing) {
                        taskList = taskList.toMutableList().also {
                            it[editIndex] = Triple(task, assignee, it[editIndex].third)
                        }
                        isEditing = false
                        editIndex = -1
                    } else {
                        taskList = taskList + Triple(task, assignee, false)
                    }
                    task = ""
                    assignee = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Update" else "Tambah")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Header table
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("No", modifier = Modifier.width(30.dp), style = MaterialTheme.typography.bodyMedium)
            Text("Tugas", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Text("Pekerja", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(60.dp)) // for icons
        }

        Divider()

        LazyColumn {
            itemsIndexed(taskList) { index, (item, worker, isDone) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${index + 1}.", modifier = Modifier.width(30.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = {
                                taskList = taskList.toMutableList().also {
                                    it[index] = Triple(item, worker, !isDone)
                                }
                            }
                        )
                        Column {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text("Oleh: $worker", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(modifier = Modifier.width(60.dp)) {
                        IconButton(onClick = {
                            task = item
                            assignee = worker
                            isEditing = true
                            editIndex = index
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Tugas")
                        }

                        IconButton(onClick = {
                            taskList = taskList.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Tugas")
                        }
                    }
                }
            }
        }
    }
}
