package com.example.todomvvm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todomvvm.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao


    class Callback @Inject constructor(
        private val taskDatabase: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)


            val dao = taskDatabase.get().taskDao()

            //db operations to fire initially
            applicationScope.launch {
                dao.addTask(Task(name = "Wake up at 5 AM"))
                dao.addTask(Task(name = "Commit to git on daily basis"))
                dao.addTask(Task(name = "Associate android dev certification"))
                dao.addTask(Task(name = "Create a terrific linkedIN profile"))
                dao.addTask(Task(name = "Get a new job with 25K salary"))
                dao.addTask(Task(name = "Buy mac book"))
                dao.addTask(Task(name = "Be a complete app dev"))
            }

        }
    }
}