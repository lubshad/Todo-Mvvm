package com.example.todomvvm.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todomvvm.data.TaskDatabase
import dagger.Module


@Module
object AppModule {
    fun provideTaskDatabase(
        app:Application,
        callback: TaskDatabase.Callback
    ) : TaskDatabase {
        return Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }
}