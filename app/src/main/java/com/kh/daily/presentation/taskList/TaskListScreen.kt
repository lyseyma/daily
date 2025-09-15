package com.kh.daily.presentation.taskList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kh.daily.data.remote.task.TaskViewModel
import com.google.gson.Gson
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.kh.daily.R
import androidx.core.content.edit
import com.kh.daily.widget.receiver.TaskWidgetReceiver
import com.kh.daily.widget.data.Task
import android.util.Log

/**
 * Loads tasks from SharedPreferences on app startup.
 * This ensures tasks are available even when offline or when Firebase fails.
 */
private fun loadTasksFromPrefs(context: Context): List<Task> {
    return try {
        val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val taskListJson = prefs.getString("task_list", "[]") ?: "[]"
        val gson = Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<Task>>() {}.type
        val tasks: List<Task> = gson.fromJson(taskListJson, type) ?: emptyList()
        Log.d("TaskListScreen", "Loaded ${tasks.size} tasks from SharedPreferences")
        tasks
    } catch (e: Exception) {
        Log.e("TaskListScreen", "Error loading tasks from SharedPreferences: ${e.message}")
        emptyList()
    }
}

/**
 * Saves tasks to SharedPreferences for persistence.
 * This ensures tasks are always available locally.
 */
private fun saveTasksToPrefs(context: Context, tasks: List<Task>) {
    try {
        val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val taskListJson = gson.toJson(tasks)
        prefs.edit {
            putString("task_list", taskListJson)
            putLong("tasks_last_updated", System.currentTimeMillis())
        }
        Log.d("TaskListScreen", "Saved ${tasks.size} tasks to SharedPreferences")
    } catch (e: Exception) {
        Log.e("TaskListScreen", "Error saving tasks to SharedPreferences: ${e.message}")
    }
}

/**
 * Notifies the widget to update when task data changes.
 * This function triggers a refresh of all widget instances.
 */
private fun notifGlanceWidget(context: Context) {
    try {
        Log.d("TaskListScreen", "Notifying widget of data changes")
        TaskWidgetReceiver.updateAllWidgets(context)
    } catch (e: Exception) {
        Log.e("TaskListScreen", "Error notifying widget: ${e.message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    navController: NavHostController,
    viewModel: TaskViewModel
) {

    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Load tasks from SharedPreferences on first composition
    LaunchedEffect(Unit) {
        val savedTasks = loadTasksFromPrefs(context)
        if (savedTasks.isNotEmpty()) {
            // If we have saved tasks, use them first while loading from Firebase
            Log.d(
                "TaskListScreen",
                "Using ${savedTasks.size} saved tasks while loading from Firebase"
            )
        }
        viewModel.fetchTasksIfNeeded(context)
    }

    // Save tasks to SharedPreferences and notify widget when tasks change
    LaunchedEffect(tasks) {
        if (tasks.isNotEmpty()) {
            saveTasksToPrefs(context, tasks)
            notifGlanceWidget(context)
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            title = taskTitle,
            onTitleChange = { taskTitle = it },
            onDismiss = {
            taskTitle = ""
                showAddTaskDialog = false
            },
            onConfirm = {
                if (taskTitle.isNotBlank()) {
                    val newTask = Task(
                        title = taskTitle.trim(),
                        description = "",
                        category = "",
                        dueDate = "",
                        isCompleted = false
                    )
                    viewModel.createTask(newTask, context)
                    taskTitle = ""
                    showAddTaskDialog = false
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddTaskDialog = true
                },
                containerColor = colorResource(id = R.color.colorPrimary),
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(Icons.Filled.AddCircle, contentDescription = "Add Task", tint = Color.White)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Welcome M Daily", color = colorResource(id = R.color.colorPrimary)) },
                actions = {
                    IconButton(onClick = {/* Settings */  }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.Gray)
                    }
                },
                modifier = Modifier.background(color = Color.Gray)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "LIST OF TODO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.colorPrimary),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Scrollable content
            if (isLoading) {
                // Show shimmer loading while fetching from Firebase, but also check for saved tasks
                val savedTasks = remember { loadTasksFromPrefs(context) }
                if (savedTasks.isNotEmpty()) {
                    // Show saved tasks while loading
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(savedTasks) { task ->
                            TaskCard(navController, task)
                        }
                    }
                } else {
                    // Show shimmer if no saved tasks
                    LazyColumn {
                        items(5) {
                            ShimmerLoadingList()
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tasks) { task ->
                        TaskCard(navController, task)
                    }

                    // Show message if no tasks
                    if (tasks.isEmpty()) {
                        item {
                            Text(
                                text = "No tasks yet. Tap + to add your first task!",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Task",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.colorPrimary)
            )
        },
        text = {
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Enter task title") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.colorPrimary)
                )
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
