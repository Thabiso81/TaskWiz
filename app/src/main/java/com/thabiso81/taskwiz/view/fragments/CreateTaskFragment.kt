package com.thabiso81.taskwiz.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.FragmentCreateTaskBinding
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.activities.LoginActivity
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModel
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate


class CreateTaskFragment : Fragment() {
    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

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

        swtAddDueDateOnClickListener()

        imgEnableDueDateOnClickListener()

        edtCompletionDateSetOnclickListener()

        btnCreateTaskSetOnClickListener()

        onBackButtonPressed()

        return view
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
                val newTask = TaskModel(taskDescription = binding.edtTaskDescription.text.toString(),
                    taskName = binding.edtTaskName.text.toString(), taskDueDate = taskCompletionDate,
                    completionStatus = defaultCompletionStatus, taskCreationDate = LocalDate.now())

                taskMvvm.insertTask(newTask)

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