package com.thabiso81.taskwiz.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thabiso81.taskwiz.model.TaskModel

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskModel) //updates and inserts. updates if row already exists

    @Delete
    suspend fun deleteTask(task:TaskModel) //notice the suspend functions

    @Query("SELECT * FROM userTasks")
    fun getAllTasks(): LiveData<List<TaskModel>>


}