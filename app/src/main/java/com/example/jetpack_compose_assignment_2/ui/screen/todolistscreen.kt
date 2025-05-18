import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel

import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: PostsViewModel = viewModel(),
    onPostClick: (String) -> Unit
) {
    val posts by viewModel.posts.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    // Remember the SystemUiController to modify system bars
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, // Transparent status bar
            darkIcons = true // Dark icons for visibility
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Todo App",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Yellow
                )
            )
        }
    ) { paddingValues ->
        // Main UI content
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                posts.isNotEmpty() -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding() + 16.dp,
                            bottom = paddingValues.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(posts) { post ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Yellow)
                                    .clickable {
                                        viewModel.selectPost(post)
                                        onPostClick(post.id.toString())
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = post.completed,
                                    onCheckedChange = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = post.title)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Retry icon to manually retry loading posts
                        IconButton(
                            onClick = { viewModel.retryFetchPosts() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh, // Use the Material Icons refresh icon
                                contentDescription = "Retry",
                                tint = Color.Yellow
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        text = "No posts available.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}