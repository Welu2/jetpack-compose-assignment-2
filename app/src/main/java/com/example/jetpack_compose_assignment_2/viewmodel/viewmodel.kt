import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpack_compose_assignment_2.MyApp
import com.example.jetpack_compose_assignment_2.data.Todo



import kotlinx.coroutines.launch

class PostsViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Todo>>()
    val posts: LiveData<List<Todo>> get() = _posts

    private val _selectedPost = MutableLiveData<Todo?>()
    val selectedPost: LiveData<Todo?> get() = _selectedPost

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

            // Step 1: Load cached posts immediately
            val cachedPosts = MyApp.database.postDao().getAllTodo()
            if (!cachedPosts.isNullOrEmpty()) {
                _posts.value = cachedPosts
            }

            // Step 2: Try to fetch from the network
            try {
                val todos = RetrofitClient.api.getTodo()
                _posts.value = todos // update UI with fresh data
                MyApp.database.postDao().insertAll(todos) // update local DB
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

    fun selectPost(post: Todo) {
        _selectedPost.value = post
    }

    fun selectPostById(postId: String) {
        val post = _posts.value?.find { it.id.toString() == postId }
        _selectedPost.value = post
    }
}
