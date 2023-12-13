package com.thabiso81.taskwiz.viewModel.viewTasksViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.model.TaskModel
import kotlinx.coroutines.launch

class ViewTasksViewModel(val taskDatabase: TaskDatabase): ViewModel() {
    /**** states that hold the data that will be presented to user ***/
    private var myTasksLiveData = taskDatabase.taskDao().getAllTasks()



    /****** logic that gets the data from api or database *****/
    fun getTasks(){

    }



    /*** methods that will observe states for any changes in their data **/
    fun observemyTasksLiveData(): LiveData<List<TaskModel>> {
        return myTasksLiveData
        //returns the live data as soon as it changes.

        /* notice difference between LiveData<> and MutableLiveData<> */
    }


    fun deleteTask(task: TaskModel){
        viewModelScope.launch{
            taskDatabase.taskDao().deleteTask(task)
        }
    }
}