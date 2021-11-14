package com.example.todomvvm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todomvvm.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao


    class Callback @Inject constructor(
        private val taskDatabase: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = taskDatabase.get().taskDao()

            applicationScope.launch {
                taskDao.addTask(Task("Wash the dishes"))
                taskDao.addTask(Task("Do the laundry"))
                taskDao.addTask(Task("Buy groceries", important = true))
                taskDao.addTask(Task("Prepare food", completed = true))
                taskDao.addTask(Task("Call mom"))
                taskDao.addTask(Task("Visit grandma", completed = true))
                taskDao.addTask(Task("Repair my bike"))
                taskDao.addTask(Task("Call Elon Musk"))
            }

        }
    }

}