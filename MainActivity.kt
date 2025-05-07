package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todolistapp.ui.theme.ToDoListAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ToDoApp()
                }
            }
        }
    }
}

@Composable
fun ToDoApp() {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    var task by remember { mutableStateOf("") }
    var assignee by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<Triple<String, String, Boolean>>()) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    fun saveToDataStore() {
        val saveData = taskList.joinToString("|") { "${it.first}::${it.second}::${it.third}" }
        scope.launch {
            dataStoreManager.saveTasks(saveData)
        }
    }

    // Load data from DataStore
    LaunchedEffect(Unit) {
        dataStoreManager.tasksFlow.collect { data ->
            if (data.isNotEmpty()) {
                taskList = data.split("|").map {
                    val parts = it.split("::")
                    if (parts.size == 3) {
                        Triple(parts[0], parts[1], parts[2].toBoolean())
                    } else Triple("", "", false)
                }
            }
        }
    }

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
            label = { Text("Nama Petugas") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (task.isNotBlank() && assignee.isNotBlank()) {
                    if (editingIndex == null) {
                        taskList = taskList + Triple(task, assignee, false)
                    } else {
                        taskList = taskList.toMutableList().also {
                            it[editingIndex!!] = Triple(task, assignee, false)
                        }
                        editingIndex = null
                    }
                    task = ""
                    assignee = ""
                    saveToDataStore()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingIndex == null) "Tambah" else "Update")
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(taskList) { index, (item, name, isDone) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = {
                                taskList = taskList.toMutableList().also {
                                    it[index] = Triple(item, name, !isDone)
                                }
                                saveToDataStore()
                            }
                        )
                        Column {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(
                                text = "Petugas: $name",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = {
                            task = item
                            assignee = name
                            editingIndex = index
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            taskList = taskList.toMutableList().also { it.removeAt(index) }
                            saveToDataStore()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            }
        }
    }
}
