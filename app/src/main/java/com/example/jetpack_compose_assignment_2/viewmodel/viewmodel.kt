import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpack_compose_assignment_2.MyApp
import com.example.jetpack_compose_assignment_2.data.Todo
import com.example.jetpack_compose_assignment_2.model.Todov

import kotlinx.coroutines.launch

class PostsViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Todov>>() // Changed to use Todov data model
    val posts: LiveData<List<Todov>> get() = _posts

    private val _selectedPost = MutableLiveData<Todov?>() // Changed to use Todov data model
    val selectedPost: LiveData<Todov?> get() = _selectedPost

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        loadPosts() // Changed from fetchPosts()
    }

    /**
     * Load cached data first, then fetch from network and update cache
     */
    private fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Step 1: Load cached posts immediately (as Todov model)
            val cachedPosts = MyApp.database.postDao().getAllTodo()
            if (cachedPosts.isNotEmpty()) {
                // Transform Todo entities to Todov models for UI use
                val transformedPosts = cachedPosts.map { todo ->
                    Todov(todo.id, todo.userId,todo.title, todo.completed) // Map Todo to Todov
                }
                _posts.value = transformedPosts
            }

            // Step 2: Try to fetch from the network
            try {
                val todos = RetrofitClient.api.getTodo()
                _posts.value = todos // Update UI with fresh data
                // Map Todov model (API response) to Todo entities for Room DB
                val todoEntities = todos.map { todov ->
                    Todo(todov.id, todov.userId,todov.title, todov.completed) // Insert as Todo entities
                }
                MyApp.database.postDao().insertAll(todoEntities) // Save to DB
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load posts: ${e.message}"
                // Keep showing cached data, don't clear _posts
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryFetchPosts() {
        loadPosts() // Retry from network but still load cache first
    }

    fun selectPost(post: Todov) {
        _selectedPost.value = post
    }

    fun selectPostById(postId: String) {
        val post = _posts.value?.find { it.id.toString() == postId }
        _selectedPost.value = post
    }
}