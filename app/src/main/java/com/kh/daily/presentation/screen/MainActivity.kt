package com.kh.daily.presentation.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kh.daily.presentation.Navigation
import com.kh.dilywidgtet.data.Task
import com.kh.daily.theme.MDailyTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set the status bar text color
//        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        // Get the Intent that started the activity
       setContent {
            MDailyTheme {
                Navigation(intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
}

private fun handleDeepLink(intent: Intent?) {
    val data: Uri? = intent?.data
    if (data != null) {
        val oobCode = data.getQueryParameter("oobCode")
        val mode = data.getQueryParameter("mode")


        Log.d("TAG", "OOB Code: $oobCode")
        Log.d("TAG", "mode : $mode")
//        if (oobCode != null && mode == "resetPassword") {
//            println("OOB Code: $oobCode")
//            resetPassword(oobCode, "NewPassword123")
//        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MDailyTheme {
//        Navigation()
    }
}

@Composable
fun TaskItem(task: Task, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Occupies the full width of the parent
            .clickable { onClick() } // Handles user clicks on the task item
            .padding(16.dp) // Adds padding around the item
    ) {
        // Checkbox to mark the task as completed
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { /* Handle completion toggle */ }
        )

        // Task details: Title and category
        Column {
            // Task title
            Text(task.title, style = MaterialTheme.typography.bodySmall)

            // Task category (e.g., Work or Personal)
            Text(task.category, style = MaterialTheme.typography.labelLarge)
        }
    }



}