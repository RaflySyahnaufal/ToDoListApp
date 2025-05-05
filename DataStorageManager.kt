package com.example.todolistapp

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "todo_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val TASKS_KEY = stringPreferencesKey("tasks_key")
    }

    // Aliran data dari DataStore
    val tasksFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TASKS_KEY] ?: ""
        }

    // Simpan data tugas ke DataStore
    suspend fun saveTasks(data: String) {
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = data
        }
    }
}
