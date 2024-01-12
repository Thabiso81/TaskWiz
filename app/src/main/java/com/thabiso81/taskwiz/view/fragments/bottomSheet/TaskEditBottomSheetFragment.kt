package com.thabiso81.taskwiz.view.fragments.bottomSheet

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thabiso81.taskwiz.database.relations.TaskWithChecklist
import com.thabiso81.taskwiz.databinding.FragmentTaskEditBottomSheetBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.activities.MainActivity
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel



class TaskEditBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTaskEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: TaskModel
    private var checklist: Array<TaskChecklistModel>? = null
    private val navigationArgs: TaskEditBottomSheetFragmentArgs by navArgs()

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



        populateUI()

        return view
    }

    private fun populateUI() {

        binding.edtTaskName.setText(task.taskName ?: " ")
        binding.edtTaskDescription.setText(task.taskDescription ?: " ")
    }

}