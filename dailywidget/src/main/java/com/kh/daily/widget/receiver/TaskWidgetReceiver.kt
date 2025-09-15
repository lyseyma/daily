package com.kh.daily.widget.receiver

import android.appwidget.AppWidgetManager
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import android.content.Context
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.R
import androidx.glance.appwidget.provideContent
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.color.ColorProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kh.daily.widget.data.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TaskWidgetReceiver handles the widget lifecycle and data display.
 * This receiver manages the Glance-based widget that shows tasks from the main app.
 */
class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskGlanceWidget()

    companion object {
        /**
         * Updates all widget instances when called from the main app.
         * This should be called whenever the task list changes in the main app.
         *
         * @param context The application context
         */
        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("TaskWidget", "Triggering widget update from main app")

                    // Use the updateAll extension function to update all widget instances
                    TaskGlanceWidget().updateAll(context)

                    Log.d("TaskWidget", "Widget update completed successfully")
                } catch (e: Exception) {
                    Log.e("TaskWidget", "Error updating widgets: ${e.message}")
                }
            }
        }

        /**
         * Alternative method for updating widgets using GlanceAppWidgetManager
         * This is a backup method in case updateAll doesn't work as expected
         */
        fun forceUpdateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val manager = GlanceAppWidgetManager(context)
                    val glanceIds = manager.getGlanceIds(TaskGlanceWidget::class.java)

                    Log.d("TaskWidget", "Force updating ${glanceIds.size} widget instances")

                    glanceIds.forEach { glanceId ->
                        TaskGlanceWidget().update(context, glanceId)
                    }
                } catch (e: Exception) {
                    Log.e("TaskWidget", "Error force updating widgets: ${e.message}")
                }
            }
        }
    }
}

/**
 * TaskGlanceWidget is the main widget implementation using Jetpack Glance.
 * It reads task data from SharedPreferences that's written by the main app.
 */
class TaskGlanceWidget : GlanceAppWidget() {

    // Define colors using context.getColor() for proper resource handling
    private fun getWidgetColors(context: Context) = object {
        val colorPrimary = Color(context.getColor(com.kh.daily.widget.R.color.colorPrimary))
        val colorWhite = Color(context.getColor(com.kh.daily.widget.R.color.white))
        val colorWhiteSecondary =
            Color(context.getColor(com.kh.daily.widget.R.color.widget_text_secondary))
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TaskWidgetContent(context)
        }
    }

    /**
     * Creates a ComponentName to open the main app's MainActivity.
     * This will navigate to the TaskList screen.
     */
    private fun createMainAppComponentName(context: Context): ComponentName {
        return try {
            val mainAppPackage = "com.kh.daily"
            val activityClass = "$mainAppPackage.presentation.screen.MainActivity"

            Log.d("TaskWidget", "Creating ComponentName for: $activityClass")
            ComponentName(mainAppPackage, activityClass)
        } catch (e: Exception) {
            Log.e("TaskWidget", "Error creating ComponentName: ${e.message}")
            // Fallback to the current package
            ComponentName(context.packageName, "${context.packageName}.MainActivity")
        }
    }

    /**
     * Main content composable for the widget.
     * Reads tasks from SharedPreferences and displays them in a list format.
     */
    @Composable
    private fun TaskWidgetContent(context: Context) {
        // Read tasks from the same SharedPreferences key used by TaskListScreen
        val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val taskListJson = prefs.getString("task_list", "[]")
        val tasks: List<Task> = deserializeTasks(taskListJson)

        Log.d("TaskWidget", "Widget loaded with ${tasks.size} tasks: $tasks")

        // Display the task list in widget format with click action
        TaskListWidget(tasks, context)
    }

    /**
     * Widget UI layout displaying the list of tasks.
     * Shows a header and up to 5 tasks with colorPrimary background and white text.
     * The entire widget is clickable and opens the main app.
     */
    @Composable
    private fun TaskListWidget(tasks: List<Task>, context: Context) {
        val colors = getWidgetColors(context)
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = colors.colorPrimary, night = colors.colorPrimary))
                .padding(12.dp)
                .clickable(
                    onClick = actionStartActivity(
                        componentName = createMainAppComponentName(context)
                    )
                ),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Widget header with emoji and white text
            Text(
                text = "📋 Daily Tasks",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = ColorProvider(day = colors.colorWhite, night = colors.colorWhite)
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Display tasks (limit to 5 for widget space)
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks available",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(
                            day = colors.colorWhite,
                            night = colors.colorWhite
                        )
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = "Tap to open app and add tasks",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = ColorProvider(
                            day = colors.colorWhite,
                            night = colors.colorWhite
                        )
                    )
                )
            } else {
                tasks.take(5).forEach { task ->
                    TaskItemWidget(task, context)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }

                // Show count if there are more tasks
                if (tasks.size > 5) {
                    Text(
                        text = "+ ${tasks.size - 5} more tasks",
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = ColorProvider(
                                day = colors.colorWhite,
                                night = colors.colorWhite
                            )
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))
                // Add tap instruction
                Text(
                    text = "Tap to view all tasks",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = ColorProvider(
                            day = colors.colorWhite,
                            night = colors.colorWhite
                        )
                    )
                )
            }
        }
    }

    /**
     * Individual task item display in the widget.
     * Shows task title with completion status indicator in white text.
     * Each task item is also clickable to open the main app.
     */
    @Composable
    private fun TaskItemWidget(task: Task, context: Context) {
        val colors = getWidgetColors(context)
        Row(
            modifier = GlanceModifier
                .clickable(
                    onClick = actionStartActivity(
                        componentName = createMainAppComponentName(context)
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion status indicator
            Text(
                text = if (task.isCompleted) "✅" else "⭕",
                style = TextStyle(fontSize = 12.sp)
            )

            // Task title with white text (truncate if too long for widget space)
            Text(
                text = task.title.take(25) + if (task.title.length > 25) "..." else "",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(
                        day = if (task.isCompleted) colors.colorWhiteSecondary else colors.colorWhite,
                        night = if (task.isCompleted) colors.colorWhiteSecondary else colors.colorWhite
                    )
                ),
                modifier = GlanceModifier.padding(start = 8.dp)
            )
        }
    }

    /**
     * Deserialize JSON string to List of Tasks.
     * Handles the same format used by TaskListScreen for data consistency.
     */
    private fun deserializeTasks(json: String?): List<Task> {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("TaskWidget", "Error deserializing tasks: ${e.message}")
            emptyList()
        }
    }
}