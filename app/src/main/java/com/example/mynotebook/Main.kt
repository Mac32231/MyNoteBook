package com.example.mynotebook

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mynotebook.network.Post
import com.example.mynotebook.network.User
import com.example.mynotebook.viewmodel.MainViewModel
import com.example.mynotebook.viewmodel.PostDetailsViewModel
import com.example.mynotebook.viewmodel.UserDetailsViewModel
import com.example.mynotebook.viewmodel.ProfileViewModel
import com.example.mynotebook.network.UiState
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.mynotebook.viewmodel.DarkThemeViewModel
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode




@Composable
fun AppNavGraph(navController: NavHostController, darkThemeViewModel: DarkThemeViewModel) {
    NavHost(navController = navController, startDestination = "main",) {

        composable("main") {
            MainScreen(
                navController = navController,
                darkThemeViewModel = darkThemeViewModel,
                onPostClick = { postId -> navController.navigate("postDetails/$postId") },
                onUserClick = { userId -> navController.navigate("userDetails/$userId") }
            )
        }

        composable("postDetails/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
            postId?.let {
                val viewModel: PostDetailsViewModel = viewModel(backStackEntry)
                PostDetailsScreen(postId = it, navController = navController, viewModel = viewModel, darkThemeViewModel = darkThemeViewModel)
            }
        }

        composable("userDetails/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            userId?.let {
                val viewModel: UserDetailsViewModel = viewModel(backStackEntry)
                UserDetailsScreen(userId = it, navController = navController, viewModel = viewModel, darkThemeViewModel = darkThemeViewModel)
            }
        }
        composable("yourDetails") {
            val viewModel: ProfileViewModel = viewModel()
            YourDetailsScreen(navController = navController, viewModel = viewModel, darkThemeViewModel = darkThemeViewModel)


        }
    }
}

@Composable
fun AppBar(
    title: String,
    navController: NavController,
    showBack: Boolean = false,
    showSettings: Boolean = false,
    showProfile: Boolean = false,
    showThemeToggle: Boolean = false,
    darkThemeViewModel: DarkThemeViewModel
) {
    val isDark by darkThemeViewModel.isDarkTheme.collectAsState()


    Surface(
        color = MaterialTheme.colorScheme.primary,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 🌙 Ikonka motywu – NA LEWO
                if (showBack) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Cofnij",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
             else if (showSettings) {
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Ustawienia",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

                if (showThemeToggle) {
                    IconButton(onClick = { darkThemeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Przełącz motyw",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }



            // Tytuł
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Prawa strona – np. profil
            if (showProfile) {
                IconButton(onClick = { navController.navigate("yourDetails") }) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = MaterialTheme.colorScheme.onPrimary
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
    onUserClick: (Int) -> Unit,
    darkThemeViewModel: DarkThemeViewModel
)
{
    val postState by viewModel.postState.collectAsState()
    val userState by viewModel.userState.collectAsState()


    Scaffold(
        topBar = {
            AppBar(title = "Lista Postów", navController = navController, showProfile = true, darkThemeViewModel = darkThemeViewModel, showThemeToggle = true)
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
                    items(posts , key = { it.id }) { post ->
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
    viewModel: PostDetailsViewModel = viewModel(),
    darkThemeViewModel: DarkThemeViewModel
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
                showBack = true,
                darkThemeViewModel = darkThemeViewModel,
                showThemeToggle = true
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
fun YourDetailsScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    darkThemeViewModel: DarkThemeViewModel
) {
    val currentName by viewModel.name.collectAsState()
    val currentSurname by viewModel.surname.collectAsState()
    val currentPhotoPath by viewModel.photoPath.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    // Parsowanie zapisanego ścieżki String do Uri
    LaunchedEffect(currentName, currentSurname, currentPhotoPath) {
        name.value = currentName
        surname.value = currentSurname
        selectedImageUri.value =
            if (currentPhotoPath.isNotBlank()) Uri.parse(currentPhotoPath) else null
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Log.d("ImagePicker", "Selected URI: $uri")
        if (uri != null) {
            selectedImageUri.value = uri
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = if (isEditing) "Edytuj swoje dane" else "Twoje Dane",
                navController = navController,
                showBack = true,
                darkThemeViewModel = darkThemeViewModel,
                showThemeToggle = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .clickable(enabled = isEditing) { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                LoadImageFromUri(
                    uri = selectedImageUri.value,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            if (isEditing) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Imię") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = surname.value,
                    onValueChange = { surname.value = it },
                    label = { Text("Nazwisko") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        name.value = currentName
                        surname.value = currentSurname
                        selectedImageUri.value =
                            if (currentPhotoPath.isNotBlank()) Uri.parse(currentPhotoPath) else null
                        isEditing = false
                    }) {
                        Text("Anuluj")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(onClick = {
                        viewModel.saveData(
                            name.value,
                            surname.value,
                            selectedImageUri.value?.toString() ?: ""
                        )
                        isEditing = false
                    }) {
                        Text("Zapisz")
                    }
                }
            } else {
                Text(
                    "Imię: ${currentName.ifBlank { "-" }}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Nazwisko: ${currentSurname.ifBlank { "-" }}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = { isEditing = true }) {
                    Text("Edytuj")
                }
            }
        }
    }
}

@Composable
fun LoadImageFromUri(uri: Uri?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    bitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                Log.e("ImageLoad", "Błąd ładowania obrazu: ${e.message}")
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Zdjęcie profilowe",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Domyślny profil",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
    }
}

@Composable
fun UserDetailsScreen(
    userId: Int,
    navController: NavController,
    viewModel: UserDetailsViewModel = viewModel(),
    darkThemeViewModel: DarkThemeViewModel
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
            AppBar(title = "Szczegóły Użytkownika", navController = navController, showBack = true, darkThemeViewModel = darkThemeViewModel, showThemeToggle = true)
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mapa
                        user.address?.geo?.let { geo ->
                            val lat = geo.lat.toDoubleOrNull()
                            val lng = geo.lng.toDoubleOrNull()
                            Log.d("MAPA", "lat=$lat, lng=$lng")

                            if (lat != null && lng != null) {
                                val location = LatLng(lat, lng)
                                val cameraPositionState = rememberCameraPositionState()
                                LaunchedEffect(user) {
                                    user?.address?.geo?.let { geo ->
                                        val lat = geo.lat.toDoubleOrNull()
                                        val lng = geo.lng.toDoubleOrNull()
                                        if (lat != null && lng != null) {
                                            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 12f)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                GoogleMap(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp),
                                    cameraPositionState = cameraPositionState,
                                    properties = MapProperties(
                                        minZoomPreference = 1f,
                                        maxZoomPreference = 10f
                                    ),
                                    uiSettings = MapUiSettings(
                                        zoomControlsEnabled = true,
                                        zoomGesturesEnabled = true
                                ) ){
                                    Marker(
                                        state = MarkerState(position = location),
                                        title = user.name,
                                        snippet = "Tutaj mieszka"
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

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


