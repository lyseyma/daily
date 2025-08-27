package com.kh.dilywidgtet.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kh.dilywidgtet.data.Task

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskGlanceWidget()
}

class TaskGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            Content(context)
        }
    }

    @Composable
    private fun Content(context: Context) {
        val prefs = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val taskListJson = prefs.getString("task_list", "[]")
        val tasks: List<Task> = deserialize(taskListJson)
        Log.d("TAG", "Content: widget content: $tasks")
        // Display tasks as you want!
        MyContent(tasks)
    }

    @Composable
    private fun MyContent(tasks: List<Task>) {

        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello, Glance!")
            // Example: display tasks
            tasks.forEach {
                Text(text = it.title)
            }
        }

    }



    // Function to deserialize JSON to List of Tasks
    private fun deserialize(json: String?): List<Task> {
        val gson = Gson()
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}