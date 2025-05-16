package com.example.mynotebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mynotebook.network.Post
import com.example.mynotebook.network.User
import com.example.mynotebook.viewmodel.MainViewModel
import com.example.mynotebook.viewmodel.PostDetailsViewModel
import com.example.mynotebook.viewmodel.UserDetailsViewModel
import com.example.mynotebook.network.UiState

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {

        composable("main") {
            MainScreen(
                navController = navController,
                onPostClick = { postId -> navController.navigate("postDetails/$postId") },
                onUserClick = { userId -> navController.navigate("userDetails/$userId") }
            )
        }

        composable("postDetails/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
            postId?.let {
                val viewModel: PostDetailsViewModel = viewModel(backStackEntry)
                PostDetailsScreen(postId = it, navController = navController, viewModel = viewModel)
            }
        }

        composable("userDetails/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            userId?.let {
                val viewModel: UserDetailsViewModel = viewModel(backStackEntry)
                UserDetailsScreen(userId = it, navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AppBar(
    title: String,
    navController: NavController,
    showBack: Boolean = false,
    showSettings: Boolean = false,
    showProfile: Boolean = false
) {
    Surface(
        color = Color(0xFFFEF7FF),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Cofnij",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else if (showSettings) {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Ustawienia",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            if (showProfile) {
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
    onPostClick: (Int) -> Unit,
    onUserClick: (Int) -> Unit
)
 {
    val postState by viewModel.postState.collectAsState()
    val userState by viewModel.userState.collectAsState()

    Scaffold(
        topBar = {
            AppBar(title = "Lista Postów", navController = navController)
        }
    ) { padding ->

        when {
            postState.isLoading || userState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            postState is UiState.Error || userState is UiState.Error -> {
                val message = (postState as? UiState.Error)?.message
                    ?: (userState as? UiState.Error)?.message
                    ?: "Błąd nieznany"

                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(message)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchData() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
            }

            postState is UiState.Success && userState is UiState.Success -> {
                val posts = (postState as UiState.Success).data
                val users = (userState as UiState.Success).data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(posts) { post ->
                        val user = users.find { it.id == post.userId }
                        PostItem(post, user,
                            onPostClick = { onPostClick(post.id) },
                            onUserClick = { user?.id?.let { onUserClick(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    user: User?,
    onPostClick: (Int) -> Unit,
    onUserClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick(post.id) }
            .padding(16.dp)
    ) {
        Text(text = post.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user?.name ?: "Nieznany użytkownik",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable(enabled = user != null) {
                user?.let { onUserClick(it.id) }
            }
        )
    }
}

@Composable
fun PostDetailsScreen(
    postId: Int,
    navController: NavController,
    viewModel: PostDetailsViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val post = posts.find { it.id == postId }


    LaunchedEffect(Unit) {
        if (posts.isEmpty()) {
            viewModel.fetchAllPosts()
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Szczegóły Posta",
                navController = navController,
                showBack = true
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage ?: "Wystąpił błąd")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchAllPosts() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
            }

            post != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "Tytuł:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Treść:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.body,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ID użytkownika: ${post.userId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nie znaleziono posta o ID: $postId")
                }
            }
        }
    }
}

@Composable
fun UserDetailsScreen(
    userId: Int,
    navController: NavController,
    viewModel: UserDetailsViewModel = viewModel()
) {
    val users by viewModel.users.collectAsState()
    val todos by viewModel.todos.collectAsState()
    val isLoadingUser by viewModel.isLoadingUser.collectAsState()
    val isLoadingTodos by viewModel.isLoadingTodos.collectAsState()
    val errorUser by viewModel.errorUser.collectAsState()
    val errorTodos by viewModel.errorTodos.collectAsState()
    val user = users.find { it.id == userId }
    val userTodos = todos.filter { it.userId == userId }

    LaunchedEffect(userId) {
        viewModel.fetchUser()
        viewModel.fetchTodos(userId)
    }

    Scaffold(
        topBar = {
            AppBar(title = "Szczegóły Użytkownika", navController = navController, showBack = true)
        }
    ) { padding ->
        when {
            isLoadingUser || isLoadingTodos -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorUser != null || errorTodos != null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        errorUser?.let {
                            Text(text = it)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        errorTodos?.let {
                            Text(text = it)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.fetchUser()
                            viewModel.fetchTodos(userId)
                        }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
            }

            user != null -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    item {
                        Text("Imię i nazwisko:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(user.name, style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Nazwa użytkownika:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(user.username, style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Email:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(user.email, style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Telefon:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(user.phone, style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Strona:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(user.website, style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Firma:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(user.company?.name ?: "Brak danych", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Adres:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "${user.address?.street ?: ""} ${user.address?.suite ?: ""}, " +
                                    "${user.address?.city ?: ""}, ${user.address?.zipcode ?: ""}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(thickness = 2.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Lista zadań:", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(userTodos) { todo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = todo.completed,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(todo.title, style = MaterialTheme.typography.bodyLarge)
                        }
                        Divider()
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nie znaleziono użytkownika o ID: $userId")
                }
            }
        }
    }
}