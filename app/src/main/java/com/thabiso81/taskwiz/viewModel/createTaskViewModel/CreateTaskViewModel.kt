package com.thabiso81.taskwiz.viewModel.createTaskViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CreateTaskViewModel(
    val taskDatabase: TaskDatabase
): ViewModel() {
    fun insertTaskOnly(task: TaskModel){
        viewModelScope.launch {
            taskDatabase.taskDao().upsertTask(task)
        }
    }
    suspend fun insertTask(task: TaskModel): Long {
        return suspendCancellableCoroutine { continuation ->
            viewModelScope.launch {
                val taskId = taskDatabase.taskDao().upsertTask(task)
                continuation.resume(taskId)
            }
        }
    }

    fun insertChecklist(checklist: TaskChecklistModel){
        viewModelScope.launch {
            taskDatabase.taskDao().upsertChecklist(checklist)
        }
    }
}