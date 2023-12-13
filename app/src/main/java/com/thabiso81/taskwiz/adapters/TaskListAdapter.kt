package com.thabiso81.taskwiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thabiso81.taskwiz.databinding.TaskViewHolderBinding
import com.thabiso81.taskwiz.model.TaskModel

class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.TaskListAdapterViewHolder>() {

    inner class TaskListAdapterViewHolder(val itemBinding: TaskViewHolderBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<TaskModel>(){
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapterViewHolder {
        return TaskListAdapterViewHolder(TaskViewHolderBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: TaskListAdapterViewHolder, position: Int) {
        val task = differ.currentList[position]
        holder.itemBinding.tvTaskName.text = task.taskName
        holder.itemBinding.tvTaskDescription.text = task.taskDescription
        holder.itemBinding.tvTaskDueDate.text = "${task.taskDueDate!!.dayOfMonth} ${task.taskDueDate!!.month.toString()}"

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}