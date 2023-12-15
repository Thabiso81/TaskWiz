package com.thabiso81.taskwiz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thabiso81.taskwiz.model.TaskModel

@Database(entities = [TaskModel::class], version = 5) //version 5 is already used up
@TypeConverters(TaskTypeConverter::class)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object{
        var INSTANCE: TaskDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TaskDatabase{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    TaskDatabase::class.java,
                    "task.db"
                ).fallbackToDestructiveMigration() //upon new version, this tells room to create a new database but keep the data
                    .build()
            }
            return INSTANCE as TaskDatabase
        }
    }
}