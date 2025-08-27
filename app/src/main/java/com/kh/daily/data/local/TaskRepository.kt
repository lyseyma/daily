package com.kh.daily.data.local

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kh.dilywidgtet.data.Task

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")



    // Create a new task
    fun createTask(task: Task, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(task.id).set(task)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Fetch all tasks

    fun fetchTasks(onComplete: (List<Task> , Boolean) -> Unit) {
        tasksCollection.get()
            .addOnSuccessListener { result ->
                val tasks = result.map { it.toObject(Task::class.java) }
                onComplete(tasks , true)
                Log.d("TAG", "Get list from db complete  $tasks ")
            }
            .addOnFailureListener {
                onComplete(emptyList() , false)
                Log.d("TAG", "Get list from db fail")
            }
    }

    // Update a task
    fun updateTask(task: Task, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(task.id).set(task)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Delete a task
    fun deleteTask(taskId: String, onComplete: (Boolean) -> Unit) {
        tasksCollection.document(taskId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}