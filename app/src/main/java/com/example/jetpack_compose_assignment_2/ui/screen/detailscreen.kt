import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    viewModel: PostsViewModel = viewModel()
) {
    val post by viewModel.selectedPost.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            // Show a loading indicator while the data is being fetched
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (post != null) {
                val p = post!!
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "ID: ${p.id}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "User ID: ${p.userId}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Title:", style = MaterialTheme.typography.bodyLarge)
                    Text(text = p.title, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Completed: ${p.completed}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text("No post selected", modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
