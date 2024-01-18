package com.thabiso81.taskwiz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.database.relations.TaskWithChecklist
import com.thabiso81.taskwiz.databinding.TaskViewHolderBinding
import com.thabiso81.taskwiz.globalMethods.GlobalMethods
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import java.time.LocalDate

class DisplayTaskListAdapter(private val onCheckboxClickListener: OnCheckboxClickListener, private val onTaskClickListener: OnTaskClickListener) : RecyclerView.Adapter<DisplayTaskListAdapter.TaskListAdapterViewHolder>() {
    interface OnCheckboxClickListener {
        fun onCheckboxClick(task: TaskModel, view: CheckBox)
    }

    interface OnTaskClickListener {
        fun onTaskClick(task: TaskModel, checklist: List<TaskChecklistModel>)
    }
    inner class TaskListAdapterViewHolder(val itemBinding: TaskViewHolderBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<TaskWithChecklist>(){
        override fun areItemsTheSame(oldItem: TaskWithChecklist, newItem: TaskWithChecklist): Boolean {
            return oldItem.task.taskId == newItem.task.taskId
        }

        override fun areContentsTheSame(oldItem: TaskWithChecklist, newItem: TaskWithChecklist): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapterViewHolder {
        return TaskListAdapterViewHolder(TaskViewHolderBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: TaskListAdapterViewHolder, position: Int) {
        val task = differ.currentList[position].task
        val checklist = differ.currentList[position].checklist
        val taskWithChecklist = differ.currentList[position]

        //displays the taskName if available
        if (!task.taskName.isNullOrEmpty()){
            holder.itemBinding.tvTaskName.text = task.taskName
        }

        //displays the description
        if (!task.taskDescription.isNullOrEmpty()){
            //if (taskName == null) display description in the taskName view
            if (!task.taskName.isNullOrEmpty()){
                holder.itemBinding.tvTaskDescription.text = task.taskDescription
            }else{
                holder.itemBinding.tvTaskName.text = task.taskDescription
                holder.itemBinding.tvTaskDescription.visibility = View.GONE
            }
        }else{
            //dont show description view if there is no description
            holder.itemBinding.tvTaskDescription.visibility = View.GONE
        }

        //displays due date
        if (task.taskDueDate != null || (task.taskDueDate?:0).toInt() != 0){
            //holder.itemBinding.tvTaskDueDate.text = "Due on ${LocalDate.ofEpochDay(task.taskDueDate!!).dayOfMonth} ${LocalDate.ofEpochDay(task.taskDueDate!!).month}"
            holder.itemBinding.tvTaskDueDate.text = "${GlobalMethods().getDate(task.taskDueDate!!)}"

        }else{
            //dont show due date if there is none
            holder.itemBinding.lytDueDate.visibility = View.GONE
        }

        //displays checklist info
        if (!checklist.isNullOrEmpty()){
            val totalChecklistItems = checklist.size
            var completeChecklistItems = 0

            //count how many checklist items are complete
            for (item in checklist){
                if (item.completionStatus.equals("complete"))
                    completeChecklistItems++
            }

            holder.itemBinding.tvChecklistAmount.text = "Checklist : $completeChecklistItems/$totalChecklistItems"
        }else{
            //dont show checklist info if there are no checklists
            holder.itemBinding.lytChecklists.visibility = View.GONE
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
                    onCheckboxClickListener.onCheckboxClick(task, holder.itemBinding.cbxCompleted)
                }
            }
        }

        //handle the click listener of the the entire task
        holder.itemBinding.lytTaskDetails.setOnClickListener {
            onTaskClickListener.onTaskClick(task, checklist)
        }

        //adjust viewholder views if (description == null) && (due date == null) && (checklist == null)
        if(task.taskDescription.isNullOrEmpty() &&
            task.taskDueDate == null &&
            checklist.isNullOrEmpty()){
            holder.itemBinding.divider.visibility = View.GONE
        }

        //adjust viewholder views if (Title == null)
        if(task.taskName.isNullOrEmpty()){
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