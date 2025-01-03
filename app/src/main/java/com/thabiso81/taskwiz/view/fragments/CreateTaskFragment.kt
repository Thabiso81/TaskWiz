package com.thabiso81.taskwiz.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.adapters.ReviewChecklistAdapter
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.FragmentCreateTaskBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.fragments.bottomSheet.OnTaskSaved
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModel
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class CreateTaskFragment : Fragment(), OnTaskSaved {
    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkListAdapter: ReviewChecklistAdapter
    private var checklistItems = mutableListOf<String>()

    private lateinit var taskMvvm: CreateTaskViewModel
    private val defaultCompletionStatus = "Incomplete"

    private var taskCompletionDate: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        val view = binding.root

        instantiate_Database_And_ViewModel()

        prepare_RecyclerView()

        edtCompletionDate_OnClickListener()

        add_ChecklistItem()

        tvAddChecklist_OnClickListener()

        imgAddMoreChecklistItems_OnClickListener()

        edtChecklist_Item_On_Fucus_Change_Listener()

        btnCreateTask_OnClickListener()

        on_Back_Button_Pressed()

        return view
    }

    private fun instantiate_Database_And_ViewModel() {
        val taskDatabase = TaskDatabase.getInstance(requireContext())
        val viewModelFactory = CreateTaskViewModelFactory(taskDatabase)
        taskMvvm = ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]
    }

    private fun prepare_RecyclerView() {
        checkListAdapter = ReviewChecklistAdapter()
        binding.rvChecklist.apply {
            setHasFixedSize(false)
            adapter = checkListAdapter
        }
    }

    private fun add_ChecklistItem() {
        binding.imgEnterChecklist.setOnClickListener {

            add_Checklist_Item()

            //if there are checklist items, adjust UI
            if (!checklistItems.isNullOrEmpty()){
                binding.tvAddChecklist.visibility = View.GONE
                binding.lytChecklistHeader.visibility = View.VISIBLE
            }else{
                binding.tvAddChecklist.visibility = View.VISIBLE
                binding.lytChecklistHeader.visibility = View.GONE
            }


            binding.edtChecklistItem.text.clear()
        }
    }


    /********************** Listeners start **********************/

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
                binding.edtCompletionDate.setText(
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


    private fun tvAddChecklist_OnClickListener() {
        binding.tvAddChecklist.setOnClickListener {
            binding.edtChecklistItem.show_Checklist_Keyboard()
        }

    }

    private fun imgAddMoreChecklistItems_OnClickListener() {
        binding.imgAddMoreChecklistItems.setOnClickListener {
            binding.edtChecklistItem.show_Checklist_Keyboard()
        }

    }

    private fun edtChecklist_Item_On_Fucus_Change_Listener() {
        binding.edtChecklistItem.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                binding.lytChecklist.visibility = View.GONE
            }
        }
    }

    private fun btnCreateTask_OnClickListener() {
        binding.btnCreateTask.setOnClickListener() {

            saveTask()
        }
    }
    /********************** Listeners end **********************/

    private fun saveTask(){

        if (inputValid(binding.edtTaskName, binding.edtTaskDescription)) {
            val newTask = TaskModel(
                taskDescription = binding.edtTaskDescription.text.toString(),
                taskName = binding.edtTaskName.text.toString(),
                taskDueDate = taskCompletionDate,
                completionStatus = defaultCompletionStatus,
                taskCreationDate = LocalDate.now().toEpochDay(),
            )


            CoroutineScope(Dispatchers.Main).launch{
                val taskId = taskMvvm.insertTask(newTask)

                if (!checklistItems.isNullOrEmpty()){
                    for(checklist in checklistItems){

                        val newChecklist = TaskChecklistModel(checklistItemTitle = checklist ,taskId=taskId, completionStatus = defaultCompletionStatus)

                        taskMvvm.insertChecklist(newChecklist)
                    }
                }

                findNavController().navigate(R.id.action_createTaskFragment_to_viewCurrentTasksFragment)
            }
        }

    }

    private fun add_Checklist_Item() {
        prepare_RecyclerView()

        if(!binding.edtChecklistItem.text.isNullOrEmpty()){
            checklistItems.add(binding.edtChecklistItem.text.toString())

            checkListAdapter.differ.submitList(checklistItems)

        }

    }

    fun View.show_Checklist_Keyboard() {
        binding.lytChecklist.visibility = View.VISIBLE

        this.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun on_Back_Button_Pressed(){
        //handle back button being pressed
        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle back press in fragments.
                if(binding.lytChecklist.isVisible){
                    binding.lytChecklist.visibility = View.GONE
                }else{
                    findNavController().navigate(R.id.action_createTaskFragment_to_viewCurrentTasksFragment)
                }
            }
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

    override fun onTaskSaved(isSaved: Boolean) {
        val taskSaved = isSaved
    }

}