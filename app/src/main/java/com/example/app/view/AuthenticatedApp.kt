package com.example.app.view

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.viewmodel.AuthViewModel

@Composable
fun AuthenticatedApp(
    navController: NavController,
    authViewModel: AuthViewModel? = null
) {
    val sharedAuthViewModel = authViewModel ?: viewModel()
    val isAuthenticated by sharedAuthViewModel.isAuthenticated.observeAsState()
    val hasExistingUser = sharedAuthViewModel.hasExistingUser()

    LaunchedEffect(isAuthenticated, hasExistingUser) {
        when {
            isAuthenticated == true -> {
                navController.navigate(AppScreen.PokemonExplorer.route) {
                    popUpTo(AppScreen.Welcome.route) { inclusive = true }
                }
            }
            hasExistingUser -> {
                navController.navigate(AppScreen.Login.route) {
                    popUpTo(AppScreen.Welcome.route) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(AppScreen.Welcome.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }
}
