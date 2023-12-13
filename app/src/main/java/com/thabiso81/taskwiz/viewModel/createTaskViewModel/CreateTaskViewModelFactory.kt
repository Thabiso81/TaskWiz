package com.thabiso81.taskwiz.viewModel.createTaskViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thabiso81.taskwiz.database.TaskDatabase

class CreateTaskViewModelFactory(
    val taskDatabase: TaskDatabase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateTaskViewModel(taskDatabase) as T
    }
}