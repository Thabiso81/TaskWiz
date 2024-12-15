package com.thabiso81.taskwiz.database.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import kotlinx.parcelize.Parcelize

data class TaskWithChecklist(
    @Embedded val task: TaskModel,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val checklist: List<TaskChecklistModel>
)
