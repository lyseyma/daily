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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedDate: String,
    onSelectedDateChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colorResource(id = R.color.colorPrimary), // Coral/salmon background color from design
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White) },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 32.dp), // Extra padding for bottom sheet
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,

        ) {
            // Header with diamond icon and title
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Diamond icon
                Text(
                    text = "◇",
                    color = colorResource(id = R.color.colorPrimary),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "ADD TODO",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.colorPrimary),
                    fontSize = 16.sp
                )
            }
            
            // Title input field
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { 
                    Text(
                        "Title",
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            
            // Description input field (larger)
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = { 
                    Text(
                        "Description",
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 6
            )
            
            // Deadline (Optional) field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { onSelectedDateChange },
                placeholder = {
                    Text(
                        "Deadline (Optional)",
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select Date",
                        tint = Color.White.copy(alpha = 0.7f),

                        modifier = Modifier
                            .clickable {
                                showBottomSheet = true
                            }
                    )
                },

            )
            
            // Add Image (Optional) field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle image input */ },
                placeholder = { 
                    Text(
                        "Add Image (Optional)",
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Image",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            )
            
            // ADD TODO button
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = colorResource(id = R.color.colorPrimary)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "ADD TODO",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = colorResource(R.color.colorPrimary)
                )
            }
        }
    }

    // DatePicker Dialog
    if (showBottomSheet) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { date ->
                onSelectedDateChange(date)
                showBottomSheet = false
            },
            onDismiss = {
                showBottomSheet = false
            }
        )
    }
}

/**
 * Shows a Material3 DatePicker in a Dialog.
 *
 * @param initialDate: Default date string in format "d MMM yyyy", or null.
 * @param onDateSelected: Called with selected date string ("d MMM yyyy").
 * @param onDismiss: Called when dialog is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    initialDate: String?,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Convert initialDate to epoch millis, fallback to today
    // DatePicker expects UTC midnight for the date
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.ENGLISH)
    val today = LocalDate.now()
    val initialLocalDate = try {
        initialDate?.let { LocalDate.parse(it, formatter) } ?: today
    } catch (e: Exception) {
        today
    }

    // Convert to UTC midnight epoch millis (DatePicker expects UTC)
    val initialMillis = initialLocalDate
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.colorPrimary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Material3 DatePicker
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialMillis
                )
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = colorResource(id = R.color.colorPrimary),
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        dayContentColor = Color.White,
                        selectedDayContainerColor = Color.White,
                        selectedDayContentColor = colorResource(id = R.color.colorPrimary),
                        todayContentColor = Color.White,
                        todayDateBorderColor = Color.White,
                        weekdayContentColor = Color.White,
                        subheadContentColor = Color.White,
                        navigationContentColor = Color.White,
                        yearContentColor = Color.White,
                        selectedYearContentColor = colorResource(id = R.color.colorPrimary),
                        selectedYearContainerColor = Color.White,
                        disabledSelectedYearContentColor = Color.White,
                        currentYearContentColor = Color.White,
                        dividerColor = Color.White,

                    )
                )

                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        enabled = datePickerState.selectedDateMillis != null,
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                // Convert from UTC millis back to LocalDate
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                val formattedDate = date.format(formatter)
                                onDateSelected(formattedDate)
                            } ?: onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = colorResource(id = R.color.colorPrimary)
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

