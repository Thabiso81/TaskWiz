package com.thabiso81.taskwiz.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.thabiso81.taskwiz.R
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

        /*val bottomNavController = Navigation.findNavController(this, R.id.hostFragment)
        NavigationUI.setupWithNavController(binding.btmNav, bottomNavController)*/



    }

}