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
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.adapters.TaskChecklistAdapter
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.FragmentCreateTaskBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel
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

        instantiateDatabaseAndViewModel()

        prepareRecyclerView()

        swtAddDueDateOnClickListener()

        imgEnableDueDateOnClickListener()

        edtCompletionDateSetOnclickListener()

        tvAddChecklistsetOnclickListener()

        btnCreateTaskSetOnClickListener()

        onBackButtonPressed()

        return view
    }

    private fun tvAddChecklistsetOnclickListener() {
        binding.tvAddChecklist.setOnClickListener {
            binding.edtChecklistItem.showKeyboard()
        }

        binding.imgEnterChecklist.setOnClickListener {

            addChecklistItem()
            binding.edtChecklistItem.hideKeyboard()
        }

        /*binding.edtChecklistItem.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus){
                view.hideKeyboard()
            }
        }*/
    }

    private fun addChecklistItem() {

        if(!binding.edtChecklistItem.text.isNullOrEmpty()){
            checklistItems.add(binding.edtChecklistItem.text.toString())

            checkListAdapter.differ.submitList(checklistItems)
        }

    }

    fun View.showKeyboard() {
        binding.lytChecklist.visibility = View.VISIBLE
        binding.btnCreateTask.visibility = View.GONE

        this.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        binding.lytChecklist.visibility = View.GONE
        binding.btnCreateTask.visibility = View.VISIBLE

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)

        binding.edtChecklistItem.text.clear()
    }

    private fun prepareRecyclerView() {
        checkListAdapter = TaskChecklistAdapter()
        binding.rvChecklist.apply {
            adapter = checkListAdapter

        }
    }


    private fun swtAddDueDateOnClickListener(){
        binding.swtAddDueDate.setOnCheckedChangeListener { _buttonView, _isChecked ->
            if (_isChecked){
                binding.edtCompletionDate.visibility = View.VISIBLE
            }else{
                binding.edtCompletionDate.visibility = View.GONE
            }
        }
    }

    private fun imgEnableDueDateOnClickListener() {
        binding.imgEnableDueDate.setOnClickListener {
            binding.swtAddDueDate.isChecked = true
        }
    }

    private fun btnCreateTaskSetOnClickListener() {
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

    private fun instantiateDatabaseAndViewModel() {
        val taskDatabase = TaskDatabase.getInstance(requireContext())
        val viewModelFactory = CreateTaskViewModelFactory(taskDatabase)
        taskMvvm = ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]
    }

    private fun edtCompletionDateSetOnclickListener() {
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


    private fun onBackButtonPressed(){
        //handle back button being pressed
        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle back press in fragments.
               findNavController().navigate(R.id.action_createTaskFragment_to_viewCurrentTasksFragment)
            }
        })
    }

    fun inputValid(taskName: EditText, taskDueDate: TextView): Boolean{
        var isValid = true
        if (taskName.text.toString().isEmpty()){
            isValid = false
            Toast.makeText(requireContext(), "Enter a task name", Toast.LENGTH_LONG).show()
        }else if (binding.swtAddDueDate.isChecked){
            if (taskDueDate.text.isEmpty()){
                isValid = false
                Toast.makeText(requireContext(), "Dont forget your due date!!", Toast.LENGTH_LONG).show()
            }else if (taskCompletionDate!!.isBefore(LocalDate.now())){
                isValid = false
                Toast.makeText(requireContext(), "Please select a future date", Toast.LENGTH_LONG).show()
            }
        }
        return isValid
    }

}