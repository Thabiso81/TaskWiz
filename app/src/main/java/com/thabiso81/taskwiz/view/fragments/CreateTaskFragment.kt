package com.thabiso81.taskwiz.view.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
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


class CreateTaskFragment : Fragment(){
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

        prepare_RecyclerView(binding.rvChecklist)

        /*swtAddDueDateOnClickListener()

        imgEnableDueDateOnClickListener()*/


        edtCompletionDate_OnClickListener()

        tvAddChecklist_OnClickListener()

        imgAddMoreChecklistItems_OnClickListener()

       // edtChecklist_Item_On_Fucus_Change_Listener()

        btnCreateTask_OnClickListener()

        on_Back_Button_Pressed()

        return view
    }

    private fun instantiate_Database_And_ViewModel() {
        val taskDatabase = TaskDatabase.getInstance(requireContext())
        val viewModelFactory = CreateTaskViewModelFactory(taskDatabase)
        taskMvvm = ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]
    }

    private fun prepare_RecyclerView(view: RecyclerView) {
        checkListAdapter = TaskChecklistAdapter()
        view.apply {
            setHasFixedSize(false)
            adapter = checkListAdapter
        }

        /*if (!checklistItems.isNullOrEmpty()){
            checkListAdapter.differ.submitList(checklistItems)
        }*/
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

    /*private fun swtAddDueDateOnClickListener(){
        binding.swtAddDueDate.setOnCheckedChangeListener { _buttonView, _isChecked ->
            if (_isChecked){
                binding.edtCompletionDate.visibility = View.VISIBLE
            }else{
                binding.edtCompletionDate.visibility = View.GONE
            }
        }
    }*/

    /*private fun imgEnableDueDateOnClickListener() {
        binding.imgEnableDueDate.setOnClickListener {
            binding.swtAddDueDate.isChecked = true
        }
    }*/

    private fun tvAddChecklist_OnClickListener() {
        binding.tvAddChecklist.setOnClickListener {
           // binding.edtChecklistItem.showKeyboard()

            showChecklistDialog(binding.tvAddChecklist)
        }

       /* binding.imgEnterChecklist.setOnClickListener {

            add_Checklist_Item()
            binding.edtChecklistItem.hideKeyboard()
        }*/

        /*binding.edtChecklistItem.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus){
                view.hideKeyboard()
            }
        }*/
    }


    private fun imgAddMoreChecklistItems_OnClickListener() {
        binding.imgAddMoreChecklistItems.setOnClickListener {
            //binding.edtChecklistItem.showKeyboard()

            showChecklistDialog(binding.imgAddMoreChecklistItems)
        }

        /*binding.imgEnterChecklist.setOnClickListener {
            add_Checklist_Item()
            binding.tvAddChecklist.visibility = View.GONE
            binding.lytChecklistHeader.visibility = View.VISIBLE

            binding.edtChecklistItem.text.clear()
        }*/

    }

    private fun showChecklistDialog(v: View){

        val dialog = Dialog((v.context as FragmentActivity))
        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_checklist_bottom_sheet)

        val btnAddChecklistItem = dialog.findViewById<ImageView>(R.id.imgEnterChecklist)
        val edtChecklistItem = dialog.findViewById<EditText>(R.id.edtChecklistItem)
        val rvChecklists = dialog.findViewById<RecyclerView>(R.id.rvChecklist)


        btnAddChecklistItem.setOnClickListener {
            prepare_RecyclerView(rvChecklists)

            add_Checklist_Item(edtChecklistItem)

            edtChecklistItem.text.clear()
        }

        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle back press in fragments.
                dialog.dismiss()
            }
        })

    }

    /*private fun edtChecklist_Item_On_Fucus_Change_Listener() {
        binding.edtChecklistItem.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                binding.edtChecklistItem.hideKeyboard()
            }
        }
    }*/

    private fun add_Checklist_Item(view: EditText) {

        if(!view.text.isNullOrEmpty()){
            checklistItems.add(view.text.toString())

            checkListAdapter.differ.submitList(checklistItems)
        }

    }

    /*fun View.showKeyboard() {
        binding.lytChecklist.visibility = View.VISIBLE

        this.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }*/

/*
    fun View.hideKeyboard() {

        //if there are checklist items, adjust UI
        if (!checklistItems.isNullOrEmpty()){
            binding.tvAddChecklist.visibility = View.GONE
            binding.lytChecklistHeader.visibility = View.VISIBLE
        }else{
            binding.tvAddChecklist.visibility = View.VISIBLE
            binding.lytChecklistHeader.visibility = View.GONE
        }

        binding.lytChecklist.visibility = View.GONE

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)

        binding.edtChecklistItem.text.clear()
    }
*/

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

                /*if (binding.edtChecklistItem.isFocused){
                    binding.edtChecklistItem.hideKeyboard()
                }else{*/
                    // handle back press in fragments.
                    findNavController().navigate(R.id.action_createTaskFragment_to_viewCurrentTasksFragment)
                //}

            }
        })
    }

    private fun inputValid(taskName: EditText, taskDueDate: TextView): Boolean{
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