package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    var task by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }

    val coroutineScope = rememberCoroutineScope()

    // Ambil data dari DataStore saat pertama kali
    LaunchedEffect(Unit) {
        dataStoreManager.tasksFlow.collect { data ->
            if (data.isNotEmpty()) {
                taskList = data.split("|").map {
                    val (t, done) = it.split("::")
                    t to done.toBoolean()
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = task,
            onValueChange = { task = it },
            label = { Text("Tambah Tugas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (task.isNotBlank()) {
                    val updatedList = taskList + (task to false)
                    taskList = updatedList
                    task = ""

                    val saveData = updatedList.joinToString("|") { "${it.first}::${it.second}" }
                    coroutineScope.launch {
                        dataStoreManager.saveTasks(saveData)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(taskList) { (item, isDone) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = {
                                val updatedList = taskList.map {
                                    if (it.first == item) it.first to !it.second else it
                                }
                                taskList = updatedList

                                val saveData = updatedList.joinToString("|") { "${it.first}::${it.second}" }
                                coroutineScope.launch {
                                    dataStoreManager.saveTasks(saveData)
                                }
                            }
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    IconButton(onClick = {
                        val updatedList = taskList.filterNot { it.first == item }
                        taskList = updatedList

                        val saveData = updatedList.joinToString("|") { "${it.first}::${it.second}" }
                        coroutineScope.launch {
                            dataStoreManager.saveTasks(saveData)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Tugas"
                        )
                    }
                }
            }
        }
    }
}
