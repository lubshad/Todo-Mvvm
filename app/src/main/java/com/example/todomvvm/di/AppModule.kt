package com.example.todomvvm.di

import android.app.Application
import androidx.room.Room
import com.example.todomvvm.data.TaskDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
object AppModule {
    fun provideTaskDatabase(
        app: Application,
        callback: TaskDatabase.Callback,
    ): TaskDatabase {
        return Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }
}