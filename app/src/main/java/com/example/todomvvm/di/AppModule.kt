package com.example.todomvvm.di

import android.app.Application
import androidx.room.Room
import com.example.todomvvm.data.TaskDao
import com.example.todomvvm.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        app: Application,
        callback: TaskDatabase.Callback,
    ): TaskDatabase {
        return Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao = db.taskDao()


    @ApplicationScope
    @Provides
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob())
}


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope