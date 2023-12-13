package com.thabiso81.taskwiz.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.thabiso81.taskwiz.adapters.TaskListAdapter
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.ActivityMainBinding
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksMvvm: ViewTasksViewModel
    private lateinit var taskListAdapter: TaskListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        instantiateDatabaseAndViewModel()

        //get the list of tasks
        tasksMvvm.getTasks()
        observerTasks()

        prepareReyclerView()

        addTaskSetOnClickListener()

        onBackButtonPressed()


    }

    private fun prepareReyclerView() {
        taskListAdapter = TaskListAdapter()
        binding.rvTasks.apply {
            adapter = taskListAdapter
        }
    }


    private fun observerTasks() {
        tasksMvvm.observemyTasksLiveData().observe(this@MainActivity, Observer { tasks ->

            taskListAdapter.differ.submitList(tasks)


        })
    }

    private fun onBackButtonPressed(){
        //handle back button being pressed
        var backButtonPressed = 0
        val dispatcher = this.onBackPressedDispatcher
        dispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle back press in fragments.
                backButtonPressed++
                if (backButtonPressed >= 2){
                    val intent  = Intent(this@MainActivity.applicationContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)


                }else if(backButtonPressed < 2){
                    Toast.makeText(this@MainActivity.applicationContext, "press back again to exit", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.IO).launch{
                        delay(3000)
                        backButtonPressed = 0
                    }
                }
            }
        })
    }

    private fun instantiateDatabaseAndViewModel(){
        val taskDatabase = TaskDatabase.getInstance(this)
        val viewModelFactory = ViewTasksViewModelFactory(taskDatabase)
        tasksMvvm = ViewModelProvider(this, viewModelFactory)[ViewTasksViewModel::class.java]
    }

    private fun addTaskSetOnClickListener(){
        binding.bvAddTask.setOnClickListener() {
            val nextPage = Intent(this@MainActivity, CreateTaskActivity::class.java)
            startActivity(nextPage)
        }
    }
}