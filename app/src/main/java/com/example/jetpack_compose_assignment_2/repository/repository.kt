package com.example.jetpack_compose_assignment_2

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import com.example.jetpack_compose_assignment_2.data.AppDatabase
import com.example.jetpack_compose_assignment_2.data.Todo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp : Application() {

    companion object {
        lateinit var database: AppDatabase

        private val _postsLiveData = MutableLiveData<List<Todo>>()
        val postsLiveData: LiveData<List<Todo>> get() = _postsLiveData
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database.db"
        ).fallbackToDestructiveMigration()
            .build()

        // Show cached data on app start
        loadCachedData()

        // Fetch and store posts from API
        fetchPostsFromApi()
    }

    private fun loadCachedData() {
        CoroutineScope(Dispatchers.IO).launch {
            val cachedPosts = database.postDao().getAllTodo()
            if (cachedPosts.isNotEmpty()) {
                _postsLiveData.postValue(cachedPosts)
                Log.d("ROOM_TEST", "Loaded ${cachedPosts.size} cached post(s)")
            } else {
                Log.d("ROOM_TEST", "No cached posts available")
            }
        }
    }

    private fun fetchPostsFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Attempt to fetch remote posts
                val remotePosts = RetrofitClient.api.getTodo()

                // Save posts to database
                database.postDao().insertAll(remotePosts)
                val storedPosts = database.postDao().getAllTodo()

                // Update LiveData to refresh UI
                _postsLiveData.postValue(storedPosts)

                Log.d("ROOM_TEST", "Fetched and stored ${storedPosts.size} post(s)")

            } catch (e: Exception) {
                Log.e("ROOM_TEST", "Error fetching posts from network", e)

                // Handle error gracefully
                CoroutineScope(Dispatchers.Main).launch {
                    // Optionally, you can show a retry prompt or a Toast here
                    Toast.makeText(applicationContext, "Network error. Showing cached data.", Toast.LENGTH_SHORT).show()
                }

                // Optionally, load cached data in case of failure
                loadCachedData()
            }
        }
    }

    // Optional: Retry fetching posts after a failure
    fun retryFetchPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Retry fetching remote posts
                val remotePosts = RetrofitClient.api.getTodo()
                database.postDao().insertAll(remotePosts)
                val storedPosts = database.postDao().getAllTodo()
                _postsLiveData.postValue(storedPosts)
                Log.d("ROOM_TEST", "Retry successful. Fetched and stored ${storedPosts.size} post(s)")
            } catch (e: Exception) {
                Log.e("ROOM_TEST", "Retry failed", e)
            }
        }
    }
}
