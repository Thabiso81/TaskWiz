package com.thabiso81.taskwiz.view.fragments.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thabiso81.taskwiz.database.relations.TaskWithChecklist
import com.thabiso81.taskwiz.databinding.FragmentTaskEditBottomSheetBinding
import com.thabiso81.taskwiz.view.activities.MainActivity
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel

private const val ARG_TASK_ID = "taskId"
private const val ARG_TASK_NAME = "taskName"
private const val ARG_TASK_DESCRIPTION = "taskDescription"
private const val ARG_TASK_COMPLETION_DATE = "taskCompletionDate"

class TaskEditBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTaskEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var taskId: String? = null
    private var taskName: String? = null
    private var taskDescription: String? = null
    private var taskCompletionDate: String? = null
    private lateinit var viewModel: ViewTasksViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getString(ARG_TASK_ID)
            taskName = it.getString(ARG_TASK_NAME)
            taskDescription = it.getString(ARG_TASK_DESCRIPTION)
            taskCompletionDate = it.getString(ARG_TASK_COMPLETION_DATE)
        }
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

    }

    companion object {

        @JvmStatic fun newInstance(
            _taskId: String,
            _taskName: String,
            _taskDescription: String,
            _taskCompletionDate: String,
            /*_taskCompletionStatus: String,
            _taskCreationDate: String,
            taskWithChecklist: TaskWithChecklist,*/

            ) =

                TaskEditBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TASK_ID, _taskId)
                        putString(ARG_TASK_NAME, _taskName)
                        putString(ARG_TASK_DESCRIPTION, _taskDescription)
                        putString(ARG_TASK_COMPLETION_DATE, _taskCompletionDate)

                    }
                }
    }
}