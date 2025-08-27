package com.kh.daily.presentation.detailTodo

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kh.daily.R
import com.kh.dilywidgtet.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailToDoScreen(navController: NavController){

    val data = navController.previousBackStackEntry?.savedStateHandle?.get<Task>("takData")

    Log.d("TAG", "DetailToDoScreen:$data ")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("", color = colorResource(id = R.color.colorPrimary)) },
                navigationIcon = {(IconButton(onClick = {navController.popBackStack()}) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.Gray)
                })},
                actions = {

                    IconButton(onClick = {/* action */  }) {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.Gray)
                    }
                    IconButton(onClick = {/* action */  }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }
                    IconButton(onClick = {/* action */  }) {
                        Icon(Icons.Filled.Delete, contentDescription = "delete", tint = Color.Gray)
                    }
                },
                modifier = Modifier.background(color = Color.Gray)
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Text(
                text = data!!.title,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp, start = 15.dp, end = 15.dp)
            )

            Text(
                text = data.description,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, start = 15.dp, end = 15.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = data.dueDate,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

        }
    }
}


