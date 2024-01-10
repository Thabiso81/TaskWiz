package com.thabiso81.taskwiz.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(tableName = "userTasks")
data class TaskModel(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val taskName : String?,
    var completionStatus: String?,
    val taskDescription: String?,
    val taskDueDate: Long?,
    val taskCreationDate: Long?,
): Parcelable
// This will be our Data model. Each variable represents a piece of the data that will be on
//each recyclerView item.

//var taskCompletionDate: String
