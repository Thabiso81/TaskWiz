package com.thabiso81.taskwiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.thabiso81.taskwiz.databinding.ChecklistViewHolderBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel

class DisplayChecklistAdapter: RecyclerView.Adapter<DisplayChecklistAdapter.TaskChecklistAdapterViewHolder>() {
        //this recyclerview is used only to display checklists.
    inner class TaskChecklistAdapterViewHolder(val itemBinding: ChecklistViewHolderBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<TaskChecklistModel>(){
        override fun areItemsTheSame(oldItem: TaskChecklistModel, newItem: TaskChecklistModel): Boolean {
            return oldItem.checklistItemId == newItem.checklistItemId
        }

        override fun areContentsTheSame(oldItem: TaskChecklistModel, newItem: TaskChecklistModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayChecklistAdapter.TaskChecklistAdapterViewHolder {
        return TaskChecklistAdapterViewHolder(ChecklistViewHolderBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DisplayChecklistAdapter.TaskChecklistAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.itemBinding.tvChecklistItemName.text = item.checklistItemTitle
        holder.itemBinding.cbxCompleted.isChecked = false
    }
}