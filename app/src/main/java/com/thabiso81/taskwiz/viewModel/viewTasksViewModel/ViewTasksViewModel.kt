package com.thabiso81.taskwiz.viewModel.viewTasksViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.model.TaskModel
import kotlinx.coroutines.launch

class ViewTasksViewModel(val taskDatabase: TaskDatabase): ViewModel() {
    /**** states that hold the data that will be presented to user ***/
    private var allTasksLiveData = taskDatabase.taskDao().getAllTasks()
    private var incompleteTasksLiveData = taskDatabase.taskDao().getAllIncompleteTasks()


    /****** logic that gets the data from api or database *****/
    fun getTasks(){
        allTasksLiveData = taskDatabase.taskDao().getAllTasks()
    }

    fun getIncompleteTasks(){
        incompleteTasksLiveData = taskDatabase.taskDao().getAllIncompleteTasks()
    }



    /*** methods that will observe states for any changes in their data **/
    fun observeAllTasksLiveData(): LiveData<List<TaskModel>> {
        return allTasksLiveData
        //returns the live data as soon as it changes.

        /* notice difference between LiveData<> and MutableLiveData<> */
    }

    fun observeIncompleteTasksLiveData(): LiveData<List<TaskModel>> {
        return incompleteTasksLiveData

    }

    fun deleteTask(task: TaskModel){
        viewModelScope.launch{
            taskDatabase.taskDao().deleteTask(task)
        }
    }
}