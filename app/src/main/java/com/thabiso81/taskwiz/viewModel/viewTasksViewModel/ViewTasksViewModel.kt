package com.thabiso81.taskwiz.viewModel.viewTasksViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.database.relations.TaskWithChecklist
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ViewTasksViewModel(val taskDatabase: TaskDatabase): ViewModel() {
    /**** states that hold the data that will be presented to user ***/
    private var taskId: Long? = 0

    private var allTasksLiveData = taskDatabase.taskDao().getAllTasks()
    private var incompleteTasksLiveData = taskDatabase.taskDao().getAllIncompleteTasksWithChecklists()
    private var checklistLiveData = taskDatabase.taskDao().getCheckListById(taskId!!)


    /****** logic that gets the data from api or database *****/
    fun getTasks(){
        allTasksLiveData = taskDatabase.taskDao().getAllTasks()
    }

    fun getTaskById(id: String){
        allTasksLiveData = taskDatabase.taskDao().getAllTasks()
    }

    fun getIncompleteTasks(){
        incompleteTasksLiveData = taskDatabase.taskDao().getAllIncompleteTasksWithChecklists()
    }

   /* fun insertTask(task: TaskModel): Long{
        var taskId: Long = 0
        viewModelScope.launch {
            taskId = taskDatabase.taskDao().upsertTask(task)
        }

        return taskId
    }*/
    suspend fun insertTask(task: TaskModel): Long {
        return suspendCancellableCoroutine { continuation ->
            viewModelScope.launch {
                val taskId = taskDatabase.taskDao().upsertTask(task)
                continuation.resume(taskId)
            }
        }
    }

    fun getChecklistById(_taskId: Long){
        checklistLiveData = taskDatabase.taskDao().getCheckListById(_taskId)
    }

    fun insertChecklist(checklist: TaskChecklistModel){
        viewModelScope.launch {
            taskDatabase.taskDao().upsertChecklist(checklist)
        }
    }

    fun deleteTask(task: TaskModel){
        viewModelScope.launch{
            taskDatabase.taskDao().deleteTask(task)
        }
    }

    /*** methods that will observe states for any changes in their data **/

    fun observeIncompleteTasksLiveData(): LiveData<List<TaskWithChecklist>> {
        return incompleteTasksLiveData

    }

    fun observeTaskChecklistLiveData(_taskId: Long): LiveData<List<TaskChecklistModel>> {
        taskId = _taskId
        return checklistLiveData

    }

}