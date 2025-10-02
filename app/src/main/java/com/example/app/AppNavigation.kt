package com.example.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.view.HomeScreen

sealed class AppScreen(val route: String){
    object HomeScreen : AppScreen("home_screen")
    object MainScreen : AppScreen("main_screen")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = AppScreen.HomeScreen.route ){
        composable(route = AppScreen.HomeScreen.route){
            HomeScreen(navController = navController)
        }

        composable(route = AppScreen.MainScreen.route){
            MainScreen(navController = navController)
        }
    }
}