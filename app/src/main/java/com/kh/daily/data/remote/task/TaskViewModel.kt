package com.kh.daily.data.remote.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kh.daily.data.local.TaskRepository
import com.kh.dilywidgtet.data.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private var tasksFetched = false
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _isLoading =  MutableStateFlow(false)
    var isLoading: StateFlow<Boolean> = _isLoading

    fun createTask(task: Task) {
        repository.createTask(task) { success ->
            if (success) fetchTasks()
        }
    }

    fun fetchTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000)
            repository.fetchTasks { taskList , success  ->

                Log.d("TAG", "fetchTasks:$taskList ")
                _tasks.value = taskList
                _isLoading.value = false
                Log.d("TAG", "Loading:$success ")
            }
        }

    }
    fun fetchTasksIfNeeded() {
        if (!tasksFetched) {
            fetchTasks()
            tasksFetched = true
        }
    }

    fun updateTask(task: Task) {
        repository.updateTask(task) { success ->
            if (success) fetchTasks()
        }
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId) { success ->
            if (success) fetchTasks()
        }
    }
}

