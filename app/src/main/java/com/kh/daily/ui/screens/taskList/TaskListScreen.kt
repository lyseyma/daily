package com.kh.daily.ui.screens.taskList
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import com.kh.daily.R
import androidx.core.content.edit
import com.kh.daily.widget.receiver.TaskWidgetReceiver
import com.kh.daily.widget.data.Task
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Surface
import com.kh.daily.ui.components.addTask.AddTaskBottomSheet
import com.kh.daily.ui.components.popup.DatePickerDialog
import java.time.Instant
import java.time.ZoneOffset

/**
 * Loads tasks from SharedPreferences on app startup.
 * This ensures tasks are available even when offline or when Firebase fails.
 */
private fun loadTasksFromPrefs(context: Context): List<Task> {
    return try {
        val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val taskListJson = prefs.getString("task_list", "[]") ?: "[]"
        val gson = Gson()
        val type = object : TypeToken<List<Task>>() {}.type
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    navController: NavHostController,
    viewModel: TaskViewModel
) {

    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddTaskBottomSheet by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
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

    if (showAddTaskBottomSheet) {
        AddTaskBottomSheet(
            title = taskTitle,
            onTitleChange = { taskTitle = it },
            description = taskDescription,
            onDescriptionChange = { taskDescription = it },
            selectedDate = selectedDate,
            onSelectedDateChange = { selectedDate = it },
            onDismiss = {
                taskTitle = ""
                taskDescription = ""
                showAddTaskBottomSheet = false
            },
            onConfirm = {
                if (taskTitle.isNotBlank()) {
                    val newTask = Task(
                        title = taskTitle.trim(),
                        description = taskDescription.trim(),
                        category = "",
                        dueDate = selectedDate.trim(),
                        isCompleted = false
                    )
                    viewModel.createTask(newTask, context)
                    taskTitle = ""
                    taskDescription = ""
                    showAddTaskBottomSheet = false
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddTaskBottomSheet = true
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

