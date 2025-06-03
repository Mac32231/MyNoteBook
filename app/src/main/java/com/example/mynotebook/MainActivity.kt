package com.example.mynotebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.mynotebook.ui.theme.MyNotebookTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.MapProperties
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.GoogleMap
import com.example.mynotebook.viewmodel.DarkThemeViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkThemeViewModel: DarkThemeViewModel = viewModel()
            val isDarkTheme = darkThemeViewModel.isDarkTheme.collectAsState().value

            MyNotebookTheme(darkTheme = isDarkTheme) {
                Surface {
                    val navController = rememberNavController()

                    val locationPermission = rememberMultiplePermissionsState(
                        permissions = listOf(
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION"
                        )
                    )

                    LaunchedEffect(locationPermission.permissions) {
                        locationPermission.launchMultiplePermissionRequest()
                    }

                    AppNavGraph(
                        navController = navController,
                        darkThemeViewModel = darkThemeViewModel // przekazanie dalej
                    )
                }
            }
        }
    }
}