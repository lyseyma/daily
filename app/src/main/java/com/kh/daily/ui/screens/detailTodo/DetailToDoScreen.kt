package com.kh.daily.ui.screens.detailTodo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kh.daily.R
import com.kh.daily.ui.components.popup.DatePickerDialog
import com.kh.daily.widget.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailToDoScreen(navController: NavController) {
    val data = navController.previousBackStackEntry?.savedStateHandle?.get<Task>("takData")

    Log.d("TAG", "DetailToDoScreen:$data")

    data?.let { task ->
        DetailToDoContent(
            task = task,
            onBackClick = { navController.popBackStack() },
            onTimeClick = { /* action */ },
            onEditClick = { /* action */ },
            onDeleteClick = { /* action */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailToDoContent(
    task: Task,
    onBackClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("", color = colorResource(id = R.color.colorPrimary)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onTimeClick) {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.Black)
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Black)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Filled.Delete, contentDescription = "delete", tint = Color.Black)
                    }
                },
                modifier = Modifier.background(color = Color.Black)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp, start = 15.dp, end = 15.dp)
            )

            Text(
                text = task.description,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = task.dueDate,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 15.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun DetailToDoScreenPreview() {
    // Create mock task data for preview
    val mockTask = Task(
        id = "preview-id",
        title = "Complete Project Presentation",
        description = "Prepare slides for the quarterly project review meeting. Include performance metrics, key achievements, and future roadmap.",
        category = "Work",
        dueDate = "2024-01-15",
        isCompleted = false
    )

    DetailToDoContent(task = mockTask)
}

@Composable
fun DetailToDoScreenCompletedPreview() {
    // Create mock completed task data for preview
    val mockCompletedTask = Task(
        id = "preview-completed-id",
        title = "Buy Groceries",
        description = "Milk, Eggs, Bread, Fruits, and Vegetables for the week",
        category = "Personal",
        dueDate = "2024-01-10",
        isCompleted = true
    )

    DetailToDoContent(task = mockCompletedTask)
}
