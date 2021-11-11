package com.example.todomvvm.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.todomvvm.ui.tasks.SortBy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "Preferences Manger"

@Singleton
class PreferenceManger @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
    private val dataStore = context.dataStore
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
            Log.i(TAG, exception.toString())

        }
        .map { preferences ->
        val sortOrder = SortBy.valueOf(preferences[PreferencesKeys.SORT_BY] ?: SortBy.BY_DATE.name)
        val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
        FilterPreferences(sortOrder, hideCompleted)
    }


    suspend fun updateSortOrder(sortOrder : SortBy) {
        dataStore.edit { preferences ->
            val currentCounterValue = preferences[EXAMPLE_COUNTER] ?: 0
            preferences[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

    companion object PreferencesKeys {
        val SORT_BY = stringPreferencesKey("sort_by")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }
}


data class FilterPreferences(
    val sortBy: SortBy,
    val hideCompleted: Boolean,
)