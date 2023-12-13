package com.thabiso81.taskwiz.viewModel.createTaskViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.model.TaskModel
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    val taskDatabase: TaskDatabase
): ViewModel() {
    fun insertTask(task: TaskModel){
        viewModelScope.launch {
            taskDatabase.taskDao().upsertTask(task)
        }
    }
}