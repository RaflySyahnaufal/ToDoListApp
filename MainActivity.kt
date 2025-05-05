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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolistapp.ui.theme.ToDoListAppTheme
import androidx.compose.ui.text.style.TextDecoration

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

data class TaskItem(val taskName: String, val assigneeName: String, val isDone: Boolean)

@Composable
fun ToDoApp() {
    var task by remember { mutableStateOf("") }
    var assignee by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<TaskItem>()) }

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
                    taskList = taskList + TaskItem(task, assignee, false)
                    task = ""
                    assignee = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header Tabel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("No", modifier = Modifier.weight(1f))
            Text("Tugas", modifier = Modifier.weight(3f))
            Text("Pekerja", modifier = Modifier.weight(2f))
            Spacer(modifier = Modifier.width(40.dp)) // untuk ikon hapus
        }

        Divider()

        // Isi Tabel
        LazyColumn {
            itemsIndexed(taskList) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(3f)
                    ) {
                        Checkbox(
                            checked = item.isDone,
                            onCheckedChange = {
                                taskList = taskList.mapIndexed { i, taskItem ->
                                    if (i == index) taskItem.copy(isDone = !taskItem.isDone) else taskItem
                                }
                            }
                        )
                        Text(
                            text = item.taskName,
                            textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    Text(
                        text = item.assigneeName,
                        modifier = Modifier.weight(2f)
                    )

                    IconButton(
                        onClick = {
                            taskList = taskList.filterIndexed { i, _ -> i != index }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Tugas")
                    }
                }
            }
        }
    }
}
