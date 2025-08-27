package com.kh.daily.presentation.taskList

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kh.daily.R
import com.kh.dilywidgtet.data.Task
import com.valentinilk.shimmer.shimmer

@Composable
fun TaskCard(navController: NavHostController,task: Task ) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardColors(
            contentColor = if (isSystemInDarkTheme()) colorResource(R.color.colorPrimaryDark) else colorResource(
                R.color.colorPrimary
            ),
            disabledContainerColor = if (isSystemInDarkTheme()) colorResource(R.color.colorPrimaryDark) else colorResource(
                R.color.colorPrimary
            ),
            disabledContentColor = if (isSystemInDarkTheme()) colorResource(R.color.colorPrimaryDark) else colorResource(
                R.color.colorPrimary
            ),
            containerColor = if (isSystemInDarkTheme()) colorResource(R.color.colorPrimaryDark) else colorResource(
                R.color.colorPrimary
            )
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.currentBackStackEntry?.savedStateHandle?.set("takData",task)
                    navController.navigate("detail_screen")
                           },

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.White)
            }
            Text(
                text = task.description,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Created at ${task.dueDate}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun ShimmerLoadingList() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shimmer()  // Apply shimmer effect
                    .background(Color.Gray, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ShimmerEffectItem() {
    val alphaAnim = rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.Gray.copy(alpha = alphaAnim.value))
    )
}
