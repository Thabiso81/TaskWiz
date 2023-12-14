package com.thabiso81.taskwiz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thabiso81.taskwiz.databinding.TaskViewHolderBinding
import com.thabiso81.taskwiz.model.TaskModel
import java.time.LocalDate

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

        if (!task.taskDescription.isNullOrEmpty()){
            if (task.taskDescription.length >= 100){
                //limit amount of characters shown
                holder.itemBinding.tvTaskDescription.text = "${task.taskDescription.substring(0, 101)}..."
            }else{
                holder.itemBinding.tvTaskDescription.text = task.taskDescription
            }
        }else{
            //dont show description view if there is no description
            holder.itemBinding.tvTaskDescription.visibility = View.GONE
        }

        if (task.taskDueDate != LocalDate.ofEpochDay(0)){
            holder.itemBinding.tvTaskDueDate.text = "Due on ${task.taskDueDate!!.dayOfMonth} ${task.taskDueDate!!.month}"
        }else{
            //dont show due date if there is none
            holder.itemBinding.lytDueDate.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}