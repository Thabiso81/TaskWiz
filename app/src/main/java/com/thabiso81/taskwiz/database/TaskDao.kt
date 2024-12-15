package com.thabiso81.taskwiz.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.thabiso81.taskwiz.database.relations.TaskWithChecklist
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskModel): Long //updates and inserts. updates if row already exists

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChecklist(checkList: TaskChecklistModel)

    @Delete
    suspend fun deleteTask(task:TaskModel) //notice the suspend functions

    @Query("SELECT * FROM userTasks")
    fun getAllTasks(): LiveData<List<TaskModel>>

    @Transaction
    @Query("SELECT * FROM userTasks where taskId = :taskId")
    fun getTaskWithTaskCheckListById(taskId: String): LiveData<List<TaskWithChecklist>>

    @Transaction
    @Query("SELECT * FROM userCheckLists where taskId = :taskId")
    fun getCheckListById(taskId: Long): LiveData<List<TaskChecklistModel>>


    @Query("SELECT * FROM userTasks where completionStatus = 'Incomplete' ORDER BY taskDueDate DESC")
    fun getAllIncompleteTasksWithChecklists(): LiveData<List<TaskWithChecklist>>

}