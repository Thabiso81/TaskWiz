package com.thabiso81.taskwiz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox.OnCheckedStateChangedListener
import com.google.android.material.snackbar.Snackbar
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.TaskViewHolderBinding
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModelFactory
import java.time.LocalDate

class TaskListAdapter(private val onCheckboxClickListener: OnCheckboxClickListener) : RecyclerView.Adapter<TaskListAdapter.TaskListAdapterViewHolder>() {
    interface OnCheckboxClickListener {
        fun onCheckboxClick(task: TaskModel)
    }
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

        //adjust the view that displays the description
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

        //adjust the view that displays the date
        if (task.taskDueDate != LocalDate.ofEpochDay(0)){
            holder.itemBinding.tvTaskDueDate.text = "Due on ${task.taskDueDate!!.dayOfMonth} ${task.taskDueDate!!.month}"
        }else{
            //dont show due date if there is none
            holder.itemBinding.lytDueDate.visibility = View.GONE
        }

        //adjust margin on the last viewholder item
        if (position == differ.currentList.size - 1){
            val param = holder.itemBinding.cardViewItem.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,10,0,220)
            holder.itemBinding.cardViewItem.layoutParams = param
        }


        //remove divider if there is no description

        //handle the click listener of the checkbox
        holder.itemBinding.cbxCompleted.isChecked = false
        holder.itemBinding.cbxCompleted.setOnCheckedChangeListener{ _, isChecked ->
            if (position != RecyclerView.NO_POSITION){

                if (isChecked){
                    onCheckboxClickListener.onCheckboxClick(task)
                }
            }
        }

        //adjust viewholder if (description == null) && (due date == null)
        if(task.taskDescription.isNullOrEmpty() && task.taskDueDate == LocalDate.ofEpochDay(0)){
            holder.itemBinding.divider.visibility = View.GONE
        }

        //animate recyclerview items
        holder.itemBinding.cardViewItem.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemBinding.cardViewItem.context, R.anim.slide_in
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}