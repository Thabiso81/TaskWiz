package com.thabiso81.taskwiz.view.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.thabiso81.taskwiz.R
import com.thabiso81.taskwiz.adapters.DisplayTaskListAdapter
import com.thabiso81.taskwiz.database.relations.TaskWithChecklist
import com.thabiso81.taskwiz.databinding.FragmentViewCurrentTasksBinding
import com.thabiso81.taskwiz.model.TaskChecklistModel
import com.thabiso81.taskwiz.model.TaskModel
import com.thabiso81.taskwiz.view.activities.MainActivity
import com.thabiso81.taskwiz.view.fragments.bottomSheet.TaskEditBottomSheetFragment
import com.thabiso81.taskwiz.viewModel.viewTasksViewModel.ViewTasksViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.system.exitProcess


class ViewCurrentTasksFragment : Fragment(), DisplayTaskListAdapter.OnCheckboxClickListener, DisplayTaskListAdapter.OnTaskClickListener {
    private var _binding: FragmentViewCurrentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var  viewModel: ViewTasksViewModel

    private lateinit var displayTaskListAdapter: DisplayTaskListAdapter

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
        viewModel.getIncompleteTasks()

        observerTasks()

        prepareReyclerView()

        addTaskSetOnClickListener()

        onTaskSwipe()

        onBackButtonPressed()

        return view
    }

    private fun SetupUi() {
        binding.lytNoTasks.visibility = View.GONE

    }


    private fun instantiateDatabaseAndViewModel(){
        viewModel = (activity as MainActivity).viewModel
    }

    private fun prepareReyclerView() {
        displayTaskListAdapter = DisplayTaskListAdapter(this, this)
        binding.rvTasks.apply {
            adapter = displayTaskListAdapter

        }


    }


    private fun observerTasks() {

        viewModel.observeIncompleteTasksLiveData().observe(viewLifecycleOwner, Observer { tasks ->


            if (!tasks.isNullOrEmpty()){
                onTasksAvailable()

                //display (number of incompleteTasks and total number of Tasks)
                val totalTasks = tasks.size
                var completeTasks = 0

                for(task in tasks){
                    if(task.task.completionStatus.equals("complete"))
                        completeTasks++
                }

                binding.edtRemainingTasks.text = "$completeTasks/$totalTasks complete"

                displayTaskListAdapter.differ.submitList(tasks.toMutableList())

            }else{
                onNoTasksAvailable()
                binding.edtRemainingTasks.text = "All done"
            }
        })

    }

    private fun onTaskSwipe() {
    //confugure swipe gesture for recyclerview
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
                val position = viewHolder.bindingAdapterPosition //get the position of viewHolder item being swiped
                val task = displayTaskListAdapter.differ.currentList[position].task
                viewModel.deleteTask(displayTaskListAdapter.differ.currentList[position].task)

                Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG).setAction(
                    "undo",
                    View.OnClickListener {
                        binding.lytNoTasks.startAnimation(
                            AnimationUtils.loadAnimation(
                                binding.lytNoTasks.context, com.thabiso81.taskwiz.R.anim.slide_out
                            )
                        )
                            CoroutineScope(Dispatchers.IO).launch{
                                viewModel.insertTask(task)
                            }

                            if (position == 0) displayTaskListAdapter.notifyDataSetChanged()

                    }
                ).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                //Code attribution
                //https://github.com/xabaras/RecyclerViewSwipeDecorator

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(ContextCompat.getColor(requireContext(), com.thabiso81.taskwiz.R.color.delete_color2))
                    .addSwipeLeftActionIcon(com.thabiso81.taskwiz.R.drawable.ic_delete)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

        //attach the itemTouchHelper to our recyclerview
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvTasks)
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()


    private fun onNoTasksAvailable() {

        binding.rvTasks.visibility = View.GONE
        binding.edtRemainingTasks.text = "All done"
        binding.lytNoTasks.visibility = View.VISIBLE
        binding.lytNoTasks.startAnimation(
            AnimationUtils.loadAnimation(
                binding.lytNoTasks.context, com.thabiso81.taskwiz.R.anim.slide_in
            )
        )
        binding.lottiAnimation.playAnimation()

    }


    private fun onTasksAvailable() {
        binding.lytNoTasks.visibility = View.GONE
        binding.rvTasks.visibility = View.VISIBLE
        binding.lottiAnimation.pauseAnimation()
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
            findNavController().navigate(com.thabiso81.taskwiz.R.id.action_viewCurrentTasksFragment_to_createTaskFragment)
        }
    }

/******************** callbacks ***********************************************/
//callback for when task checkbox is checked on recyclerview
    override fun onCheckboxClick(task: TaskModel, view: CheckBox) {
        task.completionStatus = "Complete"


        CoroutineScope(Dispatchers.IO).launch{
            viewModel.insertTask(task)
        }

        Snackbar.make(binding.rvTasks, "Task completed", Snackbar.LENGTH_LONG).setAction(
            "undo",
            View.OnClickListener {
                view.isChecked = false

                binding.lytNoTasks.startAnimation(
                    AnimationUtils.loadAnimation(
                        binding.lytNoTasks.context, com.thabiso81.taskwiz.R.anim.slide_out
                    )
                )
                task.completionStatus = "Incomplete"

                CoroutineScope(Dispatchers.IO).launch{
                    viewModel.insertTask(task)
                }

            }
        ).show()





    }

    //callback for when task is clicked on recyclerview
    override fun onTaskClick(task: TaskModel, checklist: List<TaskChecklistModel>) {
        //instantiate bottom sheet Fragment and pass the taskId as an argument

        findNavController().navigate(ViewCurrentTasksFragmentDirections.
        actionViewCurrentTasksFragmentToTaskEditBottomSheetFragment(
            task,
            checklist.toTypedArray()))
    }

}