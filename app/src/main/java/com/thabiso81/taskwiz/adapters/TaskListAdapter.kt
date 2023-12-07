package com.thabiso81.taskwiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thabiso81.taskwiz.databinding.RecyclerViewRowBinding
import com.thabiso81.taskwiz.model.TaskModel

class TaskListAdapter(val taskList: List<TaskModel>):
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    inner class ViewHolder(val itemBinding: RecyclerViewRowBinding): RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(taskModel: TaskModel){
            itemBinding.tvTaskName.text = taskModel.taskName
            itemBinding.tvTaskDescription.text = taskModel.taskDescription
            itemBinding.tvTaskDueDate.text = "Due on ${taskModel.taskDueDate.dayOfMonth} ${(taskModel.taskDueDate.month.toString()).replaceFirstChar { 
                it.uppercase()
            }}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerViewRowBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.bindItem(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}