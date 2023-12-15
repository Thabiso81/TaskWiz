package com.thabiso81.taskwiz.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.adapters.TaskListAdapter
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.FragmentViewCurrentTasksBinding
import com.thabiso81.taskwiz.view.activities.LoginActivity
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ViewCurrentTasksFragment : Fragment() {
    private var _binding: FragmentViewCurrentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var tasksMvvm: ViewTasksViewModel
    private lateinit var taskListAdapter: TaskListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewCurrentTasksBinding.inflate(inflater, container, false)
        val view = binding.root

        instantiateDatabaseAndViewModel()

        //get the list of tasks
        tasksMvvm.getIncompleteTasks()
        observerTasks()

        prepareReyclerView()

        addTaskSetOnClickListener()

        onBackButtonPressed()



        return view
    }

    private fun instantiateDatabaseAndViewModel(){
        val taskDatabase = TaskDatabase.getInstance(requireContext())
        val viewModelFactory = ViewTasksViewModelFactory(taskDatabase)
        tasksMvvm = ViewModelProvider(this, viewModelFactory)[ViewTasksViewModel::class.java]
    }

    private fun prepareReyclerView() {
        taskListAdapter = TaskListAdapter()
        binding.rvTasks.apply {
            adapter = taskListAdapter
        }
    }


    private fun observerTasks() {
        tasksMvvm.observeIncompleteTasksLiveData().observe(viewLifecycleOwner, Observer { tasks ->

            taskListAdapter.differ.submitList(tasks)


        })
    }

    private fun onBackButtonPressed(){
        //handle back button being pressed
        var backButtonPressed = 0
        val dispatcher = requireActivity().onBackPressedDispatcher
        dispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle back press in fragments.
                backButtonPressed++
                if (backButtonPressed >= 2){
                    val intent  = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)


                }else if(backButtonPressed < 2){
                    Toast.makeText(requireContext(), "press back again to exit", Toast.LENGTH_SHORT).show()

                    CoroutineScope(Dispatchers.IO).launch{
                        delay(3000)
                        backButtonPressed = 0
                    }
                }
            }
        })
    }


    private fun addTaskSetOnClickListener(){

        binding.bvAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_viewCurrentTasksFragment_to_createTaskFragment)
        }
    }
}