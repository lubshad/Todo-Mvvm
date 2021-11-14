package com.example.todomvvm

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todomvvm.data.TaskDao
import com.example.todomvvm.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext appContext: Context,
        callback: TaskDatabase.Callback
    ) :TaskDatabase {
        return Room.databaseBuilder(appContext, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }


    @Provides
    fun provideTaskDao(taskDatabase: TaskDatabase): TaskDao = taskDatabase.taskDao()


    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
