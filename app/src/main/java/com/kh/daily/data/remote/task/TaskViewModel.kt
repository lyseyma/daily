package com.kh.daily.data.remote.task

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kh.daily.data.local.TaskRepository
import com.kh.daily.widget.data.Task
import com.kh.daily.widget.receiver.TaskWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.gson.Gson
import androidx.core.content.edit

/**
 * TaskViewModel manages task data for the main application.
 * This ViewModel communicates with TaskRepository to perform CRUD operations
 * and maintains the current state of tasks for the UI.
 *
 * The tasks are also saved to SharedPreferences to share with the widget.
 * Widget updates are automatically triggered when tasks change.
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    /** Flag to track if tasks have been fetched to avoid unnecessary network calls */
    private var tasksFetched = false

    /** StateFlow containing the current list of tasks */
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    /** StateFlow indicating whether a loading operation is in progress */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Saves tasks to SharedPreferences for local persistence.
     * This ensures tasks are always available even when offline.
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
            Log.d("TaskViewModel", "Saved ${tasks.size} tasks to SharedPreferences")
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error saving tasks to SharedPreferences: ${e.message}")
        }
    }

    /**
     * Loads tasks from SharedPreferences as fallback when Firebase fails.
     */
    private fun loadTasksFromPrefs(context: Context): List<Task> {
        return try {
            val prefs = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
            val taskListJson = prefs.getString("task_list", "[]") ?: "[]"
            val gson = Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<Task>>() {}.type
            val tasks: List<Task> = gson.fromJson(taskListJson, type) ?: emptyList()
            Log.d("TaskViewModel", "Loaded ${tasks.size} tasks from SharedPreferences as fallback")
            tasks
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error loading tasks from SharedPreferences: ${e.message}")
            emptyList()
        }
    }

    /**
     * Updates widgets with the latest task data.
     * This is called after any task operation to keep widgets synchronized.
     */
    private fun updateWidgets(context: Context) {
        try {
            TaskWidgetReceiver.updateAllWidgets(context)
            Log.d("TaskViewModel", "Triggered widget update")
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error updating widgets: ${e.message}")
        }
    }

    /**
     * Creates a new task through the repository.
     * Also saves locally and refreshes the task list upon successful creation.
     */
    fun createTask(task: Task, context: Context? = null) {
        // Add to current list immediately for better UX
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.add(task)
        _tasks.value = currentTasks

        // Save to SharedPreferences immediately
        context?.let {
            saveTasksToPrefs(it, currentTasks)
            updateWidgets(it)
        }

        // Then sync with Firebase
        repository.createTask(task) { success ->
            if (success) {
                Log.d("TaskViewModel", "Task successfully synced to Firebase")
                // Optionally refresh from Firebase to get server state
                // fetchTasks()
            } else {
                Log.w("TaskViewModel", "Failed to sync task to Firebase, but saved locally")
            }
        }
    }

    /**
     * Fetches all tasks from the repository with fallback to local storage.
     * Updates the loading state and task list.
     */
    fun fetchTasks(context: Context? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            // Load from SharedPreferences first if available
            context?.let { ctx ->
                val savedTasks = loadTasksFromPrefs(ctx)
                if (savedTasks.isNotEmpty()) {
                    _tasks.value = savedTasks
                    Log.d("TaskViewModel", "Using saved tasks while fetching from Firebase")
                }
            }

            delay(2000) // Simulate network delay

            repository.fetchTasks { taskList, success ->
                Log.d("TaskViewModel", "fetchTasks from Firebase: $taskList, success: $success")

                if (success && taskList.isNotEmpty()) {
                    _tasks.value = taskList
                    // Save the fresh data from Firebase
                    context?.let { saveTasksToPrefs(it, taskList) }
                } else if (!success) {
                    // Firebase failed, keep using local data if we have it
                    val currentTasks = _tasks.value
                    if (currentTasks.isEmpty()) {
                        // Try loading from SharedPreferences as last resort
                        context?.let { ctx ->
                            val fallbackTasks = loadTasksFromPrefs(ctx)
                            if (fallbackTasks.isNotEmpty()) {
                                _tasks.value = fallbackTasks
                                Log.d(
                                    "TaskViewModel",
                                    "Using fallback tasks from SharedPreferences"
                                )
                            }
                        }
                    }
                    Log.w("TaskViewModel", "Firebase fetch failed, using local data")
                }

                _isLoading.value = false
                Log.d("TaskViewModel", "Loading completed: $success")
            }
        }
    }

    /**
     * Fetches tasks only if they haven't been fetched before.
     * Used to avoid unnecessary API calls on initial load.
     */
    fun fetchTasksIfNeeded(context: Context? = null) {
        if (!tasksFetched) {
            fetchTasks(context)
            tasksFetched = true
        }
    }

    /**
     * Updates an existing task through the repository.
     * Also saves locally and refreshes the task list upon successful update.
     */
    fun updateTask(task: Task, context: Context? = null) {
        // Update in current list immediately for better UX
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentTasks[index] = task
            _tasks.value = currentTasks

            // Save to SharedPreferences immediately
            context?.let {
                saveTasksToPrefs(it, currentTasks)
                updateWidgets(it)
            }
        }

        // Then sync with Firebase
        repository.updateTask(task) { success ->
            if (success) {
                Log.d("TaskViewModel", "Task update successfully synced to Firebase")
            } else {
                Log.w("TaskViewModel", "Failed to sync task update to Firebase, but saved locally")
            }
        }
    }

    /**
     * Deletes a task by ID through the repository.
     * Also removes locally and refreshes the task list upon successful deletion.
     */
    fun deleteTask(taskId: String, context: Context? = null) {
        // Remove from current list immediately for better UX
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.removeAll { it.id == taskId }
        _tasks.value = currentTasks

        // Save to SharedPreferences immediately
        context?.let {
            saveTasksToPrefs(it, currentTasks)
            updateWidgets(it)
        }

        // Then sync with Firebase
        repository.deleteTask(taskId) { success ->
            if (success) {
                Log.d("TaskViewModel", "Task deletion successfully synced to Firebase")
            } else {
                Log.w(
                    "TaskViewModel",
                    "Failed to sync task deletion to Firebase, but removed locally"
                )
            }
        }
    }
}
