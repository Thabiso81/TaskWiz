package com.thabiso81.taskwiz.lists

import com.thabiso81.taskwiz.model.TaskModel
import java.time.LocalDate

object TaskList {
    val taskList = mutableListOf<TaskModel>(
        TaskModel(taskId = 0, taskName =  "Walk the dog", taskDescription =  "the dog needs a walk", taskDueDate = LocalDate.now()),
        TaskModel(taskId = 1, taskName = "Feed the cat", taskDescription =  "the cat will starve if you dont feed it",taskDueDate =  LocalDate.now()),
        TaskModel(taskId = 2, taskName = "Do homework", taskDescription = "Maths homework, pages 89-94",taskDueDate =  LocalDate.now())
    )
}