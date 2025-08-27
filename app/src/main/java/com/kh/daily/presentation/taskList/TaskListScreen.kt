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
    val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        viewModel.fetchTasksIfNeeded()
    }

    LaunchedEffect(tasks) {
        prefs.edit { putString("task_list", gson.toJson(tasks)) }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            title = taskTitle,
            onTitleChange = { taskTitle = it },
            onDismiss = { showAddTaskDialog = false },
            onConfirm = {
                // TODO: Add task to viewModel
                taskTitle = ""
                showAddTaskDialog = false
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

            if (isLoading){
                LazyColumn {
                    items(5) {
                        ShimmerLoadingList()
                    }
                }
            }else{

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(tasks) { task ->
                        TaskCard(navController, task)
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
