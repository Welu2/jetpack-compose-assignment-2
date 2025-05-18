package com.example.jetpack_compose_assignment_2

import PostDetailScreen
import PostsViewModel
import TodoListScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetpack_compose_assignment_2.ui.theme.Jetpackcomposeassignment2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jetpackcomposeassignment2Theme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    // Observe current route for debugging or UI changes
    val navBackStackEntry by navController.currentBackStackEntryAsState()




    NavHost(navController = navController, startDestination = "todo_list") {
        composable("todo_list") {
            TodoListScreen(
                onPostClick = { postId ->
                    navController.navigate("todo_detail/$postId") // ✅ pass the postId
                }
            )
        }
        composable("todo_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            val viewModel: PostsViewModel = viewModel()

            // Set the selected post
            postId?.let { viewModel.selectPostById(it) }

            PostDetailScreen(navController = navController, viewModel = viewModel)
        }

    }
}


