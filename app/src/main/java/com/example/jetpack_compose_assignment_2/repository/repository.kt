package com.example.jetpack_compose_assignment_2

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.jetpack_compose_assignment_2.data.AppDatabase
import com.example.jetpack_compose_assignment_2.data.Todo
import com.example.jetpack_compose_assignment_2.model.Todov
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp : Application() {

    companion object {
        lateinit var database: AppDatabase

        private val _postsLiveData = MutableLiveData<List<Todov>>()
        val postsLiveData: LiveData<List<Todov>> get() = _postsLiveData
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database.db"
        ).fallbackToDestructiveMigration().build()

        loadCachedData()
        fetchPostsFromApi()
    }

    private fun loadCachedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val cachedPosts = database.postDao().getAllTodo()
            val transformedPosts = cachedPosts.map { todo ->
                Todov(todo.id, todo.userId,  todo.title,todo.completed)
            }

            if (transformedPosts.isNotEmpty()) {
                _postsLiveData.postValue(transformedPosts)
                Log.d("ROOM_TEST", "Loaded ${transformedPosts.size} cached post(s)")
            } else {
                Log.d("ROOM_TEST", "No cached posts available")
            }
        }
    }

    private fun fetchPostsFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remotePosts = RetrofitClient.api.getTodo()

                val todoEntities = remotePosts.map { todov ->
                    Todo(todov.id, todov.userId,todov.title, todov.completed)
                }

                database.postDao().insertAll(todoEntities)
                val storedPosts = database.postDao().getAllTodo()

                val transformedPosts = storedPosts.map { todo ->
                    Todov(todo.id, todo.userId,todo.title, todo.completed)
                }

                _postsLiveData.postValue(transformedPosts)
                Log.d("ROOM_TEST", "Fetched and stored ${transformedPosts.size} post(s)")

            } catch (e: Exception) {
                Log.e("ROOM_TEST", "Error fetching posts from network", e)
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(applicationContext, "Network error. Showing cached data.", Toast.LENGTH_SHORT).show()
                }
                loadCachedData()
            }
        }
    }

    fun retryFetchPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remotePosts = RetrofitClient.api.getTodo()

                val todoEntities = remotePosts.map { todov ->
                    Todo(todov.id, todov.userId,todov.title,  todov.completed)
                }

                database.postDao().insertAll(todoEntities)
                val storedPosts = database.postDao().getAllTodo()

                val transformedPosts = storedPosts.map { todo ->
                    Todov(todo.id,todo.userId, todo.title,  todo.completed)
                }

                _postsLiveData.postValue(transformedPosts)
                Log.d("ROOM_TEST", "Retry successful. Fetched and stored ${transformedPosts.size} post(s)")
            } catch (e: Exception) {
                Log.e("ROOM_TEST", "Retry failed", e)
            }
        }
    }
}
