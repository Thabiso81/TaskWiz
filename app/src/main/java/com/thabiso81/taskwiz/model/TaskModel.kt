package com.thabiso81.taskwiz.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "userTasks")
data class TaskModel(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    val taskName : String?,
    var completionStatus: String?,
    val taskDescription: String?,
    val taskDueDate: LocalDate?,
    val taskCreationDate: LocalDate?
)
// This will be our Data model. Each variable represents a piece of the data that will be on
//each recyclerView item.

//var taskCompletionDate: String
