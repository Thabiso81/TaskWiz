package com.thabiso81.taskwiz.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.adapters.TaskListAdapter
import com.thabiso81.taskwiz.databinding.ActivityMainBinding
import com.thabiso81.taskwiz.lists.TaskList
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.viewModel.MyTasksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksMvvm: MyTasksViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tasksMvvm = ViewModelProvider(this).get(MyTasksViewModel::class.java)

        //get the list of tasks
        tasksMvvm.getTasks()
        observerTasks()

        //bind recyclerView
        val adapter = TaskListAdapter(TaskList.taskList)
        binding.rvTasks.adapter = adapter

        //explicit intent that takes the user to a layout that lets them add a task
        binding.bvAddTask.setOnClickListener() {
            val nextPage = Intent(this@MainActivity, CreateTaskActivity::class.java)
            startActivity(nextPage)
        }


        //implicit intent that lets the user share their tasks (collected from the recyclerView)
        /**       val shareTasks = findViewById<Button>(R.id.bvShareTasks)
        shareTasks.setOnClickListener() {

        val message: String = "These are my tasks!"
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"

        startActivity(Intent.createChooser(intent,"Share to:"))
        } **/

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

    private fun observerTasks() {
        tasksMvvm.observemyTasksLiveData().observe(this@MainActivity, object: Observer<TaskModel> {

            override fun onChanged(value: TaskModel) {
                //implement changes that need to be triggered when observer is triggered
            }
        })
    }
    /*override fun onDestroy(){
        super.onDestroy()
        binding = null
    }*/

}