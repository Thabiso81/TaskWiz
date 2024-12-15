package com.thabiso81.taskwiz.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity (tableName = "userCheckLists")
data class TaskChecklistModel(
    @PrimaryKey(autoGenerate = true)
    val checklistItemId: Long = 0,
    var checklistItemTitle: String,
    var completionStatus: String?,
    val taskId: Long
): Parcelable
