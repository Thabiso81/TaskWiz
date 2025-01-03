package com.thabiso81.taskwiz.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.ActivityMainBinding
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal val viewModel: ViewTasksViewModel by lazy {
        val taskDatabase = TaskDatabase.getInstance(this)
        val viewModelFactory = ViewTasksViewModelFactory(taskDatabase)
        ViewModelProvider(this, viewModelFactory)[ViewTasksViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*val bottomNavController = Navigation.findNavController(this, R.id.hostFragment)
        NavigationUI.setupWithNavController(binding.btmNav, bottomNavController)*/



    }

}