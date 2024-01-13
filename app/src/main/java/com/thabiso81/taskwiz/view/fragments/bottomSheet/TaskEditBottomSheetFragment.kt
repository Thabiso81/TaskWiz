package com.thabiso81.taskwiz.view.fragments.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thabiso81.taskwiz.adapters.DisplayChecklistAdapter
import com.thabiso81.taskwiz.databinding.FragmentTaskEditBottomSheetBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.activities.MainActivity
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import java.time.LocalDate


class TaskEditBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTaskEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: TaskModel
    private var checklist: Array<TaskChecklistModel>? = null
    private val navigationArgs: TaskEditBottomSheetFragmentArgs by navArgs()

    private lateinit var checklistAdapter: DisplayChecklistAdapter

    private lateinit var viewModel: ViewTasksViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        task = navigationArgs.task
        checklist = navigationArgs.checklist

        //using the same viewmodel as the one in ViewCurrentTasksFragment()
        viewModel = (activity as MainActivity).viewModel

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskEditBottomSheetBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.getChecklistById(task.taskId)

        observerChecklist()

        prepareRecyclerview()

        populateUI()

        return view
    }

    private fun populateUI() {
        //title and description logic
        if(task.taskDescription.isNullOrEmpty()){
            //descrpition not available
            binding.divider2.visibility = View.GONE
            binding.edtTaskDescription.visibility = View.GONE

            binding.edtTaskName.setText(task.taskName.toString())

        }else if(task.taskName.isNullOrEmpty()){
            //task title not available
            binding.divider2.visibility = View.GONE
            binding.edtTaskDescription.visibility = View.GONE

            binding.edtTaskName.setText(task.taskDescription.toString())

        }else{
            binding.edtTaskName.setText(task.taskName.toString())
            binding.edtTaskDescription.setText(task.taskDescription.toString())
        }

        //due date logic
        if(task.taskDueDate == null){
            //due date not available, hide due date field
            binding.cdvCompletionDate.visibility = View.GONE
        }else{
            binding.edtCompletionDate.text = "Due on ${LocalDate.ofEpochDay(task.taskDueDate!!).dayOfMonth} ${LocalDate.ofEpochDay(task.taskDueDate!!).month}"
        }

        //checklist logic
        if (checklist.isNullOrEmpty()){

            onChecklistUnavailable()

        }else{

            onChecklistAvailable()

        }
    }

    private fun onChecklistUnavailable() {
        binding.lytChecklistHeader.visibility = View.GONE
        binding.tvAddChecklist.visibility = View.VISIBLE
    }

    private fun onChecklistAvailable() {
        //checklist available, display it in recyclerview
        binding.lytChecklistHeader.visibility = View.VISIBLE
        binding.tvAddChecklist.visibility = View.GONE
    }

    private fun prepareRecyclerview() {
        checklistAdapter = DisplayChecklistAdapter()
        binding.rvChecklist.apply {
            setHasFixedSize(false)
            adapter = checklistAdapter

        }
    }

    private fun observerChecklist() {

        viewModel.observeTaskChecklistLiveData(task.taskId).observe(viewLifecycleOwner, Observer { checklist ->
            if (!checklist.isNullOrEmpty()){

                onChecklistAvailable()

                checklistAdapter.differ.submitList(checklist.toMutableList())

            }else{
                onChecklistUnavailable()
            }
        })

    }

}