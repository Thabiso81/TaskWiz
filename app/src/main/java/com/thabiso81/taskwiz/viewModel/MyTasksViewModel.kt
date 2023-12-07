package com.thabiso81.taskwiz.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thabiso81.taskwiz.model.TaskModel

class MyTasksViewModel(): ViewModel() {
    /**** states that hold the data that will be presented to user ***/
    private var myTasksLiveData = MutableLiveData<TaskModel>()
    /**** states that hold the data that will be presented to user ***/


    /****** logic that gets the data from api or database *****/
    fun getTasks(){

    }
    /****** logic that gets the data from api or database *****/


    /*** methods that will observe states for any changes in their data **/
    fun observemyTasksLiveData(): LiveData<TaskModel> {
        return myTasksLiveData
        //returns the live data as soon as it changes.

        /* notice difference between LiveData<> and MutableLiveData<> */
    }
    /*** methods that will observe states for any changes in their data **/
}