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
import com.thabiso81.taskwiz.adapters.TaskChecklistAdapter
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.FragmentCreateTaskBinding
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModel
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class CreateTaskFragment : Fragment() {
    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkListAdapter: TaskChecklistAdapter
    private var checklistItems = mutableListOf<String>()

    private lateinit var taskMvvm: CreateTaskViewModel
    private val defaultCompletionStatus = "Incomplete"

    private var taskCompletionDate: LocalDate? = null
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
        checkListAdapter = TaskChecklistAdapter()
        binding.rvChecklist.apply {
            setHasFixedSize(false)
            adapter = checkListAdapter
        }
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
                taskCompletionDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                binding.edtCompletionDate.setText(
                    "${taskCompletionDate!!.dayOfMonth} " +
                            "${taskCompletionDate!!.month} " +
                            "${if (LocalDate.now().year == taskCompletionDate!!.year) "" else taskCompletionDate!!.year}"
                )

                if (taskCompletionDate!!.isBefore(LocalDate.now())){
                    binding.edtCompletionDate.text=""
                    Toast.makeText(requireContext(), "Please select a future date", Toast.LENGTH_LONG).show()
                }

            }
        }
    }


    private fun tvAddChecklist_OnClickListener() {
        binding.tvAddChecklist.setOnClickListener {
            binding.edtChecklistItem.showKeyboard()
        }

        binding.imgEnterChecklist.setOnClickListener {

            add_Checklist_Item()
            binding.edtChecklistItem.hideKeyboard()
        }

    }

    private fun imgAddMoreChecklistItems_OnClickListener() {
        binding.imgAddMoreChecklistItems.setOnClickListener {
            binding.edtChecklistItem.showKeyboard()
        }

        binding.imgEnterChecklist.setOnClickListener {
            add_Checklist_Item()
            binding.edtChecklistItem.hideKeyboard()
        }

    }

    private fun edtChecklist_Item_On_Fucus_Change_Listener() {
        binding.edtChecklistItem.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                binding.lytChecklist.visibility = View.GONE
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

    fun View.showKeyboard() {
        binding.lytChecklist.visibility = View.VISIBLE

        this.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {

        //if there are checklist items, adjust UI
        if (!checklistItems.isNullOrEmpty()){
            binding.tvAddChecklist.visibility = View.GONE
            binding.lytChecklistHeader.visibility = View.VISIBLE
        }else{
            binding.tvAddChecklist.visibility = View.VISIBLE
            binding.lytChecklistHeader.visibility = View.GONE
        }

        //binding.lytChecklist.visibility = View.GONE
        //binding.btnCreateTask.visibility = View.VISIBLE

        //val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //inputMethodManager.hideSoftInputFromWindow(windowToken, 0)

        binding.edtChecklistItem.text.clear()
    }

    private fun btnCreateTask_OnClickListener() {
        binding.btnCreateTask.setOnClickListener() {


            if (inputValid(binding.edtTaskName, binding.edtCompletionDate)) {
                val newTask = TaskModel(
                    taskDescription = binding.edtTaskDescription.text.toString(),
                    taskName = binding.edtTaskName.text.toString(),
                    taskDueDate = taskCompletionDate,
                    completionStatus = defaultCompletionStatus,
                    taskCreationDate = LocalDate.now(),
                )


                CoroutineScope(Dispatchers.IO).launch{
                    taskMvvm.insertTask(newTask)
                }


                findNavController().navigate(R.id.action_createTaskFragment_to_viewCurrentTasksFragment)
            }

        }
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

    fun inputValid(taskName: EditText, taskDueDate: TextView): Boolean{
        var isValid = true
        if (taskName.text.toString().isEmpty()){
            isValid = false
            Toast.makeText(requireContext(), "Enter a task title", Toast.LENGTH_LONG).show()
        }
        /*else if (binding.swtAddDueDate.isChecked){
            if (taskDueDate.text.isEmpty()){
                isValid = false
                Toast.makeText(requireContext(), "Dont forget your due date!!", Toast.LENGTH_LONG).show()
            }else if (taskCompletionDate!!.isBefore(LocalDate.now())){
                isValid = false
                Toast.makeText(requireContext(), "Please select a future date", Toast.LENGTH_LONG).show()
            }
        }*/
        return isValid
    }

}