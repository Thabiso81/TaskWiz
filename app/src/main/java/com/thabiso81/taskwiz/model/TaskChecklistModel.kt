package com.thabiso81.taskwiz.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "userCheckLists")
data class TaskChecklistModel(
    @PrimaryKey(autoGenerate = true)
    val checklistItemId: Long = 0,
    val checklistItemTitle: String,
    val taskId: Long
)
