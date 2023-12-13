package com.thabiso81.taskwiz.viewModel.viewTasksViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thabiso81.taskwiz.database.TaskDatabase

class ViewTasksViewModelFactory(
    private val taskDatabase: TaskDatabase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return ViewTasksViewModel(taskDatabase) as T
    }
}