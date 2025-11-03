package com.kh.daily.data.repositiory

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.FirebaseNetworkException
import com.kh.daily.widget.data.Task

/**
 * TaskRepository handles all task-related data operations.
 * This repository communicates with Firebase Firestore to perform CRUD operations
 * and serves as the single source of truth for task data.
 *
 * Includes offline support and network error handling.
 */
class TaskRepository {
    /** Firebase Firestore instance for database operations */
    private val db = FirebaseFirestore.getInstance()

    /** Reference to the tasks collection in Firestore */
    private val tasksCollection = db.collection("tasks")

    init {
        // Enable offline persistence for Firestore
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
            Log.d("TaskRepository", "Firestore offline persistence enabled")
        } catch (e: Exception) {
            Log.w("TaskRepository", "Could not enable offline persistence: ${e.message}")
        }
    }

    /**
     * Creates a new task in Firestore.
     * @param task The task to create
     * @param onComplete Callback with success/failure result
     */
    fun createTask(task: Task, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(task.id).set(task)
            .addOnSuccessListener {
                Log.d("TaskRepository", "Task created successfully: ${task.id}")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                handleFirestoreException("create task", exception)
                onComplete(false)
            }
    }

    /**
     * Fetches all tasks from Firestore.
     * @param onComplete Callback with the list of tasks and success/failure result
     */
    fun fetchTasks(onComplete: (List<Task>, Boolean) -> Unit) {
        tasksCollection.get()
            .addOnSuccessListener { result ->
                val tasks = result.map { document ->
                    // Create Task object from Firestore document
                    Task(
                        id = document.getString("id") ?: "",
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        category = document.getString("category") ?: "",
                        dueDate = document.getString("dueDate") ?: "",
                        isCompleted = document.getBoolean("isCompleted") ?: false
                    )
                }

                val isFromCache = result.metadata.isFromCache
                Log.d(
                    "TaskRepository",
                    "Fetched ${tasks.size} tasks from ${if (isFromCache) "cache" else "server"}"
                )

                onComplete(tasks, true)
            }
            .addOnFailureListener { exception ->
                handleFirestoreException("fetch tasks", exception)
                // Return empty list but still call onComplete to prevent hanging UI
                onComplete(emptyList(), false)
            }
    }

    /**
     * Updates an existing task in Firestore.
     * @param task The task to update
     * @param onComplete Callback with success/failure result
     */
    fun updateTask(task: Task, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(task.id).set(task)
            .addOnSuccessListener {
                Log.d("TaskRepository", "Task updated successfully: ${task.id}")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                handleFirestoreException("update task", exception)
                onComplete(false)
            }
    }

    /**
     * Deletes a task from Firestore by ID.
     * @param taskId The ID of the task to delete
     * @param onComplete Callback with success/failure result
     */
    fun deleteTask(taskId: String, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(taskId).delete()
            .addOnSuccessListener {
                Log.d("TaskRepository", "Task deleted successfully: $taskId")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                handleFirestoreException("delete task", exception)
                onComplete(false)
            }
    }

    /**
     * Handles Firestore exceptions with specific error messages.
     * Provides better debugging information for network-related issues.
     */
    private fun handleFirestoreException(operation: String, exception: Exception) {
        when (exception) {
            is FirebaseNetworkException -> {
                Log.e("TaskRepository", "Network error during $operation: No internet connection")
                Log.e("TaskRepository", "Check your internet connection and Firebase configuration")
            }

            else -> {
                Log.e("TaskRepository", "Failed to $operation: ${exception.message}")
                Log.e("TaskRepository", "Exception type: ${exception.javaClass.simpleName}")
            }
        }
    }
}