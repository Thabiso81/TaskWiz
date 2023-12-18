package com.thabiso81.taskwiz.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.adapters.TaskListAdapter
import com.thabiso81.taskwiz.database.TaskDatabase
import com.thabiso81.taskwiz.databinding.FragmentViewCurrentTasksBinding
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.activities.LoginActivity
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class ViewCurrentTasksFragment : Fragment(), TaskListAdapter.OnCheckboxClickListener {
    private var _binding: FragmentViewCurrentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ViewTasksViewModel
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

        setupUi()
        instantiateDatabaseAndViewModel()

        //get the list of tasks
        viewModel.getIncompleteTasks()
        observerTasks()

        prepareReyclerView()

        addTaskSetOnClickListener()

        onTaskSwipe()

        countCompleteTasks()

        onTasksComplete()

        onBackButtonPressed()

        return view
    }

    private fun setupUi() {
        binding.lytNoTasks.visibility = View.GONE
        setUpUI_Invisibile()
    }


    private fun instantiateDatabaseAndViewModel(){
        val taskDatabase = TaskDatabase.getInstance(requireContext())
        val viewModelFactory = ViewTasksViewModelFactory(taskDatabase)
        viewModel = ViewModelProvider(this, viewModelFactory)[ViewTasksViewModel::class.java]
    }

    private fun prepareReyclerView() {
        taskListAdapter = TaskListAdapter(this)
        binding.rvTasks.apply {
            adapter = taskListAdapter

        }


    }


    private fun observerTasks() {

        viewModel.observeIncompleteTasksLiveData().observe(viewLifecycleOwner, Observer { tasks ->

            if (tasks.isNullOrEmpty()){
                onNoTasksAvailable()
            }else{
                onTasksAvailable()
                taskListAdapter.differ.submitList(tasks)
            }
        })

    }

    private fun onTaskSwipe() {

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(

            ItemTouchHelper.UP or ItemTouchHelper.DOWN,     //the directions the recyclerview scrolls in
            ItemTouchHelper.LEFT       //direction that you want your item to swipe to
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true //use this if you want something to happen when we scroll in recyclerview

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition //get the position of viewHolder item being swiped
                val task = taskListAdapter.differ.currentList[position]
                viewModel.deleteTask(taskListAdapter.differ.currentList[position])

                Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG).setAction(
                    "undo",
                    View.OnClickListener {
                        viewModel.insertTask(task)
                        if (position == 0) taskListAdapter.notifyDataSetChanged()
                    }
                ).show()
            }

        }

        //attach the itemTouchHelper to our recyclerview
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvTasks)
    }

    private fun countCompleteTasks() {
        //viewModel.obser
    }

    override fun onCheckboxClick(task: TaskModel) {
        task.completionStatus = "Complete"
        viewModel.insertTask(task)

        Snackbar.make(binding.rvTasks, "Task completed", Snackbar.LENGTH_LONG).setAction(
            "undo",
            View.OnClickListener {
                task.completionStatus = "Incomplete"
                viewModel.insertTask(task)
            }
        ).show()
    }

    private fun onTasksComplete() {

    }

    private fun setUpUI_Invisibile(){
        binding.edtRemainingTasks.visibility = View.GONE
        binding.edtRemainingTasks.visibility = View.GONE
        binding.lytDivider.visibility = View.GONE
    }

    private fun setUpUI_Visibile(){
        binding.edtRemainingTasks.visibility = View.VISIBLE
        binding.edtRemainingTasks.visibility = View.VISIBLE
        binding.lytDivider.visibility = View.VISIBLE
    }
    private fun onNoTasksAvailable() {

        binding.rvTasks.visibility = View.GONE
        setUpUI_Visibile()
        binding.lytNoTasks.visibility = View.VISIBLE
        binding.lytNoTasks.startAnimation(
            AnimationUtils.loadAnimation(
                binding.lytNoTasks.context, R.anim.slide_in
            )
        )
        binding.lottiAnimation.playAnimation()

    }


    private fun onTasksAvailable() {
        binding.rvTasks.visibility = View.VISIBLE
        setUpUI_Invisibile()
        binding.lottiAnimation.pauseAnimation()
        binding.lytNoTasks.visibility = View.GONE
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
                    /*val intent  = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)*/

                    exitProcess(1)


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