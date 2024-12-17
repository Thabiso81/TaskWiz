package com.thabiso81.taskwiz.view.fragments.bottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.thabiso81.taskwiz.adapters.DisplayChecklistAdapter
import com.thabiso81.taskwiz.databinding.FragmentTaskEditBottomSheetBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.activities.MainActivity
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate



interface OnTaskSaved {
    fun onTaskSaved(isSaved: Boolean)
}


class TaskEditBottomSheetFragment : BottomSheetDialogFragment() {
    private var callbackListener: OnTaskSaved? = null

    private var _binding: FragmentTaskEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: TaskModel
    private var checklist: Array<TaskChecklistModel>? = null
    private var newChecklist: MutableList<TaskChecklistModel>? = null
    private val navigationArgs: TaskEditBottomSheetFragmentArgs by navArgs()

    private val defaultCompletionStatus = "Incomplete"
    private var taskCompletionDate: Long? = null

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

        imgEditOnClick()

        cancelEditOnClick()

        deleteDueDateOnClick()

        addChecklistOnclick()

        edtCompletionDate_OnClickListener()

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
        if (!checklist.isNullOrEmpty()){

            //onChecklistUnavailable()
            onChecklistAvailable()

            binding.imgAddMoreChecklistItems.visibility = View.GONE

        }
        /*else{

            onChecklistAvailable()

        }*/
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

    private fun imgEditOnClick() {

        binding.imgEdit.setOnClickListener {
            //setup ui
            editTaskMode()


        }

        binding.btnDone.setOnClickListener {
            saveTask()

            /*if (saveTask()){
                //task = new task that was saved
                //viewTaskMode()

                //populateUI()

                //dismiss()
            }*/
            //saveTask()


        }
    }

    private fun cancelEditOnClick() {
        binding.btnCancelEdit.setOnClickListener {
            viewTaskMode()

            populateUI()
        }
    }

    private fun deleteDueDateOnClick() {
        binding.imgDeleteDueDate.setOnClickListener {
            binding.edtCompletionDate.setText("")
            taskCompletionDate = null
        }
    }

    private fun addChecklistOnclick() {

    }

    private fun edtCompletionDate_OnClickListener() {
        binding.edtCompletionDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.show(childFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener {

                //convert unix epoch value from milliseconds to days
                taskCompletionDate = it / (24 * 60 * 60 * 1000)
                binding.edtCompletionDate.setText("Due on " +
                    "${LocalDate.ofEpochDay(taskCompletionDate!!).dayOfMonth} " +
                    "${LocalDate.ofEpochDay(taskCompletionDate!!).month} " +
                    "${if (LocalDate.now().year == LocalDate.ofEpochDay(taskCompletionDate!!).year) "" else LocalDate.ofEpochDay(taskCompletionDate!!).year}"
                )

                if (LocalDate.ofEpochDay(taskCompletionDate!!).isBefore(LocalDate.now())){
                    binding.edtCompletionDate.text=""
                    Toast.makeText(requireContext(), "Please select a future date", Toast.LENGTH_LONG).show()
                }

            }
        }
    }


    private fun saveTask() {
        var taskSaved = false

        if (inputValid(binding.edtTaskName, binding.edtTaskDescription)){
            //if no changes were after btndone is clicked, then do nothing or call viewTaskMode

            //save task
            val newTask = TaskModel(
                taskId = task.taskId,
                taskDescription = binding.edtTaskDescription.text.toString(),
                taskName = binding.edtTaskName.text.toString(),
                taskDueDate = taskCompletionDate ?: task.taskDueDate,
                completionStatus = defaultCompletionStatus,
                taskCreationDate = LocalDate.now().toEpochDay(),
            )
            CoroutineScope(Dispatchers.Main).launch {
                val taskId = viewModel.insertTask(newTask)

                if (taskId != null){
                    taskSaved = true
                }

                if (!newChecklist.isNullOrEmpty()) {
                    for (checklist in newChecklist!!) {

                        val newChecklist = TaskChecklistModel(
                            checklistItemTitle = checklist.checklistItemTitle,
                            taskId = taskId,
                            completionStatus = defaultCompletionStatus
                        )

                        viewModel.insertChecklist(newChecklist)



                    }
                }

                //if keyboard is showing then dismiss it first

                dismiss()

            }
        }


    }

    private fun editTaskMode(){
        binding.btnCancelEdit.visibility = View.VISIBLE
        binding.btnDone.visibility = View.VISIBLE
        binding.divider2.visibility = View.VISIBLE
        binding.imgAddMoreChecklistItems.visibility = View.VISIBLE
        binding.imgDeleteDueDate.visibility = View.VISIBLE


        binding.imgEdit.visibility = View.GONE
        binding.btnTaskComplete.visibility = View.GONE

        //Task Name
        binding.edtTaskName.isEnabled = true

        //description
        binding.edtTaskDescription.visibility = View.VISIBLE
        binding.edtTaskDescription.isEnabled = true

        //completion date
        binding.cdvCompletionDate.visibility = View.VISIBLE
        binding.edtCompletionDate.isEnabled = true

        //Completion
        binding.edtCompletionDate.isClickable = true

        //checklist logic
        if (checklist.isNullOrEmpty()){

            onChecklistUnavailable()

        }else{

            onChecklistAvailable()

        }
    }

    private fun viewTaskMode(){
        binding.btnCancelEdit.visibility = View.GONE
        binding.btnDone.visibility = View.GONE
        binding.imgAddMoreChecklistItems.visibility = View.GONE
        binding.imgDeleteDueDate.visibility = View.GONE
        //for the checklist
        binding.lytReviewChecklist.visibility = View.GONE

        binding.imgEdit.visibility = View.VISIBLE
        binding.btnTaskComplete.visibility = View.VISIBLE
        //for the checklist
        binding.lytDisplayChecklist.visibility = View.VISIBLE

        binding.edtTaskName.isEnabled = false

        binding.edtTaskDescription.isEnabled = false
        binding.edtTaskName.isEnabled = false

        binding.edtCompletionDate.isClickable = false
        binding.edtCompletionDate.isEnabled = false



    }
    private fun observerChecklist() {

        viewModel.observeTaskChecklistLiveData(task.taskId).observe(viewLifecycleOwner, Observer { checklist ->
            if (!checklist.isNullOrEmpty()){

                onChecklistAvailable()

                checklistAdapter.differ.submitList(checklist.toMutableList())

            }
            /*else{
                onChecklistUnavailable()
            }*/
        })

    }

    fun inputValid(taskName: EditText, taskDescription: EditText): Boolean{
        var isValid = true
        if (taskName.text.toString().isEmpty() && taskDescription.text.toString().isEmpty() ){
            isValid = false
            Toast.makeText(requireContext(), "Oops! forgot to fill in the task title or task description.", Toast.LENGTH_LONG).show()
        }
        return isValid
    }

    fun setOnTaskSavedListener(listener: OnTaskSaved) {
        callbackListener = listener
    }


}