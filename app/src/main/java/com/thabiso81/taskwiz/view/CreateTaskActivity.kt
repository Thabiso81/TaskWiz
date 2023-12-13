package com.thabiso81.taskwiz.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.ActivityCreateTaskBinding
import com.thabiso81.taskwiz.model.TaskModel
import androidx.lifecycle.ViewModelProvider
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModel
import com.thabiso81.taskwiz.viewModel.createTaskViewModel.CreateTaskViewModelFactory
import java.time.LocalDate

class CreateTaskActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var taskMvvm: CreateTaskViewModel

    private var taskCompletionDate: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        instantiateDatabaseAndViewModel()

        edtCompletionDateSetOnclickListener()

        btnCreateTaskSetOnClickListener()



    }

    private fun btnCreateTaskSetOnClickListener() {
        binding.btnCreateTask.setOnClickListener() {


            if (inputValid(binding.edtTaskName)) {
                val newTask = TaskModel(taskDescription = binding.edtTaskDescription.text.toString(),
                    taskName = binding.edtTaskName.text.toString(), taskDueDate = LocalDate.now())
                /****** adjust this ^ ******/


                //TaskList.taskList.add(newTask)
                taskMvvm.insertTask(newTask)

                val nextPage = Intent(this, MainActivity::class.java)
                startActivity(nextPage)
                finish()
            }

        }
    }

    private fun instantiateDatabaseAndViewModel() {
        val taskDatabase = TaskDatabase.getInstance(this)
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

            datePicker.show(supportFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener {
                // Respond to positive button click.

                //convert unix epoch value from milliseconds to days
                taskCompletionDate = it / (24 * 60 * 60 * 1000)
                binding.edtCompletionDate.setText(
                    "${LocalDate.ofEpochDay(taskCompletionDate).dayOfMonth} " +
                            "${LocalDate.ofEpochDay(taskCompletionDate).month} " +
                            "${LocalDate.ofEpochDay(taskCompletionDate).year}"
                )

            }
        }
    }

    fun inputValid(taskName: EditText): Boolean{
        var isValid = true
        if (taskName.text.toString().isEmpty()){
            isValid = false
            Toast.makeText(applicationContext, "Enter a task name", Toast.LENGTH_LONG).show()
        }

        return isValid
    }

}